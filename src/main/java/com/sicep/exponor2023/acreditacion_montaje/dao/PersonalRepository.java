package com.sicep.exponor2023.acreditacion_montaje.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Expositor;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.FilterListaPersonal;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Personal;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.PersonalListaExcelDTO;

public interface PersonalRepository extends JpaRepository<Personal, Long> {
	Personal findByCodigoIgnoreCase(String codigo);
	boolean existsByCodigoIgnoreCase(String codigo);
	@Query(
		"select distinct(personal) "+
		"from Personal personal "+
		"inner join personal.listaExpositor expositor "+		
		"where ( "+
			":#{#filtro.searchText == null ? 1 : 0} = 1 "+
			"or ( "+
				"upper(personal.nombre) like concat('%',upper(:#{#filtro.searchText}),'%') "+
				"or upper(expositor.nombre) like concat('%',upper(:#{#filtro.searchText}),'%') "+
				"or upper(expositor.email) like concat('%',upper(:#{#filtro.searchText}),'%') "+
				"or upper(personal.rut) like concat('%',upper(:#{#filtro.searchText}),'%') "+
				"or personal.codigo like concat('%',upper(:#{#filtro.searchText}),'%') "+
			") "+
		") "
	)
	Page<Personal> filter(PageRequest pageable, FilterListaPersonal filtro);
	
	//boolean existsByNombreIgnoreCaseAndEmpresaIgnoreCase(String nombre, String empresa);
	//List<Personal> findByEmailIgnoreCase(String email);
	
	Personal findByRut(String rut);
	boolean existsByRut(String rut);
	
	List<Personal> findByListaExpositor(Expositor expositor);

	@Query(
		"select "+
			"personal.createdDate as fechaRegistroDate, "+
			"personal.codigo as codigo, "+
			"personal.nombre as nombre, "+
			"personal.rut as rut, "+
			"personal.extranjero as extranjero, "+
			"personal.envioCredencialesDate as fechaEmailDate, "+
			"personal.impresionTicketDate as fechaTicketDate, "+
			"personal.observaciones as observaciones, "+
			"expositor.nombre as nombreEmpresaExpositora, "+
			"expositor.email as emailEmpresaExpositora, "+
			"asistencia.fecha as fechaLastAsistenciaDate, "+
			"acreditador.nombre as acreditador "+
		"from Personal personal "+
		"left join Expositor expositor on expositor.id = ( "+
			"select max(expo.id) "+
			"from Personal pers "+
			"inner join pers.listaExpositor expo "+
			"where pers = personal "+
		") "+
		"left join AsistenciaPersonal asistencia on asistencia.codigoPersonal = personal.codigo and asistencia.fecha = ( "+
			"select max(asis.fecha) "+
			"from AsistenciaPersonal asis "+
			"where asis.codigoPersonal = personal.codigo "+
		") "+
		"left join asistencia.acreditador acreditador "+

		"order by personal.nombre asc "
	)
	List<PersonalListaExcelDTO> findAllForExcel();
	

}
