package com.sicep.exponor2023.acreditacion_montaje.domain.personal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.util.FormatoCampos;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilString;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PersonalDTO {
	private String nombre;
	private String empresa;
	private String email;
	private String rut;// rut sin digito verficador
	private boolean extranjero = false;// segun rut
	private String codigo; // requerido cuando se importa desde la nube
	private String observaciones;
	
	public void setNombre(String nombre) {
		this.nombre = UtilString.textoDesdeVista(nombre);
	}

	public void setEmpresa(String empresa) {
		this.empresa = UtilString.textoDesdeVista(empresa);
	}

	public void setEmail(String email) {
		this.email = UtilString.textoDesdeVistaToLowerCase(email);
	}

	public void setRut(String rut) {
		this.rut = UtilString.textoDesdeVistaToUpperCase(rut);
		if (this.rut != null)
			this.rut = this.rut.replace(".", "").replace(",", "");
	}
	
	public void setObservaciones(String observaciones) {
		this.observaciones = UtilString.textoDesdeVista(observaciones);
	}
	
	
	public void validate() throws ServiceLayerException {
		// validacion de campos obligatorios
		FormatoCampos.verifyStringObligatorio("Nombre", nombre);
		FormatoCampos.verifyStringObligatorio("Empresa", empresa);
		//FormatoCampos.verifyStringObligatorio("Email", email);// no se requiere enviar email en 2024
		
		// validacion de tamaño de strings
		FormatoCampos.verifyStringMinMaxLength(Personal.class, "nombre", "Nombre", nombre, 2);
		FormatoCampos.verifyStringMinMaxLength(Expositor.class, "nombre", "Empresa", empresa, 3);
		FormatoCampos.verifyStringMinMaxLength(Expositor.class, "email", "Email", email, 3);
		FormatoCampos.verifyStringMaxLength(Personal.class, "rut", "Rut", rut);
		FormatoCampos.verifyStringMaxLength(Personal.class, "observaciones", "Observaciones", observaciones);
		
		// validar rut (nacional)
		if (!extranjero)
			validateNumeroRutNacional();
	}

	public void validateEdit() throws ServiceLayerException {
		// validacion de campos obligatorios
		FormatoCampos.verifyStringObligatorio("Nombre", nombre);
		
		// validacion de tamaño de strings
		FormatoCampos.verifyStringMinMaxLength(Personal.class, "nombre", "Nombre", nombre, 2);
		FormatoCampos.verifyStringMaxLength(Personal.class, "rut", "Rut", rut);
		FormatoCampos.verifyStringMaxLength(Personal.class, "observaciones", "Observaciones", observaciones);
		
		// validar rut (nacional)
		if (!extranjero)
			validateNumeroRutNacional();
	}
	
	private void validateNumeroRutNacional() throws ServiceLayerException {
		if (rut != null) {
			Pattern pattern = Pattern.compile("\\d{7,8}");
			Matcher matcher = pattern.matcher(rut);
			if (matcher.matches())
				return;
		}
		throw new ServiceLayerException("Rut nacional no tiene formato correcto. Debe contener solo números, sin dígito verificador y de tamaño 7 a 9 dígitos");
	}
	
}
