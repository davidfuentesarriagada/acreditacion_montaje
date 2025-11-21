package com.sicep.exponor2023.acreditacion_montaje.domain.personal;

import lombok.Getter;

@Getter
public class PersonalNoRegistradoDTO implements PersonalScanQrDTO {
	private String codigo;
	
	public PersonalNoRegistradoDTO(String codigo) {
		this.codigo = codigo;
	}
	
	@Override
	public String getNombre() {
		return codigo;
	}
	
	@Override
	public boolean isTicketImpreso() {
		return true;
	}
	
	@Override
	public Long getIdPersonal() {
		return null;
	}

	@Override
	public String getObservaciones() {
		return null;
	}

}
