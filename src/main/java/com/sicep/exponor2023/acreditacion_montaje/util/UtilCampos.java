package com.sicep.exponor2023.acreditacion_montaje.util;

import java.util.Date;

import jakarta.persistence.Column;

public class UtilCampos {
	private UtilCampos() {
		throw new IllegalStateException("Utility class");
	}

	public static boolean hasChanges(Object original, Object nuevo) {
		if (original instanceof Date && nuevo instanceof Date)
			return ((Date) original).compareTo((Date) nuevo) != 0;

		return (original != null || nuevo != null) && (original == null || (!original.equals(nuevo)));
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
