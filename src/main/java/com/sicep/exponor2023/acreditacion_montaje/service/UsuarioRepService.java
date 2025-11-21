package com.sicep.exponor2023.acreditacion_montaje.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sicep.exponor2023.acreditacion_montaje.dao.UsuarioRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioRepService {
	private final UsuarioRepository usuarioRepository;
	
	public Usuario getUsuarioById(long id) throws ServiceLayerException {
		Optional<Usuario> optional = usuarioRepository.findById(id);
		if (optional.isPresent())
			return optional.get();
		throw new ServiceLayerException("Usuario no existe");
	}

	public Usuario getUsuarioByEmail(String email) {
		Usuario usuario= usuarioRepository.findByEmailIgnoreCase(email);
		if (usuario == null)
			throw new UsernameNotFoundException("Usuario no existe");
		return usuario;
	}
}
