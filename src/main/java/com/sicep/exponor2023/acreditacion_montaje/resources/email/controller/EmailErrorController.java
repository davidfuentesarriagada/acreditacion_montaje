package com.sicep.exponor2023.acreditacion_montaje.resources.email.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.EmailErrorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/EmailErrorController")
@RequiredArgsConstructor
public class EmailErrorController {
	private final EmailErrorService emailErrorService;
	
	@GetMapping("/list")
	public ResponseEntity<Object> list() {
		return new ResponseEntity<>(emailErrorService.list(), HttpStatus.OK);
	}
	
	@PostMapping("/delete/{id}")
	public ResponseEntity<Object> deleteById(@PathVariable("id") long idEmailError) {
		return new ResponseEntity<>(emailErrorService.deleteById(idEmailError), HttpStatus.OK);
	}
	
	@GetMapping("/listByDestinatario")
	public ResponseEntity<Object> findByDestinatario(String destinatario) {
		return new ResponseEntity<>(emailErrorService.findByDestinatario(destinatario), HttpStatus.OK);
	}

	@PostMapping("/deleteByDestinatario")
	public ResponseEntity<Object> deleteByDestinatario(String destinatario) {
		return new ResponseEntity<>(emailErrorService.deleteByDestinatario(destinatario), HttpStatus.OK);
	}
	
	// add (rebotes manejados externamente)
	@PostMapping("/add")
	public ResponseEntity<Object> add(String destinatario, String subject, String observaciones) {
		return new ResponseEntity<>(emailErrorService.add(destinatario, subject, observaciones), HttpStatus.OK);
	}

}
