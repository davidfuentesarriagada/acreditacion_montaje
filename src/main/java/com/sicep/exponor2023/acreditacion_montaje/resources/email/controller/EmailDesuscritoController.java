package com.sicep.exponor2023.acreditacion_montaje.resources.email.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailDesuscrito;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.ColaSendEmailService;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.EmailDesuscritoService;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class EmailDesuscritoController {
	private final EmailDesuscritoService emailDesuscritoService;

	@PostMapping("/addByDestinatario")
	public ResponseEntity<Object> addByDestinatario(String destinatario) {
		emailDesuscritoService.addByDestinatario(destinatario);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}
	
	@GetMapping("/EmailDesuscritoController/add/{token}")
	public RedirectView add(@PathVariable("token") String token, RedirectAttributes attributes) {
		try {
			EmailDesuscrito email = emailDesuscritoService.add(token);
			attributes.addFlashAttribute("email_desuscrito", email.getDestinatario());
			return new RedirectView("/unsuscribed", true);// true porque es relativo al contexto, es decir, no es hacia una pagina externa
		}
		catch (ServiceLayerException e) {
			return new RedirectView("/", true);// true porque es relativo al contexto, es decir, no es hacia una pagina externa
		}

	}
	
	@GetMapping("unsuscribed")
	public String go_unsuscribed(HttpServletRequest request) {
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		if (inputFlashMap != null) {
			return "unsuscribed";
		}
		return "redirect:/";
	}
	
	@GetMapping("/EmailDesuscritoController/list")
	public ResponseEntity<Object> list() {
		return new ResponseEntity<>(emailDesuscritoService.list(), HttpStatus.OK);
	}
	
	@GetMapping("/EmailDesuscritoController/listByDestinatario")
	public ResponseEntity<Object> findByDestinatario(String destinatario) {
		return new ResponseEntity<>(emailDesuscritoService.findByDestinatario(destinatario), HttpStatus.OK);
	}

	@PostMapping("/EmailDesuscritoController/deleteByDestinatario")
	public ResponseEntity<Object> deleteByDestinatario(String destinatario) {
		return new ResponseEntity<>(emailDesuscritoService.deleteByDestinatario(destinatario), HttpStatus.OK);
	}
}
