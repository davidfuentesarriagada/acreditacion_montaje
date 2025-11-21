package com.sicep.exponor2023.acreditacion_montaje.service;

import org.springframework.stereotype.Service;

import com.sicep.exponor2023.acreditacion_montaje.dao.PersonalRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Personal;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonalRepService {
	private final PersonalRepository personalRepository;

	public Personal getPersonalByCodigo(String codigo) throws ServiceLayerException {
		Personal personal = personalRepository.findByCodigoIgnoreCase(codigo);
		if (personal == null)
			throw new ServiceLayerException("Personal no existe");
		return personal;
	}
	
	public void validatePersonalUnico(String rut) throws ServiceLayerException {
		if (personalRepository.existsByRut(rut))
			throw new ServiceLayerException(String.format("Ya se encuentra registrado personal con rut '%s'", rut));
	}

	public Personal getPersonalByRut(String rut) throws ServiceLayerException {
		Personal personal = personalRepository.findByRut(rut);
		if (personal == null)
			throw new ServiceLayerException("Personal no existe");
		return personal;
	}
	

}
