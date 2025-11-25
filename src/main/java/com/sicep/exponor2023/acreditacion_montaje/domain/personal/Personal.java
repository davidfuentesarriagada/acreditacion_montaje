package com.sicep.exponor2023.acreditacion_montaje.domain.personal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sicep.exponor2023.acreditacion_montaje.domain.Auditable;
import com.sicep.exponor2023.acreditacion_montaje.util.FormatoCampos;
import com.sicep.exponor2023.acreditacion_montaje.util.FormatoFecha;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({ "createdBy", "lastModifiedBy" })
public class Personal extends Auditable implements PersonalScanQrDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(nullable = false, length = 50)
	private String nombre;
	@Column(length = 30)
	private String rut;// rut sin digito verificador
	@Column(length = 30)
	private String nacionalidad;
	private boolean extranjero = false;
	
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = { @JoinColumn(name = "personal_id") }, inverseJoinColumns = { @JoinColumn(name = "empresa_id") })
	private List<Expositor> listaExpositor = new ArrayList<>();
	
	@Column(length = 4)
	private String codigo;// correspondiente al codigo QR
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date impresionTicketDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date envioCredencialesDate;
	
	private String observaciones;
	
	@Transient
	private boolean ticketImpreso = false;
	
	public String getFechaRegistro() {
		return UtilString.fecha(createdDate, FormatoFecha.sdfDMYHM);
	}
	
	public String getRutCompleto() {
		if (rut == null || extranjero)
			return rut;
		return String.format("%s-%s", rut, FormatoCampos.getDigitoVerificador(rut));
	}

	public String getFechaImpresionTicket() {
		return UtilString.fecha(impresionTicketDate, FormatoFecha.sdfDMYHM);
	}

	public String getFechaEnvioCredenciales() {
		return UtilString.fecha(envioCredencialesDate, FormatoFecha.sdfDMYHM);
	}

	@Override
	public Long getIdPersonal() {
		return id;
	}
	
	public String getDigitoVerificador() {
		if (extranjero)
			return null;
		return FormatoCampos.getDigitoVerificador(rut);
	}

	public boolean isExtranjero() {
		if(this.rut.contains("EXT")) return true;
		return false;
	}

}
