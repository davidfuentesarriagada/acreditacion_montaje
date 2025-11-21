package com.sicep.exponor2023.acreditacion_montaje.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilString {

	private UtilString() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Truncado del texto de ingreso en la cantidad de caracteres exacta ingresada
	 * como longitud maxima
	 * 
	 * @param texto          texto a truncar
	 * @param longitudMaxima cantidad maxima de caracteres del texto truncado
	 * @return texto truncado con longitudMaxima de caracteres maximo
	 */
	public static String truncate(String texto, int longitudMaxima) {
		if (texto != null && longitudMaxima > 0 && texto.length() > longitudMaxima)
			return texto.substring(0, longitudMaxima);

		return texto;
	}

	/**
	 * Truncado del texto de ingreso en maximo 255 caracteres
	 *
	 * @param texto texto a truncar
	 * @return texto truncado con 255 caracteres maximo
	 */
	public static String truncate(String texto) {
		return truncate(texto, 255);
	}

	/**
	 * retorna string de numero con decimales y separacion de miles
	 * 
	 * @return
	 */
	public static String numero(Number valor) {
		if (valor == null)
			return null;
		NumberFormat formato = NumberFormat.getInstance(FormatoFecha.getLocale());
		return formato.format(valor);
	}

	/**
	 * retorna string de numero con la cantidad de decimales de entrada y separacion
	 * de miles si el numero no tiene decimales o tiene menos decimales se incluyen
	 * 0 despues de la coma
	 * 
	 * @return
	 */
	public static String decimal(Number valor, int cantDecimales) {
		if (valor == null)
			return null;

		String pattern = "###,##0";
		if (cantDecimales > 0) {
			pattern += ".";
			for (int i = 0; i < cantDecimales; i++)
				pattern += "0";
		}
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(FormatoFecha.getLocale());
		decimalFormat.applyPattern(pattern);
		return decimalFormat.format(valor);
	}

	public static String decimal2d(Number valor) {
		return decimal(valor, 2);
	}

	/**
	 * Convierte un texto proveniente desde la vista a string haciendo trim y
	 * convirtiendo a null si el texto esta vacio
	 * 
	 * @param texto texto provendiente desde la vista
	 * @return texto con trim, nulo si es vacio
	 */
	public static String textoDesdeVista(String texto) {
		if (texto == null)
			return texto;
		texto = texto.trim();
		if (texto.length() == 0)
			return null;
		return texto;
	}

	/**
	 * Convierte un texto proveniente desde la vista a string haciendo trim y
	 * convirtiendo a null si el texto esta vacio. Si no es nulo, lo deja en
	 * minusculas
	 * 
	 * @param texto texto provendiente desde la vista
	 * @return texto con trim, nulo si es vacio
	 */
	public static String textoDesdeVistaToLowerCase(String texto) {
		if (texto == null)
			return texto;
		texto = texto.trim();
		if (texto.length() == 0)
			return null;
		return texto.toLowerCase();
	}

	/**
	 * Convierte un texto proveniente desde la vista a string haciendo trim y
	 * convirtiendo a null si el texto esta vacio. Si no es nulo, lo deja en
	 * mayusculas
	 * 
	 * @param texto texto provendiente desde la vista
	 * @return texto con trim, nulo si es vacio
	 */
	public static String textoDesdeVistaToUpperCase(String texto) {
		if (texto == null)
			return texto;
		texto = texto.trim();
		if (texto.length() == 0)
			return null;
		return texto.toUpperCase();
	}

	public static String fecha(Date fecha, SimpleDateFormat sdf) {
		if (fecha == null)
			return null;
		return sdf.format(fecha);
	}

}
