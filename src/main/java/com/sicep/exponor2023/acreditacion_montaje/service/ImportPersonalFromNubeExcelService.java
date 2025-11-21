package com.sicep.exponor2023.acreditacion_montaje.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sicep.exponor2023.acreditacion_montaje.domain.personal.PersonalDTO;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilString;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportPersonalFromNubeExcelService {
	public List<PersonalDTO> importarExcel(MultipartFile uploadedFile) throws ServiceLayerException {
		try {
			String fileName = uploadedFile.getOriginalFilename().toLowerCase();
			String extension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
			log.info("{}", extension);
			return importarExcel(uploadedFile.getInputStream(), extension );
		}
		catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ServiceLayerException(e.getMessage());
		}
	}
	
	
	public List<PersonalDTO> importarExcel(InputStream inputStream, String extension) {
		Workbook workbook = null;
		try {
			if (extension.equals("xls"))
				workbook = new HSSFWorkbook(inputStream);// xls
			else if (extension.equals("xlsx"))
				workbook = new XSSFWorkbook(inputStream);// xlsx
			return hojaExpositores(workbook, workbook.getSheetAt(0));
		}
		catch(IOException | ServiceLayerException e) {
			log.error(e.getMessage(), e);
		}
		finally {
			if (workbook != null) {
				try {
					workbook.close();
				} 
				catch (IOException ignore) {}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} 
				catch (Exception ignore) {}
			}
		}
		
		return new ArrayList<>();
	}
	
	private List<PersonalDTO> hojaExpositores(Workbook workbook, Sheet hoja) throws ServiceLayerException {
		CellReference cellCodigoPersonal = new CellReference("B4");
		CellReference cellNombrePersonal = new CellReference("C4");
		CellReference cellRutPersonal = new CellReference("D4");
		CellReference cellExtranjeroPersonal = new CellReference("E4");
		
		CellReference cellRefExpositor = new CellReference("H4");
		CellReference cellRefEmail = new CellReference("I4");
		
		CellReference cellRefObservaciones = new CellReference("L4");
		
		List<PersonalDTO> resultado = new ArrayList<>();
		for (int nFil = 4; nFil <= hoja.getLastRowNum(); nFil++) {
			Row row = hoja.getRow(nFil);
			if (row == null)
				continue;

			String codigo = getString(row, cellCodigoPersonal.getCol());
			String nombre = getString(row, cellNombrePersonal.getCol());
			String rut = getString(row, cellRutPersonal.getCol());
			String extranjero = getString(row, cellExtranjeroPersonal.getCol());
			String empresa = getString(row, cellRefExpositor.getCol());
			String email = getString(row, cellRefEmail.getCol());
			String observaciones = getString(row, cellRefObservaciones.getCol());
			
			if (rut != null && rut.contains("-"))
				rut = rut.split("-")[0];

			// generando el dto para el registro en el service
			PersonalDTO dto = new PersonalDTO();
			dto.setCodigo(codigo);
			dto.setEmail(email);
			dto.setEmpresa(empresa);
			dto.setNombre(nombre);
			dto.setRut(rut);
			dto.setExtranjero(extranjero.equals("SI"));
			dto.setObservaciones(observaciones);
			
			resultado.add(dto);
		}
		
		return resultado;
	}

	private String getString(Row row, int nCol) {
		Cell cell = row.getCell(nCol);
		if (cell == null || !cell.getCellType().equals(CellType.STRING))
			return null;
		return UtilString.textoDesdeVista(cell.getStringCellValue());
	}
	
}
