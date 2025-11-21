package com.sicep.exponor2023.acreditacion_montaje.resources.email;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ColaSendEmail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaEstado;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Estado estado = Estado.LIBRE;
	
	public static enum Estado {
		PROCESANDO,PAUSA,LIBRE
	}
}
