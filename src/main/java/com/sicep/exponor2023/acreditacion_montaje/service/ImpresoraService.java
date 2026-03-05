package com.sicep.exponor2023.acreditacion_montaje.service;

import com.sicep.exponor2023.acreditacion_montaje.dao.ImpresoraRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.Impresora;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpresoraService {
	private final ImpresoraRepository impresoraRepository;
	private final ImpresoraRepService impresoraRepService;

	public Impresora register(String nombreImpresora) throws ServiceLayerException {
		if (impresoraRepository.existsByPrinterNameIgnoreCase(nombreImpresora))
			throw new ServiceLayerException(String.format("Ya existe impresora con nombre '%s'", nombreImpresora));

		Impresora impresora = new Impresora(nombreImpresora);
		impresoraRepository.save(impresora);
		return impresora;
	}

	public void delete(long idImpresora) throws ServiceLayerException {
		Impresora impresora = impresoraRepService.getById(idImpresora);
		impresoraRepository.delete(impresora);
	}

	public List<Impresora> list() {
		return impresoraRepService.list();
	}

}
