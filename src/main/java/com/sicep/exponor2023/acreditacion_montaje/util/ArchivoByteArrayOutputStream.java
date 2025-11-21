package com.sicep.exponor2023.acreditacion_montaje.util;

import lombok.Getter;
import lombok.Setter;
import java.io.ByteArrayOutputStream;

@Getter
@Setter
public class ArchivoByteArrayOutputStream {
	private ByteArrayOutputStream baos;
	private String nombreArchivo;// nombre de archivo sin extension
	private String extension;
	private String contentType;

	public ArchivoByteArrayOutputStream(ByteArrayOutputStream baos) {
		this.baos = baos;
	}

	public String getNombreArchivoCompleto() {
		return nombreArchivo + "." + extension;
	}
}