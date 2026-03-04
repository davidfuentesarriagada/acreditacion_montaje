package com.sicep.exponor2023.acreditacion_montaje.domain.personal;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Modulador {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(nullable = false)
	private String nombre;
	@Column(nullable = false)
	private String email;// email contacto

	public Modulador(String nombre, String email) {
		this.nombre = nombre;
		this.email = email;
	}
}
