package com.sicep.exponor2023.acreditacion_montaje.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Impresora {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(unique = true, nullable = false)
	private String printerName;

	public Impresora(String printerName) {
		this.printerName = printerName;
	}
}
