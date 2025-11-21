package com.sicep.exponor2023.acreditacion_montaje.resources.email;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicep.exponor2023.acreditacion_montaje.util.FormatoFecha;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilEncryptEncode;
import com.sicep.exponor2023.acreditacion_montaje.util.UtilFecha;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter @Setter
public class TokenEmailDesuscripcion {
	@JsonIgnore
	public static final String secret = "gatito";
	@JsonIgnore
	private final SimpleDateFormat sdf = FormatoFecha.sdfDMYHM;
	
	private String destinatario;
	private String modulo;
	private String subject;
	private String fechaToken;
	
	public TokenEmailDesuscripcion() {
		this.fechaToken = sdf.format(UtilFecha.ahora());
	}
	
	@JsonIgnore
	public String getToken() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(this);
			//log.info(json);
			return UtilEncryptEncode.encryptAndEncode(json, secret);
		}
		catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
		}
		return "";
	}
	
	@JsonIgnore
	public Date getFechaTokenDate() throws ParseException {
		return sdf.parse(fechaToken);
	}
}