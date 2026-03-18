package com.sicep.exponor2023.acreditacion_montaje.dao;

import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Expositor;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Modulador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModuladorRepository extends JpaRepository<Modulador, Long> {
	Modulador findByNombreIgnoreCaseAndEmailIgnoreCase(String nombre, String email);
	Modulador findByNombreIgnoreCase(String nombre);

	@Query(value = "select count(distinct personal_id) " +
				   "from personal_lista_modulador where empresa_id = :id", nativeQuery = true)
	long countPersonalByModuladorIdNative(@Param("id") Long id);
}
