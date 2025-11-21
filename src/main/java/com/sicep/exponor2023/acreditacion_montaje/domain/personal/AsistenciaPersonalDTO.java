package com.sicep.exponor2023.acreditacion_montaje.domain.personal;

import java.util.Date;

import com.sicep.exponor2023.acreditacion_montaje.util.FormatoCampos;
import com.sicep.exponor2023.acreditacion_montaje.util.FormatoFecha;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilString;

public interface AsistenciaPersonalDTO {
	Date getFechaDate();
	String getCodigo();
	String getNombre();
	String getRut();
	String getNombreEmpresaExpositora();
	String getEmailEmpresaExpositora();
	String getAcreditador();
	
	default String getFecha() {
		return UtilString.fecha(getFechaDate(), FormatoFecha.sdfDMYHM);
	}
	
	default String getRutCompleto() {
		if (getRut() == null)
			return getRut();
		try {
			Integer.parseInt(getRut());
		}
		catch(NumberFormatException e) {
			return getRut();
		}
		
		return String.format("%s-%s", getRut(), FormatoCampos.getDigitoVerificador(getRut()));
	}

}
