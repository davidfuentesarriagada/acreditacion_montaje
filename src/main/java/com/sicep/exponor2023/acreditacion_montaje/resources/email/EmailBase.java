package com.sicep.exponor2023.acreditacion_montaje.resources.email;

import java.util.Date;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class EmailBase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;
	protected String subject;

	protected boolean enviando = false;
	protected int numeroPrioridad; // menor numero -> mayor prioridad
	@Column(length = 550)
	protected String destinatariosTo;// principal(es) destinatarios. 
	@Setter(AccessLevel.NONE)
    protected int cantidadDestinatariosTotal;// debe incluir to, cc y bcc
	@Temporal(TemporalType.TIMESTAMP)
	protected Date fechaRegistro;

	@Transient
	protected String sender;
	@Transient
	protected String[] testEmailTo;
	@Transient
	private TemplateEngine templateEngine = null;

	public abstract MimeMessage getMimeMessage() throws ServiceLayerException;

	protected TemplateEngine getTemplateEngine() {
		if (templateEngine == null) {
			ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
			resolver.setTemplateMode(TemplateMode.HTML);
			resolver.setCharacterEncoding("UTF-8");
			resolver.setPrefix("/templates/");
			resolver.setSuffix(".html");
			
			templateEngine = new SpringTemplateEngine();
			templateEngine.setTemplateResolver(resolver);
		}
		
		return templateEngine;
	}
	
	protected Multipart generateMainMultiPart(String process) throws MessagingException {
		// main content
		Multipart mainMultipart = new MimeMultipart("mixed");
		
		// content html y text
		Multipart htmlAndTextMultipart = new MimeMultipart("alternative");
		
		// body html
		BodyPart htmlBodyPart = new MimeBodyPart();
		
		// template a body tml
		htmlBodyPart.setContent(process, "text/html; charset=UTF-8");
		htmlAndTextMultipart.addBodyPart(htmlBodyPart);
		
		// add html y text to main content
		BodyPart htmlAndTextBodyPart = new MimeBodyPart();
		htmlAndTextBodyPart.setContent(htmlAndTextMultipart);
		mainMultipart.addBodyPart(htmlAndTextBodyPart);
		
		return mainMultipart;
	}

}
