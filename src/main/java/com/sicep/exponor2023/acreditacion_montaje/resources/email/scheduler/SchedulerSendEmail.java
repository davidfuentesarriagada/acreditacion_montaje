package com.sicep.exponor2023.acreditacion_montaje.resources.email.scheduler;

import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.dao.ColaSendEmailRepository;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.dao.EmailBaseRepository;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilFecha;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.ColaSendEmail;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailBase;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.SendEmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SchedulerSendEmail {
	@Value("${propiedad.carpetaLocal}")
	private String raizCarpetaLocal;
	@Value("${propiedad.email.from}")
	private String sender;
	@Value("${propiedad.test.email.to:#{null}}")
	private String[] testEmailTo;

	private ScheduledFuture<?> taskNextSend = null;
	@Value("${propiedad.email.msIntervaloEmail}")
	private int msIntervaloEmail;

	private final EmailBaseRepository emailBaseRepository;
	private final ColaSendEmailRepository colaSendEmailRepository;
	private final SendEmailService sendEmailService;
	private final TaskScheduler taskScheduler;
	
	public void start() {
		taskNextSend = taskScheduler.schedule(this::sendNextEmail, UtilFecha.getFechaMilisegundosDespues(UtilFecha.ahora(), msIntervaloEmail).toInstant());
	}
	
	public void sendNextEmail() {
		// si hay un email en proceso de envio, no se busca siguiente email (la tarea original se encargara de programar nuevo envio)
		if (emailBaseRepository.existsByEnviandoTrue())
			return;

		// buscando el siguiente email
		EmailBase email = emailBaseRepository.findTopByEnviandoFalseOrderByNumeroPrioridadAscIdAsc();
		// Cola libre si la lista esta vacia
		if (email == null) {
			ColaSendEmail cola = colaSendEmailRepository.findAll().get(0);
			cola.setEstado(ColaSendEmail.Estado.LIBRE);
			cola.setFechaEstado(UtilFecha.ahora());
			colaSendEmailRepository.save(cola);
			log.info("fin de la cola");
			return;
		}

		// pre envio del email : estado enviando
		email.setEnviando(true);
		emailBaseRepository.save(email);

		// envio del email
		// parametros properties
		email.setSender(sender);
		//Habilitar para para envio de prueba
		//email.setTestEmailTo(testEmailTo);
		email.setTestEmailTo(new String[] {email.getDestinatariosTo()});
		boolean ok = sendEmailService.sendEmail(email);

		// eliminacion del email de la cola
		emailBaseRepository.delete(email);

		// programando siguiente envio (intervalo dependiente de cantidad de destinatarios recien enviados)
		
		// calculo del intervalo para el siguiente envio
		int milisegundosDespues;
		if (ok)
			milisegundosDespues = msIntervaloEmail * email.getCantidadDestinatariosTotal();
		else
			milisegundosDespues = msIntervaloEmail;

		// programando el siguiente envio
		taskNextSend = taskScheduler.schedule(this::sendNextEmail, UtilFecha.getFechaMilisegundosDespues(UtilFecha.ahora(), milisegundosDespues).toInstant());
	}

	public void detenerNuevosEnvios() {
		if (taskNextSend != null && !taskNextSend.isDone())
			taskNextSend.cancel(true);
	}

}
