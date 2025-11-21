package com.sicep.exponor2023.acreditacion_montaje.resources.email.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailDesuscrito;

public interface EmailDesuscritoRepository extends JpaRepository<EmailDesuscrito, Long> {
	boolean existsByModuloAndDestinatarioIgnoreCase(String modulo, String destinatario);
	boolean existsByDestinatarioIgnoreCase(String destinatario);
	List<EmailDesuscrito> findByDestinatarioIgnoreCase(String destinatario);
	void deleteByDestinatarioIgnoreCase(String destinatario);
}
