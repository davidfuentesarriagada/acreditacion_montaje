package com.sicep.exponor2023.acreditacion_montaje.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailBase;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.ResponseEmailService;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.SendEmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SendEmailJavaService implements SendEmailService {
	@Autowired
	private JavaMailSender emailSender;
	@Autowired
	private ResponseEmailService responseEmailService;

	@Override
	public boolean sendEmail(EmailBase email) {
		try {
			// comprobacion de lista de error y suscripcion
			// si email es considerado como no valido, no se arroja error para que no quede marcado como error y solo se descarte
			if (!responseEmailService.isValid(email))
				return true;
			
			// agregando parametros para link de desuscripcion y otros en caso de requerirse
			responseEmailService.preSend(email);
			
			//if (true) throw new ServiceLayerException("Prueba local");// TODO quitar
			
			// creacion del email en bruto (aun no se envia)
			MimeMessage mimeMessage = email.getMimeMessage();
			emailSender.send(mimeMessage);
			
			responseEmailService.sent(email);
			return true;
		}
	    catch (Exception e) {
        	responseEmailService.error(email, e);
	    }
		return false;
	}

}
