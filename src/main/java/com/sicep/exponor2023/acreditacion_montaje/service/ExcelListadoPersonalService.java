package com.sicep.exponor2023.acreditacion_montaje.service;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sicep.exponor2023.acreditacion_montaje.dao.PersonalRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.PersonalListaExcelDTO;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.util.ArchivoWorkbook;
import com.sicep.exponor2023.acreditacion_montaje.util.FormatoFecha;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilExcel;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilFecha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExcelListadoPersonalService {
	private final PersonalRepository personalRepository;
	
	public ArchivoWorkbook exportar(Usuario usuario) throws ServiceLayerException {
		List<PersonalListaExcelDTO> lpersonal = personalRepository.findAllForExcel();
		try {
			Date ahora = UtilFecha.ahora();
			Workbook workbook = new XSSFWorkbook();
			ArchivoWorkbook archivo = new ArchivoWorkbook("personal montaje " + FormatoFecha.sdfDMY.format(ahora) + ".xlsx",
					workbook);
			Sheet hoja = workbook.createSheet("personal registrado");
			CellStyle styleCeldaHeader = workbook.createCellStyle();
			Font negrita = workbook.createFont();
			negrita.setBold(true);
			styleCeldaHeader.setFont(negrita);
			UtilExcel.addBorderToCellStyle(styleCeldaHeader);
			
			CellStyle styleCeldaFechaDYMHM = UtilExcel.createCellStyleDateDMYHM(workbook);
			
			// insercion del encabezado de la tabla
			int nFila = 0;
			String[] listaEncabezado = { "Fecha registro", "Código", "Nombre Completo",
					"Rut", "extranjero", "Fecha Email", "Fecha Ticket", "Empresa expositora", 
					"Email expositor", "Última asistencia", "Acreditador", "Observaciones"};
			nFila = addInformacionCreacion(usuario, hoja, nFila, listaEncabezado.length);

			nFila++;
			addHeaderToTable(hoja, nFila, listaEncabezado, styleCeldaHeader);
			if (lpersonal.isEmpty())
				return archivo;
			int fila1 = nFila;
			nFila++;

			// recorrido por los personals registrados
			for (PersonalListaExcelDTO dto : lpersonal) {
				int nCol = 0;
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getFechaRegistroDate(), styleCeldaFechaDYMHM);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getCodigo(), null);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getNombre(), null);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getRutCompleto(), null);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.isExtranjero() ? "SI" : "NO", null);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getFechaEmailDate(), styleCeldaFechaDYMHM);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getFechaTicketDate(), styleCeldaFechaDYMHM);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getNombreEmpresaExpositora(), null);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getEmailEmpresaExpositora(), null);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getFechaLastAsistenciaDate(), styleCeldaFechaDYMHM);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getAcreditador(), null);
				UtilExcel.fillCell(hoja, nCol++, nFila, dto.getObservaciones(), null);
				nFila++;
			}

			hoja.setAutoFilter(new CellRangeAddress(fila1, nFila - 1, 0, listaEncabezado.length - 1));

			// ajuste de tamaño de columnas
			for (int nCol = 0; nCol < hoja.getRow(fila1).getLastCellNum(); nCol++)
				hoja.autoSizeColumn(nCol);

			// generando el documento descargable
			return archivo;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServiceLayerException("Se produjo un error al realizar la operación.", e);
		}
	}

	private void addHeaderToTable(Sheet hoja, int nFila, String[] listaEncabezado, CellStyle styleCeldaHeader) {
		for (int nCol = 0; nCol < listaEncabezado.length; nCol++) {
			String encabezado = listaEncabezado[nCol];
			UtilExcel.fillCell(hoja, nCol, nFila, encabezado, styleCeldaHeader);
		}
	}

	private int addInformacionCreacion(Usuario usuario, Sheet hoja, int nFila, int ultimaColumna) {
		int nCol = 0;
		CellRangeAddress regionCeldas;
		UtilExcel.fillCell(hoja, nCol, nFila, "Fecha de descarga: " + FormatoFecha.sdfDMYHMS.format(UtilFecha.ahora()),
				null);
		regionCeldas = new CellRangeAddress(nFila, nFila, 0, ultimaColumna - 1);
		hoja.addMergedRegion(regionCeldas);
		nFila++;

		UtilExcel.fillCell(hoja, nCol, nFila, "Usuario: " + usuario.getNombre(), null);
		regionCeldas = new CellRangeAddress(nFila, nFila, 0, ultimaColumna - 1);
		hoja.addMergedRegion(regionCeldas);
		nFila++;
		return nFila;
	}
	
}
