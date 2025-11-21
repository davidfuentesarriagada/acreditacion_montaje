package com.sicep.exponor2023.acreditacion_montaje.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Getter
@Setter
@MappedSuperclass
public class Auditable {
	@ManyToOne
	protected Usuario createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	protected Date createdDate;
	@ManyToOne
	protected Usuario lastModifiedBy;
	@Temporal(TemporalType.TIMESTAMP)
	protected Date lastModifiedDate;
}
