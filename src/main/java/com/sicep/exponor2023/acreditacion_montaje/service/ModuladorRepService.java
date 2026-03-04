package com.sicep.exponor2023.acreditacion_montaje.service;

import com.sicep.exponor2023.acreditacion_montaje.dao.ModuladorRepository;
import com.sicep.exponor2023.acreditacion_montaje.domain.personal.Modulador;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ModuladorRepService {
	private final ModuladorRepository moduladorRepository;

	public Modulador getById(Long id) {
		return moduladorRepository.findById(id).orElse(null);
	}

	public List<Modulador> findAll() {
		return this.moduladorRepository.findAll();
	}
}
