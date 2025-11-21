package com.sicep.exponor2023.acreditacion_montaje.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Expositor;

public interface ExpositorRepository extends JpaRepository<Expositor, Long> {
	Expositor findByNombreIgnoreCaseAndEmailIgnoreCase(String nombre, String email);
	Expositor findByNombreIgnoreCase(String nombre);
}
