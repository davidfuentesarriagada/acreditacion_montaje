package com.sicep.exponor2023.acreditacion_montaje.domain;

import java.io.File;

import com.sicep.exponor2023.acreditacion_montaje.dao.ImpresoraRepository;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.service.ImpresoraRepService;
import com.sicep.exponor2023.acreditacion_montaje.service.ImpresoraService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sicep.exponor2023.acreditacion_montaje.dao.UsuarioRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.EnumRole;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {
	@Value("${propiedad.carpetaLocal}")
	private String rutaCarpetaLocal;
	private final UsuarioRepository usuarioRepository;
	private final ResourceLoader resourceLoader;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ImpresoraRepository impresoraRepository;
	private final ImpresoraService impresoraService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("INICIO CARGA USUARIOS");
		developer();
		log.info("INICIO CREACION CARPETA QR");
		crearCarpetaQR();
		log.info("INICIO CREACION CARPETA PLANTILLA");
		crearCarpetaPlantilla();
		log.info("INICIO CREACION CARPETA TICKETS");
		crearCarpetaTickets();
		log.info("INICIO CARGA IMPRESORAS");
		impresoras();
		log.info("DATALOADER END");
	}

	private void impresoras() {
		if (impresoraRepository.count() > 0)
			return;
		try {
			impresoraService.register("Brother QL-710W wifi1");
			impresoraService.register("Brother QL-710W wifi2");
			impresoraService.register("Brother QL-710W wifi3");
			impresoraService.register("Brother QL-710W wifi4");
			impresoraService.register("Brother QL-710W wifi5");
			impresoraService.register("Brother QL-710W wifi6");
			impresoraService.register("Brother QL-710W wifi7");
			impresoraService.register("Brother QL-710W wifi8");
			impresoraService.register("Brother QL-710W wifi9");
			impresoraService.register("Brother QL-710W wifi10");

			impresoraService.register("Brother QL-710W usb1");
			impresoraService.register("Brother QL-710W usb2");
			impresoraService.register("Brother QL-710W usb3");
			impresoraService.register("Brother QL-710W usb4");
			impresoraService.register("Brother QL-710W usb5");
			impresoraService.register("Brother QL-710W usb6");
			impresoraService.register("Brother QL-710W usb7");
			impresoraService.register("Brother QL-710W usb8");
			impresoraService.register("Brother QL-710W usb9");
			impresoraService.register("Brother QL-710W usb10");

		}
		catch (ServiceLayerException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private void developer() {
		if (!usuarioRepository.existsByEmailIgnoreCase("r.aspee@sicep.cl")) {
			Usuario usuario = new Usuario();
			usuario.setEmail("r.aspee@sicep.cl");
			usuario.setNombre("Ricardo Aspeé");
			usuario.setRole(EnumRole.ROLE_DEVELOPER.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_DEVELOPER.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_ADMIN.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_USER.name());
			usuario.setPassword(passwordEncoder.encode("789789"));
			usuarioRepository.save(usuario);
		}
		if (!usuarioRepository.existsByEmailIgnoreCase("dfuentes@sicep.cl")) {
			Usuario usuario = new Usuario();
			usuario.setEmail("dfuentes@sicep.cl");
			usuario.setNombre("David Fuentes");
			usuario.setRole(EnumRole.ROLE_ADMIN.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_DEVELOPER.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_ADMIN.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_USER.name());
			usuario.setPassword(passwordEncoder.encode("789789"));
			usuarioRepository.save(usuario);
		}
		if (!usuarioRepository.existsByEmailIgnoreCase("jramirez@sicep.cl")) {
			Usuario usuario = new Usuario();
			usuario.setEmail("jramirez@sicep.cl");
			usuario.setNombre("Jeanette Ramirez");
			usuario.setRole(EnumRole.ROLE_ADMIN.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_ADMIN.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_USER.name());
			usuario.setPassword(passwordEncoder.encode("789789"));
			usuarioRepository.save(usuario);
		}
		if (!usuarioRepository.existsByEmailIgnoreCase("karlau.codetia@aia.cl")) {
			Usuario usuario = new Usuario();
			usuario.setEmail("karlau.codetia@aia.cl");
			usuario.setNombre("Karla Urbina");
			usuario.setRole(EnumRole.ROLE_ADMIN.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_ADMIN.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_USER.name());
			usuario.setPassword(passwordEncoder.encode("789789"));
			usuarioRepository.save(usuario);
		}
		if (!usuarioRepository.existsByEmailIgnoreCase("mtrigo@sicep.cl")) {
			Usuario usuario = new Usuario();
			usuario.setEmail("mtrigo@sicep.cl");
			usuario.setNombre("Manuel Trigo");
			usuario.setRole(EnumRole.ROLE_ADMIN.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_DEVELOPER.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_ADMIN.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_USER.name());
			usuario.setPassword(passwordEncoder.encode("789789"));
			usuarioRepository.save(usuario);
		}
		if (!usuarioRepository.existsByEmailIgnoreCase("cgallo@sicep.cl")) {
			Usuario usuario = new Usuario();
			usuario.setEmail("cgallo@sicep.cl");
			usuario.setNombre("usuario");
			usuario.setRole(EnumRole.ROLE_USER.name());
			usuario.getListaPrivilegio().add(EnumRole.ROLE_USER.name());
			usuario.setPassword(passwordEncoder.encode("789789"));
			usuarioRepository.save(usuario);
		}
	}

	private void crearCarpetaQR() throws Exception {
		File carpeta = new File(rutaCarpetaLocal, "qr");
		if (!carpeta.exists()) {
			if (!(carpeta.mkdirs()))
				throw new Exception("no es posible crear la carpeta para almacenar QR");
		}
	}

	private void crearCarpetaPlantilla() throws Exception {
		File carpeta = new File(rutaCarpetaLocal, "plantilla");
		if (!carpeta.exists()) {
			if (!(carpeta.mkdirs()))
				throw new Exception("no es posible crear la carpeta para almacenar plantillas");
		}
	}

	private void crearCarpetaTickets() throws Exception {
		File carpeta = new File(rutaCarpetaLocal, "tickets");
		if (!carpeta.exists()) {
			if (!(carpeta.mkdirs()))
				throw new Exception("no es posible crear la carpeta para almacenar Tickets");
		}
	}
}
