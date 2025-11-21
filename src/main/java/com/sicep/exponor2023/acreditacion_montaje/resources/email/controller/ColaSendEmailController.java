package com.sicep.exponor2023.acreditacion_montaje.resources.email.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.ColaSendEmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ColaSendEmailController")
@Slf4j
@RequiredArgsConstructor
public class ColaSendEmailController {
	private final ColaSendEmailService colaSendEmailService;
	
	@PostMapping("/start")
	public ResponseEntity<Object> start() {
		return new ResponseEntity<>(colaSendEmailService.start(), HttpStatus.OK);
	}
	
	@PostMapping("/pause")
	public ResponseEntity<Object> pause() {
		return new ResponseEntity<>(colaSendEmailService.pause(), HttpStatus.OK);
	}
	
	 // reanudar desde pausa
	@PostMapping("/reanude")
	public ResponseEntity<Object> reanude() {
		return new ResponseEntity<>(colaSendEmailService.reanude(), HttpStatus.OK);
	}
	
	@GetMapping("/get")
	public ResponseEntity<Object> get() {
		return new ResponseEntity<>(colaSendEmailService.getColaSendEmail(), HttpStatus.OK);
	}
	
	@GetMapping("/list")
	public ResponseEntity<Object> list() {
		return new ResponseEntity<>(colaSendEmailService.getListEmailEnCola(), HttpStatus.OK);
	}
	
	// eliminar por id de la cola
	@PostMapping("/delete/{id}")
	public ResponseEntity<Object> deleteById(@PathVariable("id") long idEmailBase) {
		return new ResponseEntity<>(colaSendEmailService.deleteById(idEmailBase), HttpStatus.OK);
	}
	

}
