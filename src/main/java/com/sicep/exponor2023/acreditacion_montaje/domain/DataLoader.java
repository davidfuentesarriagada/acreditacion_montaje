package com.sicep.exponor2023.acreditacion_montaje.domain;

import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sicep.exponor2023.acreditacion_montaje.dao.UsuarioRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.EnumRole;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;
import com.sicep.exponor2023.acreditacion_montaje.resources.email.service.ColaSendEmailService;

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
		log.info("DATALOADER END");
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
