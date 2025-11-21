package com.sicep.exponor2023.acreditacion_montaje.resources.email.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sicep.exponor2023.acreditacion_montaje.util.UtilFecha;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilString;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailError;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.dao.EmailErrorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmailErrorService {
	private final EmailErrorRepository emailErrorRepository;
	
	// caso cuando se encuentra en un listado de destinatarios (emai1,email2, etc..)
	public boolean exists(String email) {
		return emailErrorRepository.existsByDestinatario(email);
	}
	
	public List<EmailError> list() {
		return emailErrorRepository.findAll();
	}
	
	public List<EmailError> deleteById(long idEmailError) {
		Optional<EmailError> optional = emailErrorRepository.findById(idEmailError);
		if (optional.isPresent()) {
			EmailError emailError = optional.get();
			emailErrorRepository.delete(emailError);
		}
		return list();
	}

	public EmailError add(String destinatario, String subject, String observaciones) {
		EmailError emailError = new EmailError();
		emailError.setDestinatario(destinatario);
		emailError.setFechaIntentoEnvio(UtilFecha.ahora());
		emailError.setObservaciones(UtilString.truncate(observaciones));
		emailError.setSubject(UtilString.truncate(subject));
		emailErrorRepository.save(emailError);
		return emailError;
	}

	// caso cuando se encuentra en un listado de destinatarios (emai1,email2, etc..)
	public List<EmailError> findByDestinatario(String destinatario) {
		return emailErrorRepository.findByDestinatario(destinatario);
	}

	public List<EmailError> deleteByDestinatario(String destinatario) {
		destinatario = destinatario.toLowerCase();
		List<EmailError> lemail = emailErrorRepository.findByDestinatario(destinatario);
		for (EmailError email : lemail) {
			// identificacion si destinatario unico, inicial, intermedio o final
			if (email.getDestinatario().equalsIgnoreCase(destinatario))
				emailErrorRepository.delete(email);
			else {
				String destinatarios = email.getDestinatario().toLowerCase();
				String updated = destinatarios
						.replaceAll("^"+destinatario+",", "")// inicia
						.replaceAll(","+destinatario+"$", "")// finaliza
						.replaceAll(","+destinatario+",", ",")// intermedio
						;
				email.setDestinatario(updated);
				emailErrorRepository.save(email);
			}
		}
		
		return list();
	}
}
