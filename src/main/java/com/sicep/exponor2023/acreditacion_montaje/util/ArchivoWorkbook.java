package com.sicep.exponor2023.acreditacion_montaje.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;

@Getter
@Setter
public class ArchivoWorkbook {
	private String nombreArchivo;
	private Workbook workbook;

	public ArchivoWorkbook(String nombreArchivo, Workbook workbook) {
		this.nombreArchivo = nombreArchivo;
		this.workbook = workbook;
	}

	public String getExtension() {
		return FilenameUtils.getExtension(nombreArchivo).toLowerCase();
	}
}
