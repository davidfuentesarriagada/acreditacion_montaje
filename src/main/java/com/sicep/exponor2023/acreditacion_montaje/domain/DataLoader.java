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
		log.info("inicio");
		developer();
		crearCarpetaQR();
		crearCarpetaPlantilla();
		crearCarpetaTickets();
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
