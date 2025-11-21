package com.sicep.exponor2023.acreditacion_montaje.service;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.sicep.exponor2023.acreditacion_montaje.util.GeneradorCodigoAlfanumerico;

@Service
public class GeneradorCodigoAlfanumericoService {
	private GeneradorCodigoAlfanumerico gen4;
	public GeneradorCodigoAlfanumericoService() {
		gen4 = new GeneradorCodigoAlfanumerico(4, ThreadLocalRandom.current());
	}
	
	public String nextString() {
		return gen4.nextString();
	}
}
