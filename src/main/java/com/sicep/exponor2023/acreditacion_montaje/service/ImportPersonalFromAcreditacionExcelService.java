package com.sicep.exponor2023.acreditacion_montaje.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.sicep.exponor2023.acreditacion_montaje.util.FormatoCampos;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilString;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportPersonalFromAcreditacionExcelService {
	public List<PersonalDTO> importarExcel(MultipartFile uploadedFile) throws ServiceLayerException {
		try {
			String fileName = uploadedFile.getOriginalFilename().toLowerCase();
			String extension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
			//log.info("{}", extension);
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
		CellReference cellRefExpositor = new CellReference("C2");
		CellReference cellRefEmail = new CellReference("J2");
		CellReference cellRefPersonal = new CellReference("K2");
		
		List<PersonalDTO> resultado = new ArrayList<>();
		for (int nFil = 1; nFil <= hoja.getLastRowNum(); nFil++) {
			Row row = hoja.getRow(nFil);
			if (row == null)
				continue;
			String expositor = getString(row, cellRefExpositor.getCol());
			String email = getString(row, cellRefEmail.getCol());
			String trabajadores = getString(row, cellRefPersonal.getCol());
			if (trabajadores == null)
				continue;
			resultado.addAll(parseTrabajadores(expositor, email, trabajadores));
			//log.info("{} {} {}", expositor, email, trabajadores);
		}
		
		return resultado;
	}

	private String getString(Row row, int nCol) {
		Cell cell = row.getCell(nCol);
		if (cell == null || !cell.getCellType().equals(CellType.STRING))
			return null;
		return UtilString.textoDesdeVista(cell.getStringCellValue());
	}
	
	private List<PersonalDTO> parseTrabajadores(String empresa, String email, String contenido) {
		//log.info(contenido);
		List<PersonalDTO> listaDto = new ArrayList<>();
		Pattern pattern = Pattern.compile("Nombre: (.+?) ;  Rut: (.*?) /");
		Matcher matcher = pattern.matcher(contenido);
		while (matcher.find()) {
			String nombre = UtilString.textoDesdeVista(matcher.group(1));
			String rut = UtilString.textoDesdeVista(matcher.group(2));
			boolean extranjero = true;// segun formato de rut
			rut = UtilString.textoDesdeVistaToUpperCase(rut);
			if (rut != null) {
				rut = rut.replace(".", "").replace(",", "");
				Integer numeroRut = null;
				// obteniendo solo la parte numerica del rut
				// y eliminacion de 0 a la izquierda
				if (rut.contains("-")) {
					String subrut = rut.split("-")[0];
					subrut = UtilString.textoDesdeVista(subrut);
					// verificacion de solo numero o no tomar accion
					Pattern patternNumeros = Pattern.compile("\\d+");
					Matcher matcherNumeros = patternNumeros.matcher(subrut);
					if (matcherNumeros.matches())
						numeroRut = Integer.parseInt(subrut);
				}
				// identificacion de ruts con dv sin guion
				else {
					String subrut = rut;
					//log.info("{}", rut);
					// verificacion si ultimo digo corresponde al dv
					if (subrut.length() == 9) {
						//log.info("{}", rut);
						String num = subrut.substring(0, subrut.length() - 1);
						String dv = subrut.substring(subrut.length() - 1);
						String completo = String.format("%s-%s", num, dv);
						if (FormatoCampos.isValidRut(completo))
							subrut = num;
					}
					try {
						numeroRut = Integer.parseInt(subrut);
					}
					catch(NumberFormatException ignore) {}
				}
				
				// verificacion si numero tiene formato de rut
				if (numeroRut != null && numeroRut > 1000000 && numeroRut < 100000000) { // 1.000.000 < rut < 100.000.000
					rut = numeroRut+"";
					extranjero = false;
				}
			}// rut de entrada no nulo

			if(rut == null){
				log.error("RUT NULO para trabajador {} de empresa {}", nombre, empresa);
				continue;
			}

			PersonalDTO dto = new PersonalDTO();
			dto.setEmail(email);
			dto.setEmpresa(empresa);
			dto.setNombre(nombre);
			dto.setRut(extranjero ? "EXT".concat(rut) : rut);
			dto.setExtranjero(extranjero);
			
			listaDto.add(dto);
		}
		
		return listaDto;
	}

}
