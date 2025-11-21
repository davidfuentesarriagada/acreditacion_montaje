package com.sicep.exponor2023.acreditacion_montaje.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilExcel {
	public UtilExcel() {
		throw new IllegalStateException("Utility class");
	}

	public static Cell fillCell(Sheet hoja, CellReference cellReference, Object valor) {
		return fillCell(hoja, cellReference.getCol(), cellReference.getRow(), valor, null);
	}

	public static Cell fillCell(Sheet hoja, CellReference cellReference, Object valor, CellStyle estilo) {
		return fillCell(hoja, cellReference.getCol(), cellReference.getRow(), valor, estilo);
	}

	public static Cell fillCell(Sheet hoja, int nCol, int nFil, Object valor) {
		return fillCell(hoja, nCol, nFil, valor, null);
	}

	/**
	 * Asigna contenido a una celda especifica de la plantilla. Se identifica el
	 * tipo de contenido y asigna el valor correspondiente. (ej. numerico, texto,
	 * fecha, etc.)
	 * 
	 * @param hoja   hoja de la plantilla donde esta ubicada la celda
	 * @param nCol   indice numerico de la columna de la celda. Comienza por 0
	 *               (columna A corresponde a 0)
	 * @param nFil   indice numerico de la fila de la celda. Comienza por 0
	 * @param valor  instancia de objeto que es el valor que se asigna a la celda
	 * @param estilo si no es nulo, se puede personalizar el color, tamaño de letra
	 * @return
	 */
	public static Cell fillCell(Sheet hoja, int nCol, int nFil, Object valor, CellStyle estilo) {
		Cell celda = null;
		// obteniendo o creando la fila y columna correspondiente
		Row fila = hoja.getRow(nFil);
		if (fila == null)
			fila = hoja.createRow(nFil);
		celda = fila.getCell(nCol);
		if (celda == null)
			celda = fila.createCell(nCol);

		// asignando el estilo de la celda
		if (estilo != null)
			celda.setCellStyle(estilo);

		// asignando el contenido de la celda
		if (valor != null) {
			// rellenando la celda dependiendo del tipo del valor como parametro
			if (valor instanceof Date)
				celda.setCellValue((Date) valor);
			else if (valor instanceof Long)
				celda.setCellValue((Long) valor);
			else if (valor instanceof Integer)
				celda.setCellValue((Integer) valor);
			else if (valor instanceof Float)
				celda.setCellValue((Float) valor);
			else if (valor instanceof Double)
				celda.setCellValue((Double) valor);
			else
				celda.setCellValue((String) valor);
		}

		return celda;
	}

	/**
	 * Analiza la entrada y retorna una instancia de Numerica si corresponde. Se
	 * utiliza para asignar formato numerico al contenido de una celda si
	 * corresponde
	 *
	 * @param valor objeto de entrada que se verifica si corresponde a numero
	 * @return instancia numerica
	 */
	public static Object parseCell(Object valor) {
		if (!(valor instanceof String))
			return valor; // no es string no se puede parsear
		String strValor = ((String) valor).trim();
		if (strValor.equals(""))
			return strValor; // string vacio

		Pattern pattern;
		Matcher m;

		pattern = Pattern.compile("\\-?\\d+([,\\.]\\d+)?");
		m = pattern.matcher(strValor);
		if (!m.matches())
			return strValor; // no tiene valor numerico

		// verificacion de numero entero
		pattern = Pattern.compile("\\-?\\d+");
		m = pattern.matcher(strValor);
		if (m.matches())
			return Long.parseLong(strValor); // retorna un Long (si es int es incluido aqui)

		// verificacion de numero decimal
		strValor = strValor.replace(",", ".");// el punto decimal se asigna como ',' al formatear con DF_2D
		pattern = Pattern.compile("\\-?\\d+\\.\\d+");
		m = pattern.matcher(strValor);
		if (m.matches())
			return Double.parseDouble(strValor); // retorna un Double

		return valor;
	}

	/**
	 * Para generar el parametro DataFormat se debe generar de la siguiente forma:
	 * DataFormat dataFormat= workbook.createDataFormat(); y se puede reutilizar
	 * para crear otros estilos
	 * 
	 * @param workbook
	 * @param dataFormat
	 * @return
	 */
	public static CellStyle createCellStyleText(Workbook workbook, DataFormat dataFormat) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(dataFormat.getFormat("@"));

		return cellStyle;
	}

	public static void addBorderToCellRegion(CellRangeAddress cellRangeAddress, Sheet hoja) {
		RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, hoja);
		RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, hoja);
		RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, hoja);
		RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, hoja);
	}

	/**
	 * No muestra decimales ni separador de miles Para generar el parametro
	 * DataFormat se debe generar de la siguiente forma: DataFormat dataFormat=
	 * workbook.createDataFormat(); y se puede reutilizar para crear otros estilos
	 * 
	 * @param workbook
	 * @param dataFormat
	 * @return
	 */
	public static CellStyle createCellStyleNumberNoSeparator0d(Workbook workbook, DataFormat dataFormat) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(dataFormat.getFormat("0"));
		return cellStyle;
	}

	/**
	 * Siempre muestra 2 decimales aunque el numero sea entero, con separador de
	 * miles Para generar el parametro DataFormat se debe generar de la siguiente
	 * forma: DataFormat dataFormat= workbook.createDataFormat(); y se puede
	 * reutilizar para crear otros estilos
	 * 
	 * @param workbook
	 * @param dataFormat
	 * @return
	 */
	public static CellStyle createCellStyleNumber2Decimal(Workbook workbook, DataFormat dataFormat) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));
		return cellStyle;
	}

	/**
	 * Permite crear un estilo de celda para fechas cuyo despliegue depende del
	 * patron de entrada. (ej. pattern = "dd/mm/yyyy hh:mm"
	 * 
	 * @param workbook
	 * @param pattern  patron de despleigue de la fecha
	 * @return
	 */
	public static CellStyle createCellStyleDate(Workbook workbook, String pattern) {
		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle cellStyle = workbook.createCellStyle();
		short format = creationHelper.createDataFormat().getFormat(pattern);
		cellStyle.setDataFormat(format);

		return cellStyle;
	}

	public static CellStyle createCellStyleDateDMY(Workbook workbook) {
		return createCellStyleDate(workbook, "dd-mm-yyyy");
	}

	public static CellStyle createCellStyleDateDMYHM(Workbook workbook) {
		return createCellStyleDate(workbook, "dd-mm-yyyy hh:mm");
	}

	public static CellStyle createCellStyleDateDMYHMSS(Workbook workbook) {
		return createCellStyleDate(workbook, "dd-mm-yyyy hh:mm:ss");
	}

	/* Nunca muestra decimales, con separador de miles
	 * Para generar el parametro DataFormat se debe generar de la siguiente forma:
	 * DataFormat dataFormat= workbook.createDataFormat();
	 * y se puede reutilizar para crear otros estilos
	 * @param workbook
	 * @param dataFormat
	 * @return
	 */
	public static CellStyle createCellStyleNumber0Decimal(Workbook workbook, DataFormat dataFormat) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(dataFormat.getFormat("#,##0"));
		return cellStyle;
	}

	public static void addBorderToCellStyle(CellStyle cellStyle) {
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
	}
}
