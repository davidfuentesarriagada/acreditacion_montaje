package com.sicep.exponor2023.acreditacion_montaje.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sicep.exponor2023.acreditacion_montaje.domain.personal.AsistenciaPersonalDTO;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.AsistenciaPersonal;

public interface AsistenciaPersonalRepository extends JpaRepository<AsistenciaPersonal, Long> {
	@Query(
		"select count(distinct asistencia.codigoPersonal) " +
		"from AsistenciaPersonal asistencia "
	)
	long countPersonalAsistentes();

	// verificar relacion
	@Query(
		"select "+
			"asistencia.fecha as fechaDate, "+
			"asistencia.codigoPersonal as codigo, "+
			"personal.nombre as nombre, "+
			"personal.rut as rut, "+
			"expositor.nombre as nombreEmpresaExpositora, "+
			"expositor.email as emailEmpresaExpositora, "+
			"acreditador.nombre as acreditador "+
		"from AsistenciaPersonal asistencia "+
		"left join Personal personal on personal.codigo = asistencia.codigoPersonal "+
		"left join asistencia.acreditador acreditador "+
		"left join Expositor expositor on expositor.id = ( "+
			"select max(expo.id) "+
			"from Personal pers "+
			"inner join pers.listaExpositor expo "+
			"where pers = personal "+
		") "+
		"order by asistencia.fecha asc "
	)
	List<AsistenciaPersonalDTO> getListAsistentes();
	
	@Query(
		"select max(asistencia.fecha) "+
		"from AsistenciaPersonal asistencia "+
		"where asistencia.codigoPersonal = :#{#codigo} "
	)
	Date findLastFechaByCodigoPersonal(String codigo);
	
}
