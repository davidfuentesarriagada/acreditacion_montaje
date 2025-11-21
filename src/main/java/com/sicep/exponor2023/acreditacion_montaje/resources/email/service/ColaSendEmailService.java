package com.sicep.exponor2023.acreditacion_montaje.resources.email.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.dao.ColaSendEmailRepository;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.dao.EmailBaseRepository;
import com.sicep.exponor2023.acreditacion_montaje.util.FormatoFecha;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilFecha;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilString;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.ColaSendEmail;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailBase;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.scheduler.SchedulerSendEmail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ColaSendEmailService {
	private final ColaSendEmailRepository colaSendEmailRepository;
	private final SchedulerSendEmail schedulerSendEmail;
	private final EmailBaseRepository emailBaseRepository;
	
	public boolean start() {
		// indicador de cola corriendo
		ColaSendEmail cola = colaSendEmailRepository.findAll().get(0);
		if (cola.getEstado().equals(ColaSendEmail.Estado.PROCESANDO) 
				|| cola.getEstado().equals(ColaSendEmail.Estado.PAUSA))
			return false;
		
		log.info("inicio de cola");
		cola.setEstado(ColaSendEmail.Estado.PROCESANDO);
		cola.setFechaEstado(UtilFecha.ahora());
		colaSendEmailRepository.save(cola);
		
		// scheduler
		schedulerSendEmail.start();
		
		return true;
	}
	
	public boolean pause() {
		ColaSendEmail cola = colaSendEmailRepository.findAll().get(0);
		cola.setEstado(ColaSendEmail.Estado.PAUSA);
		cola.setFechaEstado(UtilFecha.ahora());
		colaSendEmailRepository.save(cola);
		
		schedulerSendEmail.detenerNuevosEnvios();
		
		return true;
	}
	
	public boolean reanude() {
		// indicador de cola corriendo
		ColaSendEmail cola = colaSendEmailRepository.findAll().get(0);
		if (cola.getEstado().equals(ColaSendEmail.Estado.PROCESANDO))
			return false;
		
		log.info("inicio de cola");
		cola.setEstado(ColaSendEmail.Estado.PROCESANDO);
		cola.setFechaEstado(UtilFecha.ahora());
		colaSendEmailRepository.save(cola);
		
		// scheduler
		schedulerSendEmail.start();
		return true;
	}
	
	public ColaSendEmail getColaSendEmail() {
		List<ColaSendEmail> lcola = colaSendEmailRepository.findAll();
		if (lcola.isEmpty())
			return null;
		
		return lcola.get(0);
	}
	
	
	public List<Map<String, Object>> getListEmailEnCola() {
		List<Map<String, Object>> resultado = new ArrayList<>();
		// ordenamiento por prioridad
		Sort sort = Sort.by("numeroPrioridad").ascending().and(Sort.by("fechaRegistro").ascending());
		for (EmailBase email : emailBaseRepository.findAll(sort)) {
			Map<String, Object> map = new HashMap<>();
			resultado.add(map);
			map.put("id", email.getId());
			map.put("subject", email.getSubject());;
			map.put("destinatarios", email.getDestinatariosTo());
			map.put("numeroPrioridad", email.getNumeroPrioridad());
			map.put("fechaRegistro", UtilString.fecha(email.getFechaRegistro(), FormatoFecha.sdfDMYHMS));
			map.put("cantidadDestinatariosTotal", email.getCantidadDestinatariosTotal());
			map.put("enviando", email.isEnviando());
		}
		
		return resultado;
	}

	public List<Map<String, Object>> deleteById(long idEmailBase) {
		Optional<EmailBase> optional = emailBaseRepository.findById(idEmailBase);
		if (optional.isPresent()) {
			EmailBase email = optional.get();
			if (!email.isEnviando()) {
				emailBaseRepository.delete(email);
			}
		}
		return getListEmailEnCola();
	}

	public void inicializar() {
		ColaSendEmail cola = getColaSendEmail();
		// si no existe cola, se crea
		if (cola == null) {
			colaSendEmailRepository.save(new ColaSendEmail());
			return;
		}
		
		// si cola existe y su estado indica procesando, se debe cambiar a cola pausada
		if (cola.getEstado().equals(ColaSendEmail.Estado.PROCESANDO)) {
			cola.setEstado(ColaSendEmail.Estado.PAUSA);
			cola.setFechaEstado(UtilFecha.ahora());
			colaSendEmailRepository.save(cola);
		}
		
		// quitar la marca de procesando a cada email base
		emailBaseRepository.clearEnviando();
	}
}
