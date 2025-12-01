package com.sicep.exponor2023.acreditacion_montaje.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Expositor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpositorRepository extends JpaRepository<Expositor, Long> {
	Expositor findByNombreIgnoreCaseAndEmailIgnoreCase(String nombre, String email);
	Expositor findByNombreIgnoreCase(String nombre);

	@Query(value = "select count(distinct personal_id) " +
				   "from personal_lista_expositor where empresa_id = :id", nativeQuery = true)
	long countPersonalByExpositorIdNative(@Param("id") Long id);

}
