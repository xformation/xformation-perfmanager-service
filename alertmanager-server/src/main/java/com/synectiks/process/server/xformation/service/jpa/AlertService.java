package com.synectiks.process.server.xformation.service.jpa;

import java.util.List;

import com.synectiks.process.server.xformation.domain.Alert;

public interface AlertService {
	
	public List<Alert> getAllAlerts();
}