package com.sicep.exponor2023.acreditacion_montaje.resources.email.service;

import com.sicep.exponor2023.acreditacion_montaje.resources.email.EmailBase;

public interface SendEmailService {
	public boolean sendEmail(EmailBase email);
}
