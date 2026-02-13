package com.sicep.exponor2023.acreditacion_montaje;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupPrinter implements CommandLineRunner {

	@Value("${spring.application.name}")
	private String appName;

	@Value("${spring.application.version}")
	private String appVersion;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Value("${server.port}")
	private String port;

	@Override
	public void run(String... args) {
		log.info("");
		log.info("=================================================");
		log.info("=          APPLICATION STARTUP INFO            =");
		log.info("=================================================");
		log.info("= Name          : {}", appName);
		log.info("= Version       : {}", appVersion);
		log.info("= Environment   : {}", activeProfile);
		log.info("= Port          : {}", port);
		log.info("=================================================");
	}
}

