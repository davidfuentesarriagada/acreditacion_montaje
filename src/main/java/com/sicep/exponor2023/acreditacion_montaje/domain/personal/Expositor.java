package com.sicep.exponor2023.acreditacion_montaje.domain.personal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Expositor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(nullable = false)
	private String nombre;
	@Column(nullable = false)
	private String email;// email contacto

	public Expositor() {
	}
	public Expositor(String nombre, String email) {
		this.nombre = nombre;
		this.email = email;
	}
	
	
}
