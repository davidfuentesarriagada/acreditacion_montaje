package com.sicep.exponor2023.acreditacion_montaje.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Usuario findByEmailIgnoreCase(String username);
	boolean existsByEmailIgnoreCase(String email);
	List<Usuario> findByRoleNotOrderByNombreDesc(String role);
	boolean existsByEmailAndIdNot(String email, long id);
}
