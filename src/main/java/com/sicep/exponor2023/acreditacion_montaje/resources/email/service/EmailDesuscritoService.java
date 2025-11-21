package com.sicep.exponor2023.acreditacion_montaje.resources.email.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilEncryptEncode;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilFecha;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailDesuscrito;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.TokenEmailDesuscripcion;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.dao.EmailDesuscritoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmailDesuscritoService {
	//private final int diasVigenciaToken = 60;
	private final EmailDesuscritoRepository emailDesuscritoRepository;

	public EmailDesuscrito addByDestinatario(String destinatario) {
		// busqueda en bd para no repetir 
		if (emailDesuscritoRepository.existsByDestinatarioIgnoreCase(destinatario))
			return null;
		
		// validacion expiracion de token (no implementado porque eventos tienen una vida limitada)
		
		EmailDesuscrito email = new EmailDesuscrito();
		email.setDestinatario(destinatario);
		email.setFechaDesuscripcion(UtilFecha.ahora());
		email.setModulo("manual");
		email.setSubject("MANUAL");
		
		emailDesuscritoRepository.save(email);
		return email;
	}
	
	public EmailDesuscrito add(String tokenEncrypted) throws ServiceLayerException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = UtilEncryptEncode.decodeAndDecrypt(tokenEncrypted, TokenEmailDesuscripcion.secret);
			TokenEmailDesuscripcion token = mapper.readValue(json, TokenEmailDesuscripcion.class);
			
			// busqueda en bd para no repetir 
			if (emailDesuscritoRepository.existsByModuloAndDestinatarioIgnoreCase(token.getModulo(), token.getDestinatario()))
				throw new ServiceLayerException("Error intentando desuscribir");
			
			// validacion expiracion de token (no implementado porque eventos tienen una vida limitada)
			
			EmailDesuscrito email = new EmailDesuscrito();
			email.setDestinatario(token.getDestinatario());
			email.setFechaDesuscripcion(UtilFecha.ahora());
			email.setModulo(token.getModulo());
			email.setSubject(token.getSubject());
			email.setFechaToken(token.getFechaTokenDate());
			
			emailDesuscritoRepository.save(email);
			return email;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		throw new ServiceLayerException("Error intentando desuscribir");
	}
	
	// list
	public List<EmailDesuscrito> list() {
		return emailDesuscritoRepository.findAll();
	}

	public boolean exists(String email) {
		return emailDesuscritoRepository.existsByDestinatarioIgnoreCase(email);
	}
	
	public boolean exists(String email, String modulo) {
		return emailDesuscritoRepository.existsByModuloAndDestinatarioIgnoreCase(modulo, email);
	}
	
	// caso cuando se encuentra en un listado de destinatarios (emai1,email2, etc..)
	public List<EmailDesuscrito> findByDestinatario(String destinatario) {
		return emailDesuscritoRepository.findByDestinatarioIgnoreCase(destinatario);
	}

	public List<EmailDesuscrito> deleteByDestinatario(String destinatario) {
		emailDesuscritoRepository.deleteByDestinatarioIgnoreCase(destinatario);
		return list();
	}
	
}
