package com.sicep.exponor2023.acreditacion_montaje.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.UsuarioDTO;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;
import com.sicep.exponor2023.acreditacion_montaje.service.UsuarioService;

@Controller
@RequestMapping("/UsuarioController")
public class UsuarioController {
	@Autowired
	private UsuarioService usuarioService;

	@Secured({"ROLE_ADMIN"})
	@PostMapping("/usuario/register")
	public ResponseEntity<Object> register(@RequestBody UsuarioDTO dto, Principal principal) {
		System.out.println("Registro de usuario: " + dto);
		try {
			Usuario admin = usuarioService.getUsuarioFromSession(principal);
			usuarioService.register(admin, dto);
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Secured({"ROLE_ADMIN"})
	@GetMapping("/usuario/list")
	public ResponseEntity<Object> list() {
		return new ResponseEntity<>(usuarioService.list(), HttpStatus.OK);
	}
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("usuario/{idUsuario}/edit")
	public ResponseEntity<Object> editUsuario(@PathVariable("idUsuario") long idUsuario, UsuarioDTO dto, Principal principal) {
		Usuario usuarioAdmin = usuarioService.getUsuarioFromSession(principal);
		try {
			dto.setId(idUsuario);
			usuarioService.editUsuario(dto, usuarioAdmin);
			return new ResponseEntity<>(HttpStatus.OK);
		} 
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Secured({"ROLE_ADMIN"})
	@PostMapping("usuario/{idUsuario}/changeEnabled/{enabled}")
	public ResponseEntity<Object> changeEnabled(@PathVariable("idUsuario") long idUsuario, @PathVariable("enabled") boolean enabled, Principal principal) {
		Usuario usuarioAdmin = usuarioService.getUsuarioFromSession(principal);
		try {
			usuarioService.changeEnabled(idUsuario, enabled, usuarioAdmin);
			return new ResponseEntity<>(HttpStatus.OK);
		} 
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

}
