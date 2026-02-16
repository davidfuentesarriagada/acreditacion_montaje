package com.sicep.exponor2023.acreditacion_montaje.controller;

import java.io.IOException;
import java.security.Principal;

import com.sicep.exponor2023.acreditacion_montaje.service.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sicep.exponor2023.acreditacion_montaje.domain.personal.FilterListaPersonal;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Personal;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.PersonalDTO;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.util.ArchivoWorkbook;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/PersonalController")
@Slf4j
@RequiredArgsConstructor
public class PersonalController {
	@Value("#{'${propiedad.carpetaLocal}'.concat('/tickets')}")
	private String carpetaTickets;
	private final UsuarioService usuarioService;
	private final PersonalService personalService;
	private final ExcelListadoPersonalService excelListadoPersonalService;
	private final ExcelListadoAsistenciaService excelListadoAsistenciaService;
	private final ExpositorRepService expositorRepService;
	
	@PostMapping("personal/filter")
	public ResponseEntity<Object> filter(@RequestBody FilterListaPersonal filtro) {
		return new ResponseEntity<>(personalService.filter(filtro), HttpStatus.OK);
	}
	
	@PostMapping("personal/register")
	public ResponseEntity<Object> registerPersonal(@RequestBody PersonalDTO dto, Principal principal) {
		try {
			Usuario admin = null;
			if (principal != null)
				admin = usuarioService.getUsuarioFromSession(principal);
			Personal personal = personalService.registerPersonalIndividual(dto, admin);
			if (admin == null)
				return new ResponseEntity<>(personal.getCodigo(), HttpStatus.OK);
			return new ResponseEntity<>(personal, HttpStatus.OK);
		}
		catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/personal/{codigo}/edit")
	public ResponseEntity<Object> editPersonal(@PathVariable String codigo, @RequestBody PersonalDTO dto, Principal principal) {
		try {
			Usuario usuario = usuarioService.getUsuarioFromSession(principal);
			return new ResponseEntity<>(personalService.editPersonal(usuario, codigo, dto), HttpStatus.OK);
		}
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/personal/remover/{codigo}")
	public ResponseEntity<Object> deletePersonal(@PathVariable String codigo, Principal principal) {
		try {
			Usuario usuario = usuarioService.getUsuarioFromSession(principal);
			personalService.deletePersonal(usuario, codigo);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	

	@PostMapping("/personal/{codigo}/generateImages")
	public ResponseEntity<Object> generateImagesPersonal(@PathVariable String codigo) {
		try {
			personalService.generateImagesPersonal(codigo);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Utilizado cuando se realiza impresion desde el cliente. La impresion por servidor de impresion incluye la activacion 
	 * del flag de impreso
	 * @param codigo
	 * @return
	 */
	@PostMapping("/personal/{codigo}/setPrintedTicket")
	public ResponseEntity<Object> setPrintedTicketPersonal(@PathVariable String codigo) {
		try {
			return new ResponseEntity<>(personalService.setPrintedTicketPersonal(codigo), HttpStatus.OK);
		}
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/personal/scanQr")
	public ResponseEntity<Object> scanQr(String texto, Principal principal) {
		try {
			Usuario usuario = usuarioService.getUsuarioFromSession(principal);
			return new ResponseEntity<>(personalService.scanQr(usuario, texto), HttpStatus.OK);
		}
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/expositor/{idExpositor}/sendEmail")
	public ResponseEntity<Object> sendEmail(@PathVariable long idExpositor) {
		try {
			personalService.sendEmailByExpositor(idExpositor);
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping("/personal/{codigo}/printTicket")
	public ResponseEntity<Object> printTicket(Principal principal, @PathVariable String codigo) {
		try {
			Usuario usuario = usuarioService.getUsuarioFromSession(principal);
			return new ResponseEntity<>(personalService.printTicket(usuario, codigo), HttpStatus.OK);
		}
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/personal/lista/exportar")
	public ResponseEntity<Object> exportarLista(Principal principal, HttpServletResponse response) {
		try {
			Usuario usuario = usuarioService.getUsuarioFromSession(principal);
			ArchivoWorkbook archivoWorkbook = excelListadoPersonalService.exportar(usuario);
			Workbook workbook = archivoWorkbook.getWorkbook();
			
	        response.setContentType("application/vnd.ms-excel");
	        response.setHeader("Cache-Control", "private, max-age=15");
			response.setHeader("Set-Cookie", "fileDownload=true; path=/");// requerido por jquery.fileDownloader
			response.setHeader("Content-Disposition", "attachment; filename=\""+archivoWorkbook.getNombreArchivo()+"\"");
	        workbook.write(response.getOutputStream()); // realizando las modificaciones en el archivo de salida

			return null;
		}
		catch (ServiceLayerException | IOException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/personal/generatePlantilla/all")
	public ResponseEntity<Object> generatePlantillaQrToAll() {
		try {
			personalService.generatePlantillaQrToAll();
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/personal/{codigo}/generateTicket")
	public ResponseEntity<Object> generateTicket(@PathVariable String codigo, Principal principal) {
		try {
			Usuario usuario = usuarioService.getUsuarioFromSession(principal);
			personalService.marcarAsistencia(usuario, codigo);
			personalService.generateTicket(codigo);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch (ServiceLayerException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/personal/asistencia/exportar")
	public ResponseEntity<Object> exportarListaAsistencia(Principal principal, HttpServletResponse response) {
		try {
			Usuario usuario = usuarioService.getUsuarioFromSession(principal);
			ArchivoWorkbook archivoWorkbook = excelListadoAsistenciaService.exportar(usuario);
			Workbook workbook = archivoWorkbook.getWorkbook();
			
	        response.setContentType("application/vnd.ms-excel");
	        response.setHeader("Cache-Control", "private, max-age=15");
			response.setHeader("Set-Cookie", "fileDownload=true; path=/");// requerido por jquery.fileDownloader
			response.setHeader("Content-Disposition", "attachment; filename=\""+archivoWorkbook.getNombreArchivo()+"\"");
	        workbook.write(response.getOutputStream()); // realizando las modificaciones en el archivo de salida

			return null;
		}
		catch (ServiceLayerException | IOException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/personal/check/{rut}")
	public ResponseEntity<Object> verificarRutExistente(@PathVariable String rut, Principal principal) {
		try {
			return new ResponseEntity<>(personalService.verificarRutExistente(rut), HttpStatus.OK);
		}
		catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/expositor/all")
	public ResponseEntity<Object> getAllExpositores() {
		try {
			return new ResponseEntity<>(expositorRepService.findAll(), HttpStatus.OK);
		}
		catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

//	@GetMapping("/personal/remover/{rut}")
//	public ResponseEntity<Object> removerPersonal(@PathVariable String rut, Principal principal) {
//		try {
//			return new ResponseEntity<>(personalService.removerPersonalExistente(rut), HttpStatus.OK);
//		}
//		catch (Exception e) {
//			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//		}
//	}
}
