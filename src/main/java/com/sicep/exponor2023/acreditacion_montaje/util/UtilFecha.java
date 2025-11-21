package com.sicep.exponor2023.acreditacion_montaje.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class UtilFecha {

	private UtilFecha() {
		throw new IllegalStateException("Utility class");
	}

	public static Timestamp ahora() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static Date getTomorrow() {
		Date ahora = ahora();
		return getNextDay(ahora);
	}

	public static Date getNextDay(Date fecha) {
		if (fecha == null)
			return null;
		Date fechaNextDay = null;
		Calendar c = Calendar.getInstance(FormatoFecha.locale);
		c.setTime(fecha);
		c.add(Calendar.DATE, 1);
		fechaNextDay = c.getTime();

		return fechaNextDay;
	}

	/**
	 * Retorna texto indicando la diferencia en dias o horas o minutos. No cambia el
	 * texto si la fecha de referencia es antes de la fecha objetivo o viceversa
	 * 
	 * @param fechaObjectivo
	 * @param fechaReferencia
	 * @return
	 */
	public static String getStringTiempoEntreFechas(Date fechaObjectivo, Date fechaReferencia) {
		if (fechaObjectivo == null)
			return null;

		long diffInMillies = Math.abs(fechaObjectivo.getTime() - fechaReferencia.getTime());
		// indicando la diferencia en dias
		long diffD = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		if (diffD >= 1) {// si queda mas de un dia de diferencia, el calculo no toma en cuenta las horas
			diffInMillies = Math
					.abs(getZeroTimeDate(fechaObjectivo).getTime() - getZeroTimeDate(fechaReferencia).getTime());
			diffD = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			if (diffD > 1)
				return diffD + " días";
			return "1 día";
		}

		// indicando la diferencia en horas si hay menos de un dia de diferencia
		long diffH = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		if (diffH > 1)
			return diffH + " horas";
		if (diffH == 1)
			return "1 hora";

		// menos de una hora de diferencia
		long diffM = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
		if (diffM > 1 || diffM == 0)
			return diffM + " minutos";

		return "1 minuto";
	}

	public static Date getFechaDiasAntes(Date fechaReferencia, int diasAntes) {
		if (fechaReferencia == null)
			return null;

		Date fechaObjetivo = null;
		int sumaDias = -1 * diasAntes;
		Calendar c = Calendar.getInstance(FormatoFecha.locale);
		c.setTime(fechaReferencia);
		c.add(Calendar.DATE, sumaDias);
		fechaObjetivo = c.getTime();

		return fechaObjetivo;
	}

	public static Date getFechaDiasDespues(Date fechaReferencia, int diasDespues) {
		Calendar c = Calendar.getInstance(FormatoFecha.locale);
		c.setTime(fechaReferencia);
		c.add(Calendar.DATE, diasDespues);
		return c.getTime();
	}

	public static int getAnioActual() {
		return Calendar.getInstance(FormatoFecha.locale).get(Calendar.YEAR);
	}

	/**
	 * retorna el año de la fecha de entrada
	 * 
	 * @param fecha
	 * @return
	 */
	public static int getAnio(Date fecha) {
		Calendar calendar = Calendar.getInstance(FormatoFecha.locale);
		calendar.setTime(fecha);
		return calendar.get(Calendar.YEAR);
	}

	public static List<Date> getListWeekendDatesInMonth(Date fecha) {
		List<Date> listaDateWeekendsDaysInMonth = new ArrayList<>();
		// parseado de la fecha de entrada
		Calendar c = Calendar.getInstance(FormatoFecha.locale);
		c.setTime(fecha);
		int year = c.get(Calendar.YEAR);// obtencion del año
		int month = c.get(Calendar.MONTH);// obtencion del mes

		// generando el date del primer dia del mes obtenido
		Calendar cal = new GregorianCalendar(year, month, 1);
		do {// recorrido dia por dia recopilando los dates de fin de semana
			int day = cal.get(Calendar.DAY_OF_WEEK);
			if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
				// agregando el date al listado de fechas fin de semana
				listaDateWeekendsDaysInMonth.add(cal.getTime());
			}
			// siguiente dia
			cal.add(Calendar.DAY_OF_YEAR, 1);
		} while (cal.get(Calendar.MONTH) == month);// fin cuando se terminen los dias del mes
		return listaDateWeekendsDaysInMonth;
	}

	public static List<Date> getListWeekendDatesBetweenDates(Date dateFrom, Date dateTo) {
		List<Date> listaDateWeekendsDaysInMonth = new ArrayList<>();
		// parseado de la fecha de entrada
		Calendar cFrom = Calendar.getInstance(FormatoFecha.locale);
		cFrom.setTime(dateFrom);

		Calendar cTo = Calendar.getInstance(FormatoFecha.locale);
		cTo.setTime(dateTo);
		// generando el date del primer dia del mes obtenido
		do {// recorrido dia por dia recopilando los dates de fin de semana
			int day = cFrom.get(Calendar.DAY_OF_WEEK);
			if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
				// agregando el date al listado de fechas fin de semana
				listaDateWeekendsDaysInMonth.add(cFrom.getTime());
			}
			// siguiente dia
			cFrom.add(Calendar.DAY_OF_YEAR, 1);
		} while (cFrom.compareTo(cTo) < 0);// fin cuando se terminen los dias del mes
		return listaDateWeekendsDaysInMonth;
	}

	/**
	 * Recibe como entrada una fecha y retorna la fecha pero sin hora, minutos,
	 * segundos Util para comparar por dias
	 * 
	 * @param fecha
	 * @return
	 */
	public static Date getZeroTimeDate(Date fecha) {
		Calendar calendar = Calendar.getInstance(FormatoFecha.locale);

		calendar.setTime(fecha);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	public static String getStringFecha(Date fecha, SimpleDateFormat sdf) {
		if (fecha == null)
			return null;
		return sdf.format(fecha);
	}

	public static long getCantDiasEntreFechas(Date fechaInicio, Date fechaTermino) {
		long diffInMillies = Math.abs(fechaInicio.getTime() - fechaTermino.getTime());
		// indicando la diferencia en dias
		long diffD = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		return diffD;
	}

	public static Date getFechaMinutosDespues(Date fechaReferencia, int minutosDespues) {
		if (fechaReferencia == null)
			return null;

		Date fechaObjetivo = null;
		Calendar c = Calendar.getInstance(FormatoFecha.locale);
		c.setTime(fechaReferencia);
		c.add(Calendar.MINUTE, minutosDespues);
		fechaObjetivo = c.getTime();

		return fechaObjetivo;
	}

	public static Date getFechaHorasAntes(Date fechaReferencia, int horasAntes) {
		if (fechaReferencia == null)
			return null;

		Date fechaObjetivo = null;
		Calendar c = Calendar.getInstance(FormatoFecha.locale);
		c.setTime(fechaReferencia);
		c.add(Calendar.HOUR, -1 * horasAntes);
		fechaObjetivo = c.getTime();

		return fechaObjetivo;
	}

	public static long getCantMinutosEntreFechas(Date fechaInicio, Date fechaTermino) {
		long diffInMillies = Math.abs(fechaInicio.getTime() - fechaTermino.getTime());
		// indicando la diferencia en dias
		long diffM = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
		return diffM;
	}

	public static Date getYesterday() {
		Date ahora = ahora();
		return getFechaDiasAntes(ahora, 1);
	}

	/**
	 * Devuelve el numero de dia de la semana. Se puede utilizar como comprobacion
	 * if (day == Calendar.SATURDAY)
	 */
	public static int getDayOfWeek(Date fecha) {
		Calendar cal = Calendar.getInstance(FormatoFecha.locale);
		cal.setTime(fecha);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static long getCantHorasEntreFechas(Date fechaInicio, Date fechaTermino) {
		long diffInMillies = Math.abs(fechaInicio.getTime() - fechaTermino.getTime());
		// indicando la diferencia en dias
		long diffH = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		return diffH;
	}

	public static Date getFechaHorasDespues(Date fechaReferencia, int horasDespues) {
		if (fechaReferencia == null)
			return null;

		Date fechaObjetivo = null;
		Calendar c = Calendar.getInstance(FormatoFecha.locale);
		c.setTime(fechaReferencia);
		c.add(Calendar.HOUR, horasDespues);
		fechaObjetivo = c.getTime();

		return fechaObjetivo;
	}

    public static Date getFechaMilisegundosDespues(Date fechaReferencia, int milisegundosDespues) {
        Date fechaObjetivo= null;
        Calendar c = Calendar.getInstance(FormatoFecha.locale);
        c.setTime(fechaReferencia);
        c.add(Calendar.MILLISECOND, milisegundosDespues);
        fechaObjetivo = c.getTime();

        return fechaObjetivo;
    }
}
