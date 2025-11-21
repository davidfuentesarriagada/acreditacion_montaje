package com.sicep.exponor2023.acreditacion_montaje.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.dao.EmailBaseRepository;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.ColaSendEmailService;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.EmailErrorService;
import com.sicep.exponor2023.acreditacion_montaje.dao.PersonalRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.email.EmailAcreditacion;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Expositor;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Personal;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilFecha;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {
	private final EmailErrorService emailErrorService;
	private final EmailBaseRepository emailBaseRepository;
	private final ColaSendEmailService colaSendEmailService;
	private final QrGeneratorService qrGeneratorService;
	private final PersonalRepository personalRepository;

	public void sendByExpositor(Expositor expositor) throws ServiceLayerException {
		if (emailErrorService.exists(expositor.getEmail()))
			return;
		
		// creacion de la plantilla qr en caso de no existir
		List<Personal> listaPersonal = personalRepository.findByListaExpositor(expositor);
		for (Personal personal : listaPersonal)
			qrGeneratorService.generatePlantilla(personal);
		
		// registro del email en la cola bd 
		EmailAcreditacion email = new EmailAcreditacion(expositor);
		email.setNumeroPrioridad(emailBaseRepository.previousPrimeraPrioridad());
		email.setFechaRegistro(UtilFecha.ahora());
		emailBaseRepository.saveAndFlush(email);

		// inicio de cola en caso de no estar en procesamiento
		colaSendEmailService.start();
	}

}
