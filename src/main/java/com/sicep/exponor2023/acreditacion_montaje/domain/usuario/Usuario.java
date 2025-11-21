package com.sicep.exponor2023.acreditacion_montaje.domain.usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Usuario implements Serializable {
	private static final long serialVersionUID = -3861159202986059054L;
	@Id
	@GeneratedValue
	private Long id;
	@Column(nullable = false, unique = true, length = 80)
	private String email;
	@JsonIgnore
	@Column(nullable = false, length = 64)
	private String password;
	@Column(nullable = false, length = 100)
	private String nombre;
	@Column(nullable = false, length = 20)
	private String role;
	@Column(nullable = false)
	private boolean enabled = true;
	
	@JsonIgnore
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> listaPrivilegio = new ArrayList<>();
	
	public boolean hasPrivilegio(EnumRole privilegio) {
		return listaPrivilegio.stream().anyMatch(privilegio.name()::equals);
	}

}
