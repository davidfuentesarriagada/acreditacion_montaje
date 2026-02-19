package com.sicep.exponor2023.acreditacion_montaje.domain.email;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.thymeleaf.context.Context;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailBase;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.TokenEmailDesuscripcion;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlantillaEmail {
	public static final String nombreEvento = "Exponor 2026";
	public static final String emailDesuscripcion = "desuscripciones@sicep.cl";
	
	public static void addDesuscripcion(MimeMessage mimeMessage, String modulo) throws MessagingException {
		addDesuscripcion(mimeMessage, modulo, null, null, null, null);
	}
	
	public static void addDesuscripcion(MimeMessage mimeMessage, String modulo, String destinatario, String urlServer, String contextPath, Context context) throws MessagingException {
		if (destinatario != null && urlServer != null && context != null) {
			// generando el link de desuscripcion
			TokenEmailDesuscripcion token = new TokenEmailDesuscripcion();
			token.setModulo(modulo);
			token.setDestinatario(destinatario);
			token.setSubject(mimeMessage.getSubject());
			String urlDesuscripcion = String.format("%s%s/EmailDesuscritoController/add/%s", urlServer, contextPath, token.getToken());
			mimeMessage.addHeader("List-Unsubscribe", String.format(
					"<%s>,<mailto:%s?subject=%s - %s>",
					urlDesuscripcion,
					emailDesuscripcion,
					nombreEvento,
					modulo));
			mimeMessage.addHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click");
			context.setVariable("link_desuscripcion", urlDesuscripcion);
			context.setVariable("nombre_evento", nombreEvento);
		}
		else {
			mimeMessage.addHeader("List-Unsubscribe", String.format(
					"<mailto:%s?subject=%s - %s>",
					emailDesuscripcion,
					nombreEvento,
					modulo
					));
			mimeMessage.addHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click");
		}
	}
	
	public static void addEncabezado(Class<? extends EmailBase> classEmail, Multipart multipart) {
		InputStream inputStream = null;
		try {
			MimeBodyPart imagePart = new MimeBodyPart();
			// recurso en el proyecto
			inputStream = classEmail.getResourceAsStream("/static/images/email/encabezado.jpg");
			byte[] fileContent = IOUtils.toByteArray(inputStream);
			DataSource fds = new ByteArrayDataSource(fileContent, "image/jpeg");
			imagePart.setDataHandler(new DataHandler(fds));
			imagePart.setContentID("<encabezado.jpg>");
			imagePart.setDisposition(MimeBodyPart.INLINE);
			multipart.addBodyPart(imagePart);
			
		}
		catch (MessagingException | IOException e) {
			log.error(e.getMessage(), e);
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		
	}


}
