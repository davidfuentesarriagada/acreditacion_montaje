package com.sicep.exponor2023.acreditacion_montaje.domain.personal;

import java.util.Date;

import com.sicep.exponor2023.acreditacion_montaje.util.FormatoCampos;
import com.sicep.exponor2023.acreditacion_montaje.util.FormatoFecha;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilString;

public interface PersonalListaExcelDTO {
	Date getFechaRegistroDate();
	String getCodigo();
	String getNombre();
	String getRut();
	String getObservaciones();
	boolean isExtranjero();
	Date getFechaTicketDate();
	Date getFechaEmailDate();
	String getNombreEmpresaExpositora();
	String getEmailEmpresaExpositora();
	Date getFechaLastAsistenciaDate();
	String getAcreditador();
	
	default String getFechaRegistro() {
		return UtilString.fecha(getFechaRegistroDate(), FormatoFecha.sdfDMYHM);
	}
	
	default String getFechaTicket() {
		return UtilString.fecha(getFechaTicketDate(), FormatoFecha.sdfDMYHM);
	}
	default String getFechaEmail() {
		return UtilString.fecha(getFechaEmailDate(), FormatoFecha.sdfDMYHM);
	}
	default String getFechaLastAsistencia() {
		return UtilString.fecha(getFechaLastAsistenciaDate(), FormatoFecha.sdfDMYHM);
	}

	default String getRutCompleto() {
		if (getRut() == null || isExtranjero())
			return getRut();
		return String.format("%s-%s", getRut(), FormatoCampos.getDigitoVerificador(getRut()));
	}

}