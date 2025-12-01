package com.sicep.exponor2023.acreditacion_montaje.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Personal;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;

@Service
@Transactional
public class QrGeneratorService {
	private static final Logger log = LoggerFactory.getLogger(QrGeneratorService.class);

	@Value("#{'${propiedad.carpetaLocal}'.concat('/qr')}")
	private String carpetaQr;
	@Value("#{'${propiedad.carpetaLocal}'.concat('/plantilla')}")
	private String carpetaPlantilla;
	@Value("${propiedad.url.server}")
	private String urlServer;

	@Autowired
	private ResourceLoader resourceLoader;
	
	private BufferedImage getBufferedImage() throws ServiceLayerException {
		Resource resource = resourceLoader.getResource("classpath:static/images/base_logo.jpg");
		InputStream inputStream = null;
		try {
			inputStream = resource.getInputStream();
			return ImageIO.read(inputStream);
		}
		catch (IOException e) {
			throw new ServiceLayerException("Error en la creacion de las imagenes con código QR", e);
		}
		finally {
			try {
				if (inputStream != null)
					inputStream.close();
			}
			catch (IOException ignore) {
			}
		}
	}

	public void generateQRCode(Personal personal) throws ServiceLayerException {
		String rutaQR = String.format("%s/qr_%s.png", carpetaQr, personal.getCodigo());
		File pngQr = new File(rutaQR);
		if (pngQr.exists())
			return;
		
		try {
			// obtencion de las dimensiones del qr basadas en la plantilla
			BufferedImage imgPlantilla = getBufferedImage();

			int heightPlantilla = imgPlantilla.getHeight();
			int widthPlantilla = imgPlantilla.getWidth();
			int sideCodeQR = heightPlantilla;
			if (heightPlantilla > widthPlantilla)
				sideCodeQR = widthPlantilla;
			sideCodeQR = (int) (sideCodeQR * 0.49);// ancho y largo del cuadro QR

			String enlaceCodigo = String.format("%s/personal/%s/ver", urlServer, personal.getCodigo());// ej /personal/P22S/ver
			//log.info(enlaceCodigo);
			
			getImage(enlaceCodigo, rutaQR, sideCodeQR);
		}
		catch (IOException | WriterException e) {
			throw new ServiceLayerException("Error en la creacion de las imágenes con código QR");
		}
	}

	public void generatePlantilla(Personal personal) throws ServiceLayerException {
		String rutaQR = String.format("%s/qr_%s.png", carpetaQr, personal.getCodigo());
		String rutaResultado = String.format("%s/plantilla_%s.jpg", carpetaPlantilla, personal.getCodigo());
		
		try {
			// obtencion de las dimensiones del qr basadas en la plantilla
			BufferedImage imgPlantilla = getBufferedImage();

			int heightPlantilla = imgPlantilla.getHeight();
			int widthPlantilla = imgPlantilla.getWidth();
			int sideCodeQR = heightPlantilla;
			if (heightPlantilla > widthPlantilla)
				sideCodeQR = widthPlantilla;
			sideCodeQR = (int) (sideCodeQR * 0.49);// ancho y largo del cuadro QR

			File pngQr = new File(rutaQR);
			if (!pngQr.exists()) {
				generateQRCode(personal);
				pngQr = new File(rutaQR);
			}
			BufferedImage imgQr = ImageIO.read(pngQr);

			Graphics2D g = imgPlantilla.createGraphics();
			// agregando la segunda imagen en la plantilla
			g.drawImage(imgQr, (widthPlantilla - sideCodeQR) / 2, (int) (heightPlantilla * 0.45), null);
			// agregando el nombre del personal
			String nombre = personal.getNombre().toUpperCase();
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 24));
			FontMetrics fm = g.getFontMetrics();
			int anchoTexto = fm.stringWidth(nombre);
			
			g.drawString(nombre, ((widthPlantilla - anchoTexto) / 2), (int) (heightPlantilla * 0.43));

			g.setFont(new Font("Arial", Font.BOLD, 26));
			fm = g.getFontMetrics();
			anchoTexto = fm.stringWidth(personal.getCodigo());
			g.drawString(personal.getCodigo(), ((widthPlantilla - anchoTexto) / 2), (int) (heightPlantilla * 0.93));

			g.dispose();

			// generado el archivo resultado: QR + plantilla
			File resultado = new File(rutaResultado);
			ImageIO.write(imgPlantilla, "jpg", resultado);
		}
		catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ServiceLayerException("Error en la creación de las imágenes con código QR");
		}
	}

	private File getImage(String texto, String rutaPNGdestino, int altoAncho) throws IOException, WriterException {
		Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 0); // default = 4

		BitMatrix matrix = new MultiFormatWriter().encode(texto, BarcodeFormat.QR_CODE, altoAncho, altoAncho, hints);
		File file = new File(rutaPNGdestino);
		String extension = rutaPNGdestino.substring(rutaPNGdestino.lastIndexOf('.') + 1);
		MatrixToImageWriter.writeToPath(matrix, extension, file.toPath());
		return file;
	}
	
	
	public void eliminar(Personal personal) {
		String rutaResultado = String.format("%s/plantilla_%s.jpg", carpetaPlantilla, personal.getCodigo());
		File pngPlantilla = new File(rutaResultado);
		if (pngPlantilla.exists() && !pngPlantilla.delete()) {
			// error con la eliminacion
		}
	}

	public void eliminarQr(Personal personal) {
		String rutaResultado = String.format("%s/qr_%s.png", carpetaQr, personal.getCodigo());
		File pngPlantilla = new File(rutaResultado);
		if (pngPlantilla.exists() && !pngPlantilla.delete()) {
			// error con la eliminacion
		}
	}

}
