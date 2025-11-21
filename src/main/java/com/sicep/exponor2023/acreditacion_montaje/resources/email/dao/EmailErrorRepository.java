package com.sicep.exponor2023.acreditacion_montaje.resources.email.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailError;

public interface EmailErrorRepository extends JpaRepository<EmailError, Long> {
	@Query(
		"select email "+
		"from EmailError email "+
		"where lower(email.destinatario) = lower(:destinatario) "+//destinatario unico
		"or lower(email.destinatario) like concat(lower(:destinatario),',%') "+// primer destinatario
		"or lower(email.destinatario) like concat('%,',lower(:destinatario),'%') "// destinatario intermedio o final
	)
	public List<EmailError> findByDestinatario(@Param("destinatario") String destinatario);

	@Query(
		"select count(email) > 0 "+
		"from EmailError email "+
		"where lower(email.destinatario) = lower(:destinatario) "+//destinatario unico
		"or lower(email.destinatario) like concat(lower(:destinatario),',%') "+// primer destinatario
		"or lower(email.destinatario) like concat('%,',lower(:destinatario),'%') "// destinatario intermedio o final
	)
	public boolean existsByDestinatario(@Param("destinatario") String destinatario);

}
