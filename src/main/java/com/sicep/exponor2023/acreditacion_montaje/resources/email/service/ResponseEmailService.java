package com.sicep.exponor2023.acreditacion_montaje.resources.email.service;

import java.util.ArrayList;
import java.util.List;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailBase;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.dao.EmailBaseRepository;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ResponseEmailService {
	protected EmailErrorService emailErrorService;
	protected EmailBaseRepository emailBaseRepository;
	
	/**
	 * permite filtrado de lista de suscripcion y de error previo a ser enviado.
	 * por defecto, se realizado filtrado por lista de error
	 * @param email
	 * @return
	 */
	public boolean isValid(EmailBase email) {
		String to = email.getDestinatariosTo().toLowerCase();
		if (to.contains(",")) {
			List<String> lto = new ArrayList<>();
			for (String destinatario : to.split(",")) {
				if (!emailErrorService.exists(destinatario))
					lto.add(destinatario);
			}
			
			// si no quedan destinatarios, se rechaza el envio
			if (lto.isEmpty())
				return false;
			
			String updated = String.join(",", lto);
			
			// si no se filtra ningun correo, se da el visto bueno
			if (to.equals(updated))
				return true;
			
			// se registran cambios en el listado de destinatarios
			email.setDestinatariosTo(updated);
			emailBaseRepository.save(email);
		}
		// verificacion de destinatario unico
		else if (emailErrorService.exists(to))
			return false;
		return true;
	}
	
	/**
	 * permite asignar parametros y valores de componentes de spring como url del sistema
	 * @param email
	 */
	public void preSend(EmailBase email) {
		
	}
	
	public void sent(EmailBase email) {
		log.info("Email para: {} con título '{}' enviado correctamente", email.getDestinatariosTo(), email.getSubject());
	}
	public void error(EmailBase email, Exception e) {
		log.error(String.format("Email para: %s con título '%s' no pudo ser enviado", email.getDestinatariosTo(), email.getSubject()), e);

		// agregando destinatarios a la lista de error
		InternetAddress[] laddress;
		try {
			laddress = InternetAddress.parse(email.getDestinatariosTo());
		}
		catch (AddressException ex) {
			emailErrorService.add(email.getDestinatariosTo(), email.getSubject(), ex.getMessage());
			return;
		}
		
		for (InternetAddress address : laddress)
			emailErrorService.add(address.getAddress(), email.getSubject(), e.getMessage());
	
	}
}
