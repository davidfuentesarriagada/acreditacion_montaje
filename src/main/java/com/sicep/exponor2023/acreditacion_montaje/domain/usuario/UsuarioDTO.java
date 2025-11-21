package com.sicep.exponor2023.acreditacion_montaje.domain.usuario;

import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.util.FormatoCampos;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilString;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuarioDTO {
	private String nombre;
	private String email;
	private String password;
	private EnumRole role = EnumRole.ROLE_USER;
	// solo para la edicion
	private boolean enabled = true;
	private Long id;
	
	public void setEmail(String email) {
		this.email = UtilString.textoDesdeVista(email);
	}

	public void setPassword(String password) {
		this.password = UtilString.textoDesdeVista(password);
	}

	public void setNombre(String nombre) {
		this.nombre = UtilString.textoDesdeVista(nombre);
	}

	public void validate() throws ServiceLayerException {
		FormatoCampos.verifyStringObligatorio("Email", email);
		if (id == null)
			FormatoCampos.verifyStringObligatorio("Contraseña", password);
		FormatoCampos.verifyStringObligatorio("Nombre de usuario", nombre);
		FormatoCampos.verifyStringMaxLength(Usuario.class, "email", "Email", email);
		FormatoCampos.verifyStringMinMaxLength(Usuario.class, "password", "Contraseña", password, 4);
		FormatoCampos.verifyStringMinMaxLength(Usuario.class, "nombre", "Nombre de usuario", nombre, 2);
		
		// verificar el formato del email
		if (!FormatoCampos.isValidEmail(email))
			throw new ServiceLayerException("El campo email no tiene formato correcto");
	}
}
