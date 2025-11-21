package com.sicep.exponor2023.acreditacion_montaje.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;

import jakarta.persistence.Column;

public class FormatoCampos {
	public FormatoCampos() {
		throw new IllegalStateException("Utility class");
	}

	// **** funciones de utilidades
	public static boolean isValidRut(String rut) {
		// verificacion de formato
		Pattern pattern = Pattern.compile("^\\d+-[0-9kK]$");
		Matcher matcher = pattern.matcher(rut);
		if (!matcher.matches())
			return false;

		// verificacion de dv
		try {
			rut = rut.toUpperCase();
			rut = rut.replace(".", "");
			rut = rut.replace("-", "");
			int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));// parte sin dv
			char dv = rut.charAt(rut.length() - 1);// solo dv

			int m = 0, s = 1;
			for (; rutAux != 0; rutAux /= 10)
				s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;

			if (dv == (char) (s != 0 ? s + 47 : 75))
				return true;
		}
		catch (Exception ignored) {
		}
		return false;
	}

	public static boolean isValidEmail(String email) {
		Pattern pattern = Pattern.compile("^[\\w-_.+]*[\\w-_.]@([\\w-_]+\\.)+[\\w]+[\\w]$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	// [0-9-+() ]
	public static boolean isValidFono(String fono) {
		Pattern pattern = Pattern.compile("^[0-9\\-+() ,]+$");
		Matcher matcher = pattern.matcher(fono);
		return matcher.matches();
	}

	public static boolean isValidURL(String url) {
		Pattern pattern = Pattern.compile(
				"^(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]+\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(url);
		return matcher.matches();
	}

	public static boolean isValidUsername(String username) {
		Pattern pattern = Pattern.compile("^\\w{4,100}$");
		Matcher matcher = pattern.matcher(username);
		return matcher.matches();
	}

	public static void verifyRut(String campo, String rut, String dv) throws ServiceLayerException {
		if (rut == null && dv == null)
			return;
		if (rut == null || dv == null)
			throw new ServiceLayerException("Celda '" + campo + "' sin número o sin dígito verificador");
		String rutCompleto = rut + "-" + dv;
		if (!isValidRut(rutCompleto))
			throw new ServiceLayerException("Celda '" + campo + "' no tiene formato correcto");
	}

	public static void verifyStringMaxLength(String nombreCampo, String valorCampo, int maxLength)
			throws ServiceLayerException {
		if (valorCampo == null)
			return;
		if (valorCampo.length() > maxLength)
			throw new ServiceLayerException("Campo '" + nombreCampo + "' no debe superar " + maxLength + " caracteres");
	}

	public static void verifyStringObligatorio(String nombreCampo, String valorCampo) throws ServiceLayerException {
		if (valorCampo == null)
			throw new ServiceLayerException("Campo '" + nombreCampo + "' es obligatorio");
	}

	public static String getDigitoVerificador(String rut) {
		int m = 0, s = 1;
		int rutInt = Integer.parseInt(rut);
		for (; rutInt != 0; rutInt /= 10)
			s = (s + rutInt % 10 * (9 - m++ % 6)) % 11;

		char dvChar = (char) (s != 0 ? s + 47 : 75);
		return dvChar + "";
	}

	public static void verifyStringMaxLength(Class<?> clase, String field, String nombreCampo, String valorCampo)
			throws ServiceLayerException {
		if (valorCampo == null)
			return;
		int maxLength = getMaxCaracteres(clase, field);
		if (valorCampo.length() > maxLength)
			throw new ServiceLayerException("Campo '" + nombreCampo + "' no debe superar " + maxLength + " caracteres");
	}

	public static void verifyStringMinMaxLength(Class<?> clase, String field, String nombreCampo, String valorCampo, int minLength)
			throws ServiceLayerException {
		if (valorCampo == null)
			return;
		int maxLength = getMaxCaracteres(clase, field);
		if (valorCampo.length() < minLength || valorCampo.length() > maxLength)
			throw new ServiceLayerException("Campo '" + nombreCampo + "' debe tener entre " + minLength + " a " + maxLength + " caracteres");
	}

	public static Integer getMaxCaracteres(Class<?> clase, String field) {
		try {
			Column column = clase.getDeclaredField(field).getAnnotation(Column.class);
			// 255 caracteres es la restriccion por defecto
			if (column == null)
				return 255;
			return column.length();
		}
		catch (NoSuchFieldException | SecurityException ignore) {
		}
		
		return null;
	}
}
