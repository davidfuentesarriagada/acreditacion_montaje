package com.sicep.exponor2023.acreditacion_montaje.service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.sicep.exponor2023.acreditacion_montaje.dao.AsistenciaPersonalRepository;
import com.sicep.exponor2023.acreditacion_montaje.dao.ExpositorRepository;
import com.sicep.exponor2023.acreditacion_montaje.dao.PersonalRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.AsistenciaPersonal;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Expositor;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.FilterListaPersonal;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Personal;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.PersonalDTO;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.PersonalNoRegistradoDTO;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.PersonalScanQrDTO;
import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilFecha;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PersonalService {
	@Value("#{'${propiedad.carpetaLocal}'.concat('/tickets')}")
	private String rutaTickets;
	@Value("${propiedad.ver.marca-asistencia}")
	private boolean marcaAsistencia;
	@Value("${propiedad.printTicket}")
	private boolean printTicket;
	
	private final GeneradorCodigoAlfanumericoService generadorCodigoAlfanumericoService;
	private final PersonalRepository personalRepository;
	private final PersonalRepService personalRepService;
	private final QrGeneratorService qrGeneratorService;
	private final ExpositorRepository expositorRepository;
	private final TicketGeneratorService ticketGeneratorService;
	private final ImportPersonalFromAcreditacionExcelService importPersonalFromAcreditacionExcelService;
	private final ImportPersonalFromNubeExcelService importPersonalFromNubeExcelService;
	private final EmailService emailService;
	private final ExpositorRepService expositorRepService;
	private final AsistenciaPersonalRepository asistenciaPersonalRepository;
	
	public Map<String, Object> filter(FilterListaPersonal filtro) {
		Map<String, Object> respuesta = new HashMap<>();
		long totalNoFiltrado = personalRepository.count();
		Sort sort;
		if (filtro.hasOrderParam()) {
			//log.info("{}", filtro.getOrderParam());
			if (filtro.getOrderParam().equals("fechaRegistro")) {
				sort = Sort.by("createdDate");
			}
			else if (filtro.getOrderParam().equals("fechaImpresionTicket")) {
				sort = Sort.by("impresionTicketDate");
			}
			else if (filtro.getOrderParam().equals("fechaEnvioCredenciales")) {
				sort = Sort.by("envioCredencialesDate");
			}
			else {
				sort = Sort.by(filtro.getOrderParam());
			}
			sort = filtro.isDesc() ? sort.descending() : sort.ascending();
		}
		else
			sort = Sort.by("createdDate").descending();
		
	    PageRequest pageable = PageRequest.of(
	            filtro.getPageNumber(),
	            filtro.getLength(),
	            sort
	    );
	    
	    Page<Personal> ppersonal = personalRepository.filter(pageable, filtro);
		respuesta.put("draw", filtro.getDraw());
		respuesta.put("recordsTotal", totalNoFiltrado);
		respuesta.put("recordsFiltered", ppersonal.getTotalElements());
		respuesta.put("data", ppersonal.getContent());
		
		return respuesta;
	}
	
	
	public Personal registerPersonalIndividual(PersonalDTO dto, Usuario admin) throws ServiceLayerException {
		Personal personal = registerPersonal(dto, admin);
		if (marcaAsistencia) {
			marcarAsistencia(admin, personal);
		}
		
		// print inmediato si corresponde
		if (printTicket) {
			qrGeneratorService.generateQRCode(personal);
			ticketGeneratorService.generateTicket(personal);
			printTicket(personal);
		}

		return personal;
	}
	
	public Personal registerPersonal(PersonalDTO dto, Usuario admin) throws ServiceLayerException {
		dto.validate();
		// busqueda o creacion de la empresa expositora
		Expositor expositor = expositorRepository.findByNombreIgnoreCase(dto.getEmpresa());
		if (expositor == null) {
			// relleno aleatorio de email si vacio
			if (dto.getEmail() == null)
				dto.setEmail(String.format("%s@sicep.cl", generateCodigo4()));
			
			expositor = new Expositor(dto.getEmpresa(), dto.getEmail());
			expositorRepository.save(expositor);
		}
		
		// buscar personal si existe por rut
		// o por par nombre,empresa
		Personal personal = null;
		if (dto.getRut() != null)
			personal = personalRepository.findByRut(dto.getRut());
		
		if (personal == null) {
			// validar fecha de termino de registro
			Date ahora = UtilFecha.ahora();
			
			personal = new Personal();
			personal.setNombre(dto.getNombre());
			personal.setRut(dto.getRut());
			personal.getListaExpositor().add(expositor);
			personal.setExtranjero(dto.isExtranjero());
			personal.setObservaciones(dto.getObservaciones());
			personal.setNacionalidad(dto.getNacionalidad());
			
			// creacion o asignacion de codigo
			personal.setCodigo(dto.getCodigo() == null ? generateCodigo4() : dto.getCodigo());
			
			// audit
			personal.setCreatedDate(ahora);
			personal.setCreatedBy(admin);// null si es publico

			personalRepository.save(personal);
		}
		else {
			if (personal.getListaExpositor().contains(expositor))
				return personal;
			personal.getListaExpositor().add(expositor);
			personalRepository.save(personal);
		}
		return personal;
	}
	
	public void generateImagesPersonal(String codigo) throws ServiceLayerException {
		Personal personal = personalRepService.getPersonalByCodigo(codigo);
		qrGeneratorService.eliminar(personal);
		ticketGeneratorService.eliminar(personal);
		qrGeneratorService.generatePlantilla(personal);
	}
	
	public void generateTicket(String codigo) throws ServiceLayerException {
		Personal personal = personalRepService.getPersonalByCodigo(codigo);
		qrGeneratorService.generateQRCode(personal);
		ticketGeneratorService.generateTicket(personal);
	}
	
	public Personal editPersonal(Usuario usuario, String codigo, PersonalDTO dto) throws ServiceLayerException {
		dto.validateEdit();
		Personal personal = personalRepService.getPersonalByCodigo(codigo);
		
		// validar rut unico
		if (!personal.getRut().equals(dto.getRut()))
			personalRepService.validatePersonalUnico(dto.getRut());
		
		Date ahora = UtilFecha.ahora();
		personal.setNombre(dto.getNombre());
		personal.setRut(dto.getRut());
		personal.setExtranjero(dto.isExtranjero());
		personal.setObservaciones(dto.getObservaciones());
		
		// audit
		personal.setLastModifiedBy(usuario);
		personal.setLastModifiedDate(ahora);
		
		personalRepository.save(personal);
		
		
		// eliminacion de qr, plantilla y ticket
		qrGeneratorService.eliminar(personal);
		ticketGeneratorService.eliminar(personal);
		
		return personal;
	}
	
	private String generateCodigo4() {
		while (true) {
			String rnd = generadorCodigoAlfanumericoService.nextString();
			if (!personalRepository.existsByCodigoIgnoreCase(rnd))
				return rnd;
		}
	}

	public Personal generateQRCode(String codigo) throws ServiceLayerException {
		Personal personal = personalRepService.getPersonalByCodigo(codigo);
		qrGeneratorService.generateQRCode(personal);
		return personal;
	}

	public void deletePersonal(Usuario usuario, String codigo) throws ServiceLayerException {
		Personal personal = personalRepService.getPersonalByCodigo(codigo);
		// eliminacion de qr, plantilla y ticket
		qrGeneratorService.eliminar(personal);
		ticketGeneratorService.eliminar(personal);
		
		personalRepository.delete(personal);
	}

	public void importExpositoresFromAcreditacion(MultipartFile uploadedFile, Usuario usuario) throws ServiceLayerException {
		// validacion de la extension
		String fileName = uploadedFile.getOriginalFilename().toLowerCase();
		if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))
			throw new ServiceLayerException("Debe seleccionar un archivo con extensión xls o xlsx.");
		
		List<PersonalDTO> listaPersonalDto = importPersonalFromAcreditacionExcelService.importarExcel(uploadedFile);
		for (PersonalDTO dto : listaPersonalDto) {
			try {
				registerPersonal(dto, usuario);
			}
			catch(ServiceLayerException e) {
				// TODO manejo error
				log.error(e.getMessage(), e);
			}
		}
	}

	public void importExpositoresFromNube(MultipartFile uploadedFile, Usuario usuario) throws ServiceLayerException {
		// validacion de la extension
		String fileName = uploadedFile.getOriginalFilename().toLowerCase();
		if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))
			throw new ServiceLayerException("Debe seleccionar un archivo con extensión xls o xlsx.");
		
		List<PersonalDTO> listaPersonalDto = importPersonalFromNubeExcelService.importarExcel(uploadedFile);
		for (PersonalDTO dto : listaPersonalDto) {
			try {
				registerPersonal(dto, usuario);
			}
			catch(ServiceLayerException e) {
				// TODO manejo error
				log.error(e.getMessage(), e);
			}
		}
	}

	public Personal setPrintedTicketPersonal(String codigo) throws ServiceLayerException {
		Personal personal = personalRepService.getPersonalByCodigo(codigo);
		personal.setImpresionTicketDate(UtilFecha.ahora());
		personalRepository.save(personal);
		return personal;
	}

	public String getRutFromScanQr(String texto) throws ServiceLayerException {
		// soporte para pistoleo
		//httpsÑ--portal.sidiv.registrocivil.cl-docstatus_RUN¡18792910'5/type¡CEDULA/serial¡528838682/mrz¡528838682094102462710246
		Pattern pattern = Pattern.compile("RUN.(\\d+)");
		Matcher matcher = pattern.matcher(texto);
		// identificacion de rut 
		if (matcher.find())
			return matcher.group(1);
		return null;
	}

	public String getCodigoFromScanQr(String texto) throws ServiceLayerException {
		// ç000026httpÑ--www.reunionessicep.cl-ver-G13S
		Pattern pattern = Pattern.compile("personal.(\\w{4}).ver$");
		//log.info("{}", texto);
		Matcher matcher = pattern.matcher(texto);
		// identificacion de codigo 
		if (matcher.find())
			return matcher.group(1);
		
		return null;
	}

	public void sendEmailByExpositor(long idExpositor) throws ServiceLayerException {
		Expositor expositor = expositorRepService.getById(idExpositor);
		emailService.sendByExpositor(expositor);
	}

	public Personal printTicket(Usuario acreditador, String codigo) throws ServiceLayerException {
		Personal personal = personalRepService.getPersonalByCodigo(codigo);
		if (marcaAsistencia)
			marcarAsistencia(acreditador, personal);
		
		return printTicket(personal);
	}
	
	public Personal printTicket(Personal personal) throws ServiceLayerException {
		// creando el qr si no existe
		qrGeneratorService.generateQRCode(personal);
		
		// convirtiendo el ticket de png a pdf
		ticketGeneratorService.convertToPdf(personal);
		// habilitar
		RestTemplate restTemplate= new RestTemplate();
		try {
			restTemplate.getForEntity("http://127.0.0.1:8001/print/"+personal.getCodigo(), String.class);
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		
		personal.setImpresionTicketDate(UtilFecha.ahora());
		personalRepository.save(personal);
		
		return personal;
	}
	
	
	public void marcarAsistencia(Usuario acreditador, String codigo) throws ServiceLayerException {
		Personal personal = personalRepService.getPersonalByCodigo(codigo);
		marcarAsistencia(acreditador, personal);
	}
	
	public void marcarAsistencia(Usuario acreditador, PersonalScanQrDTO personaldto) throws ServiceLayerException {
		AsistenciaPersonal asistencia = new AsistenciaPersonal(personaldto, acreditador);
		asistenciaPersonalRepository.save(asistencia);
	}
	
	public PersonalScanQrDTO scanQr(Usuario admin, String texto) throws ServiceLayerException {
		PersonalScanQrDTO personaldto = null;
		// texto escaneado corresponde a qr en cedula de identidad
		String rut = getRutFromScanQr(texto);
		if (rut != null)
			personaldto = personalRepService.getPersonalByRut(rut);
		else {
			// texto escaneado corresponde a qr en invitacion
			String codigo = getCodigoFromScanQr(texto);
			if (codigo != null) {
				// En caso que personal tenga ticket impreso pero no se encuentre registrado en la base de datos, 
				// se debe registrar asistencia para el codigo. 
				// Aplica cuando el sistema se encuentre en modo de asistencia y no impresion
				if (marcaAsistencia && !printTicket) {
					personaldto = personalRepository.findByCodigoIgnoreCase(codigo);
					if (personaldto == null)
						personaldto = new PersonalNoRegistradoDTO(codigo);
				}
				else
					personaldto = personalRepService.getPersonalByCodigo(codigo);
			}
			// texto escaneado es directamente el codigo porque no se ajusta ni a formato rut ni qr invitacion
			else
				personaldto = personalRepService.getPersonalByCodigo(texto);
		}
		
		// si personal esta registrado, realizando actualizaciones de estados
		if (personaldto instanceof Personal) {
			Personal personal = (Personal)personaldto;
			personal.setTicketImpreso((personal.getImpresionTicketDate() != null));
			
			if (printTicket) {
				// saltar impresion si el ticket ha sido impreso anteriormente
				if (personal.getImpresionTicketDate() == null)
					printTicket(personal);
			}
		}
		
		if (marcaAsistencia)
			marcarAsistencia(admin, personaldto);
		
		return personaldto;
	}
	
	/**
	 * Eliminacion de QR y plantilla actual y creacion de una nueva para personal completo 
	 * @throws ServiceLayerException
	 */
	public void generatePlantillaQrToAll() throws ServiceLayerException {
		List<Personal> lpersonal = personalRepository.findAll();
		for (Personal personal : lpersonal) {
			qrGeneratorService.eliminar(personal);
			qrGeneratorService.generatePlantilla(personal);
		}
	}

	public boolean verificarRutExistente(String rut) {
		return personalRepository.existsByRutIgnoreCase(rut);
	}
	
}
