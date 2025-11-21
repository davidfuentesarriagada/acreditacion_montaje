package com.sicep.exponor2023.acreditacion_montaje.domain.personal;

public interface PersonalScanQrDTO {
	Long getIdPersonal();
	String getCodigo();
	String getNombre();
	String getObservaciones();
	boolean isTicketImpreso();
}
