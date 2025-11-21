package com.sicep.exponor2023.acreditacion_montaje.service;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.ResponseEmailService;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilFecha;
import com.sicep.exponor2023.acreditacion_montaje.dao.PersonalRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.email.EmailAcreditacion;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Personal;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailBase;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailConUrlServer;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.dao.EmailBaseRepository;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.EmailErrorService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.EmailDesuscritoService;

@Service
@Transactional
@Slf4j
public class ResponseEmailMontajeService extends ResponseEmailService {
	private final EmailDesuscritoService emailDesuscritoService;
	private final PersonalRepository personalRepository;
	@Value("${propiedad.url.server}")
	private String urlServer;
	@Value("#{'${propiedad.carpetaLocal}'.concat('/plantilla')}")
	private String carpetaPlantilla;
	
	public ResponseEmailMontajeService(
			EmailErrorService emailErrorService, // OBLIGATORIO
			EmailBaseRepository emailBaseRepository, // OBLIGATORIO
			EmailDesuscritoService emailDesuscritoService,
			PersonalRepository personalRepository
			) {
		this.emailErrorService = emailErrorService;
		this.emailBaseRepository = emailBaseRepository;
		this.emailDesuscritoService = emailDesuscritoService;
		this.personalRepository = personalRepository;
	}

	@Override
	public boolean isValid(EmailBase email) {
		// verificacion si email se encuentra en lista de error
		if (!super.isValid(email))
			return false;

		// verificacion de lista de suscripcion en caso de modulo afectado
		if (email instanceof EmailAcreditacion) {
			String to = email.getDestinatariosTo().toLowerCase();
			if (emailDesuscritoService.exists(to, "ACREDITACION_MONTAJE"))
				return false;
		} // end if email de tipo anuncio afectado por suscripcion

		return true;
	}

	@Override
	public void sent(EmailBase email) {
		super.sent(email);
		if (email instanceof EmailAcreditacion) {
			EmailAcreditacion emailAcreditacion = (EmailAcreditacion) email;
			
			for (Personal personal :  emailAcreditacion.getListaPersonal()) {
				personal.setEnvioCredencialesDate(UtilFecha.ahora());
				personalRepository.save(personal);
			}
		}
	}

	@Override
	public void error(EmailBase email, Exception e) {
		super.error(email, e);
	}

	@Override
	public void preSend(EmailBase email) {
		super.preSend(email);
		// agregar url sistema
		if (email instanceof EmailConUrlServer)
			((EmailConUrlServer) email).setUrlServer(urlServer);
		if (email instanceof EmailAcreditacion) {
			EmailAcreditacion emailAcreditacion = (EmailAcreditacion) email;
			emailAcreditacion.setCarpetaQr(carpetaPlantilla);
			List<Personal> listaPersonal = personalRepository.findByListaExpositor(emailAcreditacion.getExpositor());
			emailAcreditacion.setListaPersonal(listaPersonal);
		}
	}
	
}
