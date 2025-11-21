package com.sicep.exponor2023.acreditacion_montaje.resources.email;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class EmailError {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(nullable = false, length = 550)
	private String destinatario;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaIntentoEnvio;
	private String observaciones;
	private String subject;

}
