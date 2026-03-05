package com.sicep.exponor2023.acreditacion_montaje.dao;

import com.sicep.exponor2023.acreditacion_montaje.domain.Impresora;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImpresoraRepository extends JpaRepository<Impresora, Long> {
	boolean existsByPrinterNameIgnoreCase(String printerName);
	Impresora findById(long id);
}