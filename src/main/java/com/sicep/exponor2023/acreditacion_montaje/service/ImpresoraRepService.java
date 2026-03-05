package com.sicep.exponor2023.acreditacion_montaje.service;

import com.sicep.exponor2023.acreditacion_montaje.dao.ImpresoraRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.Impresora;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpresoraRepService {
	private final ImpresoraRepository impresoraRepository;
	public List<Impresora> list() {
		return impresoraRepository.findAll();
	}

	public Impresora getById(long id) throws ServiceLayerException {
		Impresora impresora = impresoraRepository.findById(id);
		if (impresora == null)
			throw new ServiceLayerException(String.format("No existe impresora con id '%d'", id));

		return impresora;
	}

}
