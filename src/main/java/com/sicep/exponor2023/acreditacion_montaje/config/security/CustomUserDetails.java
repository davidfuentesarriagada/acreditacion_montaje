package com.sicep.exponor2023.acreditacion_montaje.config.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;

import lombok.Getter;

@SuppressWarnings("serial")
public class CustomUserDetails implements UserDetails {
	@Getter
	private Usuario usuario;
	
	public CustomUserDetails(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> list = new ArrayList<>();
		usuario.getListaPrivilegio().stream().map(p -> new SimpleGrantedAuthority(p)).forEach(list::add);
		return list;
	}

	@Override
	public String getPassword() {
		return usuario.getPassword();
	}

	@Override
	public String getUsername() {
		return usuario.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return usuario.isEnabled();
	}

}
