package com.sicep.exponor2023.acreditacion_montaje.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Personal;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketGeneratorService {
	@Value("#{'${propiedad.carpetaLocal}'.concat('/qr')}")
	private String carpetaQr;
	@Value("#{'${propiedad.carpetaLocal}'.concat('/tickets')}")
	private String carpetaTickets;
	private final float porcAnchoQr = 2.5f / 10;
	private final float porcAnchoInfoPersonal = 1f - porcAnchoQr;
	private final int anchoTicket = 1050;
	private final int altoTicket = 315;
	
	public File generateTicket(Personal personal) throws ServiceLayerException {
		try {
			//BufferedImage imgTicket = new BufferedImage(895, 375, BufferedImage.TYPE_BYTE_BINARY);
			BufferedImage imgTicket = new BufferedImage(anchoTicket, altoTicket, BufferedImage.TYPE_BYTE_BINARY);
			Graphics2D g = imgTicket.createGraphics();
			
			g.setColor(Color.WHITE);
	        g.fillRect(0, 0, imgTicket.getWidth(), imgTicket.getHeight());
			
	        // codigo qr
        	insertaCodigoQr(g, personal.getCodigo(), imgTicket.getWidth(), imgTicket.getHeight());

			// nombre
        	insertaInfoNombre(g, personal.getNombre(), imgTicket.getWidth(), imgTicket.getHeight(), 0);
			// empresa
        	String nombreEmpresas = personal.getListaExpositor().stream().map(e -> e.getNombre()).collect(Collectors.joining(", "));
        	
        	insertaInfoEmpresa(g, nombreEmpresas, imgTicket.getWidth(), imgTicket.getHeight(), imgTicket.getHeight() * 1 / 3);
			// codigo
        	//insertaInfoPersonal(g, personal.getCodigo(), imgTicket.getWidth(), imgTicket.getHeight(), imgTicket.getHeight() * 2 / 3);

			// generado el archivo resultado
			g.dispose();
			String ticketFilename = "ticket_" + personal.getCodigo() + ".png";
			File resultado = new File(carpetaTickets, ticketFilename);
			ImageIO.write(imgTicket, "png", resultado);
			return resultado;
		}
		catch (IOException e) {
			throw new ServiceLayerException("Error en la creacion de las imágenes");
		}
	}

	private void insertaCodigoQr(Graphics2D g, String codigo, int anchoImagen, int altoImagen) {
		// si no hay mesa y es invitado de honor se inserta una imagen de estrella en la ubicacion
		BufferedImage imgQr = getBufferedCodigoQr(codigo);// 200 x 200
		
		int xImgTxt = (int)((anchoImagen * porcAnchoQr - imgQr.getWidth()) / 2);// de izq a derecha
		int yImgTxt = (int)((((altoImagen * 7 / 10) - imgQr.getHeight()) / 2));// de arriba hacia abajo
		g.drawImage(imgQr, xImgTxt, yImgTxt, null);

		// texto codigo
		int tamFuente = 40;
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.PLAIN, tamFuente));
		FontMetrics fm = g.getFontMetrics();
		int anchoTexto = fm.stringWidth(codigo);
		//int altoTexto = (int) (tamFuente*0.71);
		
		xImgTxt = (int)(((anchoImagen * porcAnchoQr) - anchoTexto) / 2);
		yImgTxt = (int)(altoImagen * 8 / 10); // coordenada y para el texto de la mesa
		g.drawString(codigo, xImgTxt, yImgTxt);

	}
	
	private void insertaInfoEmpresa(Graphics2D g, String texto, int anchoImagen, int altoImagen, int yBaseSuperior) {
		texto = texto.toUpperCase();
		int tamFuente = 55;
		String linea1 = texto;
		int anchoTexto1 = 0;
		String linea2 = null;
		int anchoTexto2 = 0;
		while(true) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, tamFuente));
			FontMetrics fm = g.getFontMetrics();
			anchoTexto1 = fm.stringWidth(linea1);
			// achicando fuente hasta calzar texto dentro del espacio correspondiente 
			//(en el area izquierda se inserta qr)
			if (anchoTexto1 <= (anchoImagen * porcAnchoInfoPersonal))
				break;
			if (tamFuente > 35)
				tamFuente-= 5;
			else
				linea1 = linea1.substring(0, linea1.length() - 5);
		}
		
		// si corta texto, se agrega en la segunda linea
		if (linea1.length() < texto.length()) {
			// busqueda de ultimo espacio en la linea 1 para corte
			int iEsp = linea1.lastIndexOf(" ");
			// no hubo espacios en la linea 1, se corta texto
			if (iEsp == -1) {
				linea2 = texto.substring(linea1.length() + 1);
			}
			// hubo espacios, se corta desde el espacio
			else {
				linea1 = texto.substring(0, iEsp);
				linea2 = texto.substring(iEsp + 1, texto.length());
			}
			
			//log.info("{}", linea1);
			// ajuste de la linea 2
			while(true) {
				g.setColor(Color.BLACK);
				g.setFont(new Font("Arial", Font.PLAIN, tamFuente));
				FontMetrics fm = g.getFontMetrics();
				anchoTexto2 = fm.stringWidth(linea2);
				// acrotando texto hasta calzar dentro del espacio correspondiente 
				//(en el area izquierda se inserta qr)
				if (anchoTexto2 <= (anchoImagen * porcAnchoInfoPersonal))
					break;
				linea2 = linea2.substring(0, linea2.length() - 5);
			}
			//log.info("{}", linea2);
			
			// recalculo de anchos
			FontMetrics fm = g.getFontMetrics();
			anchoTexto1 = fm.stringWidth(linea1);
			anchoTexto2 = fm.stringWidth(linea2);
		}// end if se corta el texto en linea 1
		
		
		//log.info("texto = {}, ancho = {}, fuente = {}", texto, anchoTexto, tamFuente);
		
		int altoTexto = (int) (tamFuente*0.71);
		
		// insercion de texto dependiente de lineas a insertar
		if (linea2 == null) {
			int xImgTxt = (int)((anchoImagen * porcAnchoQr) + (((anchoImagen * porcAnchoInfoPersonal) - anchoTexto1) / 2));
			int yImgTxt = (int)(yBaseSuperior +((altoImagen - yBaseSuperior - altoTexto) / 2));// dentro de sus 2 tercios de altura desde arriba hacia abajo
			g.drawString(linea1, xImgTxt, yImgTxt);
		}
		else {
			int xImgTxt1 = (int)((anchoImagen * porcAnchoQr) + (((anchoImagen * porcAnchoInfoPersonal) - anchoTexto1) / 2));
			int yImgTxt1 = (int)(yBaseSuperior + ((float)(altoImagen - yBaseSuperior) / 3) - ((float) altoTexto / 2));// dentro de su tercio de altura desde arriba hacia abajo
			g.drawString(linea1, xImgTxt1, yImgTxt1);

			int xImgTxt2 = (int)((anchoImagen * porcAnchoQr) + (((anchoImagen * porcAnchoInfoPersonal) - anchoTexto2) / 2));
			int yImgTxt2 = (int)(yBaseSuperior + ((float)(altoImagen - yBaseSuperior) * (2.0 / 3)) - ((float)altoTexto / 2));// dentro de su tercio de altura desde arriba hacia abajo
			g.drawString(linea2, xImgTxt2, yImgTxt2);
		}
	}
	
	private void insertaInfoNombre(Graphics2D g, String texto, int anchoImagen, int altoImagen, int yBaseSuperior) {
		texto = texto.toUpperCase();
		int tamFuente = 55;
		int anchoTexto;
		while(true) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, tamFuente));
			FontMetrics fm = g.getFontMetrics();
			anchoTexto = fm.stringWidth(texto);
			// achicando fuente hasta calzar texto dentro del espacio correspondiente 
			//(en el area izquierda se inserta la mesa en un cuadrado )
			if (anchoTexto <= (anchoImagen * porcAnchoInfoPersonal))
				break;
			if (tamFuente > 35)
				tamFuente-= 5;
			else
				texto = texto.substring(0, texto.length() - 5);
		}
		//log.info("texto = {}, ancho = {}, fuente = {}", texto, anchoTexto, tamFuente);
		
		int altoTexto = (int) (tamFuente*0.71);
		
		int xImgTxt = (int)((anchoImagen * porcAnchoQr) + (((anchoImagen * porcAnchoInfoPersonal) - anchoTexto) / 2));
		int yImgTxt = (int)(yBaseSuperior + (((altoImagen / 3) - altoTexto) / 2) + altoTexto);// dentro de su tercio de altura desde arriba hacia abajo
		g.drawString(texto, xImgTxt, yImgTxt);
	}
	
	private BufferedImage getBufferedCodigoQr(String codigo) {
		try {
			BufferedImage buff1 = ImageIO.read(new File(carpetaQr, String.format("qr_%s.png", codigo)));
			
			Image tmp = buff1.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
			BufferedImage dimg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2d = dimg.createGraphics();
			g2d.drawImage(tmp, 0, 0, null);
			g2d.dispose();

			return dimg;
		}
		catch (IOException e) {
			log.error("Error en la creacion de las imagenes con código QR", e);
			return null;
		}
	}
	
	public void convertToPdf(Personal personal) throws ServiceLayerException {
		String codigo = personal.getCodigo();
		File pdfFile = new File(carpetaTickets, String.format("ticket_%s.pdf", codigo));
		// si el pdf existe, no hacer nada
		if (pdfFile.exists())
			return;
		
		// se requiere crear el png primero
		File imageFile = new File(carpetaTickets, String.format("ticket_%s.png", codigo));
		if (!imageFile.exists()) {
			// creando el ticket
			generateTicket(personal);
			imageFile = new File(carpetaTickets, String.format("ticket_%s.png", codigo));
		}
		
		try {
			// generando el pdf si no existe
			FileOutputStream fos = new FileOutputStream(pdfFile);
			com.itextpdf.text.Image image1 = com.itextpdf.text.Image.getInstance(imageFile.getPath());
			image1.setAbsolutePosition(0, 0);
		    Document document = new Document(image1);
		    PdfWriter writer = PdfWriter.getInstance(document, fos);
		    writer.open();
		    document.open();

		    document.add(image1);
		    document.close();
		    writer.close();
		}
		catch(IOException | DocumentException e) {
			log.error(e.getMessage());
			throw new ServiceLayerException("No pudo ser generado el ticket pdf para "+codigo);
		}
	}
	
	public void eliminar(Personal personal) {
		File pdfFile = new File(carpetaTickets, String.format("ticket_%s.pdf", personal.getCodigo()));
		if (pdfFile.exists() && !pdfFile.delete()) {
			// no pudo eliminarse
		}
		
		File imageFile = new File(carpetaTickets, String.format("ticket_%s.png", personal.getCodigo()));
		if (imageFile.exists() && !imageFile.delete()) {
			// no pudo eliminarse
		}
		
	}
	
}
