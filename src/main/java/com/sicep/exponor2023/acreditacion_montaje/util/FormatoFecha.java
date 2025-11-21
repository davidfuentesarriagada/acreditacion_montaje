package com.sicep.exponor2023.acreditacion_montaje.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatoFecha {

	private FormatoFecha() {
		throw new IllegalStateException("Utility class");
	}

	public static final Locale locale = new Locale.Builder().setLanguage("es").setRegion("CL").build();
	public static final SimpleDateFormat sdfDMY = new SimpleDateFormat("dd-MM-yyyy");
	public static final SimpleDateFormat sdfDMYHM = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	public static final SimpleDateFormat sdfDMYHM_slash = new SimpleDateFormat("dd/MM/yyyy HH:mm");// utilizado para los informe dicom de CMLB
	public static final SimpleDateFormat sdfDMYHMS = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public static final SimpleDateFormat sdfHM = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat sdfDMM = new SimpleDateFormat("dd 'de' MMMM", locale);

	public static SimpleDateFormat sdfDMY() {
		return sdfDMY;
	}

	public static SimpleDateFormat sdfDMYHM() {
		return sdfDMYHM;
	}

	public static Locale getLocale() {
		return locale;
	}

	/**
	 * Retorna null en caso de que la fecha de entrada sea null
	 */
	public static String getStringFecha(Date fecha, SimpleDateFormat sdf) {
		if (fecha == null)
			return null;
		return sdf.format(fecha);
	}

	public static Date getDateFecha(String fechaString, SimpleDateFormat sdf) {
		if (fechaString == null)
			return null;
		try {
			return sdf.parse(fechaString);
		}
		catch (ParseException e) {
			return null;
		}
	}
}
