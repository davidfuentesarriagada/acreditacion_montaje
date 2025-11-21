package com.sicep.exponor2023.acreditacion_montaje.domain.email;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.thymeleaf.context.Context;

import com.sicep.exponor2023.acreditacion_montaje.domain.email.PlantillaEmail;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Expositor;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Personal;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailBase;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailConUrlServer;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
@Slf4j
public class EmailAcreditacion extends EmailBase implements EmailConUrlServer {
	@ManyToOne(optional = false)
	private Expositor expositor;
	@Transient
	private String urlServer;
	@Transient
	private String carpetaQr;
	@Transient
	private List<Personal> listaPersonal;

	public EmailAcreditacion() {
	}

	public EmailAcreditacion(Expositor expositor) throws ServiceLayerException {
		this.expositor = expositor;
		this.subject = PlantillaEmail.nombreEvento + " - Credenciales acreditación montaje";
		
		MimeMessage mimeMessage = generarMimeMessage();
		// asignacion y calculo de destinatarios en el objeto
		try {
			this.destinatariosTo = Arrays.toString(mimeMessage.getRecipients(Message.RecipientType.TO)).replace("[", "").replace("]", "");
			this.cantidadDestinatariosTotal = mimeMessage.getAllRecipients().length;
		}
		catch (MessagingException e) {
			throw new ServiceLayerException(e.getMessage(), e);
		}
	}

	private MimeMessage generarMimeMessage() throws ServiceLayerException {
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage mimeMessage = new MimeMessage(session);

		try {
			// to
			// test email obtiene su valor solamente al momento de envio y no en la creacion del email
			if (testEmailTo != null) {
				for (String emailTo : testEmailTo)
					mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
				this.destinatariosTo = Arrays.toString(testEmailTo).replace("[", "").replace("]", "");
			}
			else {
				mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(expositor.getEmail()));
			}

			return mimeMessage;
		}
		catch (MessagingException e) {
			throw new ServiceLayerException(e.getMessage(), e);
		}
	}

	@Override
	public MimeMessage getMimeMessage() throws ServiceLayerException {
		try {
			MimeMessage mimeMessage = generarMimeMessage();
			mimeMessage.setSubject(subject);
			mimeMessage.setFrom(new InternetAddress(sender, PlantillaEmail.nombreEvento));
			
			// atributos del template de email (lo unico que varia en este email del evento)
			Context context = new Context();
			context.setVariable("nombreEmpresa", expositor.getNombre());
			context.setVariable("listaPersonal", listaPersonal);
			
			// agregando al email acceso a desuscripcion
			PlantillaEmail.addDesuscripcion(mimeMessage, "ACREDITACION_MONTAJE", expositor.getEmail(), urlServer, "/",context);
			
			// conversion de la plantilla
			String process = getTemplateEngine().process("email/acreditacion", context);

			Multipart mainMultipart = generateMainMultiPart(process);
			for (Personal personal :  listaPersonal)
				addPlantillaQr(mainMultipart, personal);
			mimeMessage.setContent(mainMultipart);
			return mimeMessage;
		}
		catch (MessagingException | IOException e) {
			throw new ServiceLayerException(e.getMessage(), e);
		}
	}

	private void addPlantillaQr(Multipart multipart, Personal personal) {
		InputStream inputStream = null;
		try {
			MimeBodyPart imagePart = new MimeBodyPart();
			// generar automaticamente qr en caso de no existir
			String rutaCodigoQrLocal = carpetaQr + "/plantilla_" + personal.getCodigo() + ".jpg";
			
			imagePart.attachFile(rutaCodigoQrLocal);
			imagePart.setFileName(personal.getCodigo() + ".jpg");
			imagePart.setContentID("<"+personal.getCodigo() + ".jpg>");
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
