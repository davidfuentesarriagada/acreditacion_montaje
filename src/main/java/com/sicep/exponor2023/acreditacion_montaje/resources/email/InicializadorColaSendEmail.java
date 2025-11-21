package com.sicep.exponor2023.acreditacion_montaje.resources.email;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.ColaSendEmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InicializadorColaSendEmail implements ApplicationRunner {
	private final ColaSendEmailService colaSendEmailService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		colaSendEmailService.inicializar();
	}

}
