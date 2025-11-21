package com.sicep.exponor2023.acreditacion_montaje.resources.email.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailBase;

public interface EmailBaseRepository extends JpaRepository<EmailBase, Long> {
	boolean existsByEnviandoTrue();
	EmailBase findTopByEnviandoFalseOrderByNumeroPrioridadAscIdAsc();
	@Query(
		"select case when count(email) = 0 then 1 else max(email.numeroPrioridad) + 1 end "+
		"from EmailBase email "
	)
	int nextUltimaPrioridad();
	
	@Query(
		"select case when count(email) = 0 then 0 else min(email.numeroPrioridad) - 1 end "+
		"from EmailBase email "
	)
	int previousPrimeraPrioridad();
	
	@Modifying
	@Query(
		"update EmailBase email "+
		"set email.enviando = false "+
		"where email.enviando = true "
	)
	int clearEnviando();
}
