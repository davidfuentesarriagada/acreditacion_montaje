package com.sicep.exponor2023.acreditacion_montaje.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.sicep.exponor2023.acreditacion_montaje.dao.UsuarioRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {
	private final UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario= usuarioRepository.findByEmailIgnoreCase(username);
		if (usuario == null)
			throw new UsernameNotFoundException("Usuario no existe");
		return new CustomUserDetails(usuario);
	}

}
