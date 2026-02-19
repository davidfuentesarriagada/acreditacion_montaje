package com.sicep.exponor2023.acreditacion_montaje.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.EnumRole;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.service.PersonalRepService;
import com.sicep.exponor2023.acreditacion_montaje.service.PersonalService;
import com.sicep.exponor2023.acreditacion_montaje.service.UsuarioRepService;
import com.sicep.exponor2023.acreditacion_montaje.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewController {
	@Value("#{'${propiedad.carpetaLocal}'.concat('/qr')}")
	private String carpetaQr;
	@Value("#{'${propiedad.carpetaLocal}'.concat('/tickets')}")
	private String carpetaTickets;
	@Value("#{'${propiedad.carpetaLocal}'.concat('/plantilla')}")
	private String carpetaPlantilla;
	@Value("${spring.profiles.active}")
	private String env;
	@Value("${propiedad.ver.marca-asistencia}")
	private boolean marcaAsistencia;
	@Value("${propiedad.printTicket}")
	private boolean printTicket;
	private final UsuarioService usuarioService;
	private final PersonalService personalService;
	private final PersonalRepService personalRepService;
	private final UsuarioRepService usuarioRepService;

	@GetMapping("login")
	public String go_login() {
		return "login";
	}

	@GetMapping("listaPersonal")
	public String go_listaPersonal() {
		return "listaPersonal";
	}

	@GetMapping("importarExpositoresAcreditacion")
	public String go_importarExpositoresAcreditacion(Model model) {
		String importUrl = "/import/expositoresFromAcreditacion";
		if (env.equals("prod")) importUrl = "/acreditacion_montaje".concat(importUrl);
		model.addAttribute("importUrl", importUrl);
		return "importarExpositoresAcreditacion";
	}
	
	@PostMapping("import/expositoresFromAcreditacion")
	public String importExpositoresFromAcreditacion(@RequestParam("file") MultipartFile uploadedFile, Principal principal) {
		try {
			Usuario usuario = usuarioService.getUsuarioFromSession(principal);
			personalService.importExpositoresFromAcreditacion(uploadedFile, usuario);
			return "redirect:/listaPersonal";
		}
		catch(ServiceLayerException e) {
			log.error(e.getMessage(), e);
		}
		return "redirect:/listaPersonal";
	}

	@GetMapping("importarExpositoresNube")
	public String go_importarExpositoresNube() {
		return "importarExpositoresNube";
	}
	
	@PostMapping("import/expositoresFromNube")
	public String importExpositoresFromNube(@RequestParam("file") MultipartFile uploadedFile, Principal principal) {
		try {
			Usuario usuario = usuarioService.getUsuarioFromSession(principal);
			personalService.importExpositoresFromNube(uploadedFile, usuario);
			return "redirect:/listaPersonal";
		}
		catch(ServiceLayerException e) {
			log.error(e.getMessage(), e);
		}
		return "redirect:/listaPersonal";
	}

	// personal/QQC4/ver
	@GetMapping("personal/{codigo}/ver")
	public String go_ver(@PathVariable String codigo, final Model model, Principal principal) {
		try {
			model.addAttribute("personal", personalRepService.getPersonalByCodigo(codigo));
			if (principal == null)
				return "verPublic";
			return "ver";
		}
		catch (ServiceLayerException e) {
			return "error";
		}
	}

	@GetMapping("personal/ticket/{codigo}.png")
	public void getTicket(@PathVariable String codigo, HttpServletResponse response) throws IOException, ServiceLayerException {
		File imageFile = new File(carpetaTickets, String.format("ticket_%s.png", codigo));
		if (!imageFile.exists()) {
			personalService.generateTicket(codigo);
			imageFile = new File(carpetaTickets, String.format("ticket_%s.png", codigo));
		}
		
		response.setContentType("image/png");
		InputStream in= new FileInputStream(imageFile);
		IOUtils.copy(in, response.getOutputStream());
		in.close();
	}

	@GetMapping("personal/plantilla/{codigo}.jpg")
	public void getPlantilla(@PathVariable String codigo, HttpServletResponse response) throws IOException, ServiceLayerException {
		File imageFile = new File(carpetaPlantilla, String.format("plantilla_%s.jpg", codigo));
		if (!imageFile.exists()) {
			personalService.generateImagesPersonal(codigo);
			imageFile = new File(carpetaPlantilla, String.format("plantilla_%s.jpg", codigo));
		}
			
		response.setContentType("image/jpeg");
		InputStream in= new FileInputStream(imageFile);
		IOUtils.copy(in, response.getOutputStream());
		in.close();
	}

	@GetMapping("scan")
	public String go_scan() {
		return "scan";
	}
	
	@GetMapping("scanAndPrint")
	public String go_scanAndPrint() {
		return "scanAndPrint";
	}

	@GetMapping("personal/{codigo}/editar")
	public String go_editar(@PathVariable String codigo, final Model model, Principal principal) {
		try {
			model.addAttribute("personal", personalRepService.getPersonalByCodigo(codigo));
			return "editar";
		}
		catch (ServiceLayerException e) {
			return "error";
		}
	}

	@GetMapping("exportarAsistencia")
	public String go_exportarAsistencia() {
		return "exportarAsistencia";
	}
	
	@GetMapping("listaUsuario")
	public String go_listaUsuario(final Model model) {
		model.addAttribute("listaUsuario", usuarioService.list());
		return "listaUsuario";
	}

	@GetMapping("usuario/{idUsuario}")
	public String go_usuario(@PathVariable("idUsuario") long idUsuario, final Model model) throws ServiceLayerException {
		model.addAttribute("usuario", usuarioRepService.getUsuarioById(idUsuario));
		return "usuario";
	}

    /**
     * Redireccionamiento a acreditacion si se trata de acceder a la raiz
     * @return
     */
	@GetMapping("/")
	public ModelAndView go_root(Principal principal) {
		Usuario usuario = usuarioService.getUsuarioFromSession(principal);
		if (usuario.hasPrivilegio(EnumRole.ROLE_ADMIN))
			return new ModelAndView("redirect:listaPersonal");
		return new ModelAndView("redirect:scan");
	}
    
}
