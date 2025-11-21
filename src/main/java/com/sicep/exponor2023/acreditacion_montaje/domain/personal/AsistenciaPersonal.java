package com.sicep.exponor2023.acreditacion_montaje.domain.personal;

import java.util.Date;

import com.sicep.exponor2023.acreditacion_montaje.domain.usuario.Usuario;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilFecha;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class AsistenciaPersonal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String codigoPersonal;// codigo del personal
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fecha;
    @ManyToOne
    private Usuario acreditador;
    
	public AsistenciaPersonal() {
		
	}

	public AsistenciaPersonal(PersonalScanQrDTO personaldto, Usuario acreditador) {
		this.fecha = UtilFecha.ahora();
		this.codigoPersonal = personaldto.getCodigo();
		this.acreditador = acreditador;
	}
    
}
