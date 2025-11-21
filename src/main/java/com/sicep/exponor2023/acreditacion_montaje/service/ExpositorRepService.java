package com.sicep.exponor2023.acreditacion_montaje.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sicep.exponor2023.acreditacion_montaje.dao.ExpositorRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Expositor;
import com.sicep.exponor2023.acreditacion_montaje.resources.ServiceLayerException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpositorRepService {
	private final ExpositorRepository expositorRepository;
	
	public Expositor getById(long id) throws ServiceLayerException {
		Optional<Expositor> optional = expositorRepository.findById(id);
		if (optional.isPresent())
			return optional.get();
		throw new ServiceLayerException("Expositor no existe");
	}
}
