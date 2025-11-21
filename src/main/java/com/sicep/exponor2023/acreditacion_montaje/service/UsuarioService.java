package com.sicep.exponor2023.acreditacion_montaje.service;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sicep.exponor2023.acreditacion_montaje.config.security.CustomUserDetails;
import com.sicep.exponor2023.acreditacion_montaje.dao.UsuarioRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.EnumRole;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.UsuarioDTO;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
	private final UsuarioRepService usuarioRepService;
	private final UsuarioRepository usuarioRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	public Usuario getUsuarioFromSession(Principal principal) {
		Authentication authentication = (Authentication) principal;
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		return customUserDetails.getUsuario();
	}

	public void register(UsuarioDTO dto) throws ServiceLayerException {
		register(null, dto);
	}
	
	public void register(Usuario admin, UsuarioDTO dto) throws ServiceLayerException {
		dto.validate();
		verificacionEmailUsuarioDisponible(dto.getEmail());
		
		Usuario usuario = new Usuario();
		usuario.setEmail(dto.getEmail());
		usuario.setNombre(dto.getNombre());
		usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
		usuario.setRole(dto.getRole().name());
		usuario.getListaPrivilegio().add(EnumRole.ROLE_USER.name());
		if (dto.getRole().equals(EnumRole.ROLE_ADMIN))
			usuario.getListaPrivilegio().add(EnumRole.ROLE_ADMIN.name());
		
		usuarioRepository.save(usuario);
	}
	
	public List<Usuario> list() {
		return usuarioRepository.findByRoleNotOrderByNombreDesc(EnumRole.ROLE_DEVELOPER.name());
	}
	
	public boolean verificacionEmailUsuarioDisponible(String emailUsuario) throws ServiceLayerException {
		if (usuarioRepository.existsByEmailIgnoreCase(emailUsuario))
			throw new ServiceLayerException("Email de Usuario ya registrado (" + emailUsuario + ")");
		return true;
	}
	
	public void editUsuario(UsuarioDTO dto, Usuario usuarioAdmin) throws ServiceLayerException {
		dto.validate();
		
		// verificacion de email unico
		if (usuarioRepository.existsByEmailAndIdNot(dto.getEmail(), dto.getId()))
			throw new ServiceLayerException("Ya existe usuario con email: " + dto.getEmail());
		
		Usuario usuario = usuarioRepService.getUsuarioById(dto.getId());
		usuario.setEmail(dto.getEmail());
		usuario.setNombre(dto.getNombre());
		if (dto.getPassword() != null)
			usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
		usuario.setRole(dto.getRole().name());
		usuario.getListaPrivilegio().clear();
		usuario.getListaPrivilegio().add(dto.getRole().name());
		usuario.setEnabled(dto.isEnabled());
		
		usuarioRepository.save(usuario);
	}

	public void changeEnabled(long idUsuario, boolean enabled, Usuario usuarioAdmin) throws ServiceLayerException {
		Usuario usuario = usuarioRepService.getUsuarioById(idUsuario);
		usuario.setEnabled(enabled);
		usuarioRepository.save(usuario);
	}
	
}
