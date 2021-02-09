package com.synectiks.process.server.xformation.service.jpa;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.Alert;

public class AlertServiceImpl implements AlertService {

	private static final Logger LOG = LoggerFactory.getLogger(AlertServiceImpl.class);

	
	private EntityManager entityManager = null;

	@Inject
    public AlertServiceImpl() {
		this.entityManager = GuiceInjectorHolder.getInjector().getInstance(EntityManager.class);
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Alert> getAllAlerts() {
		List<Alert> list = Collections.emptyList();
		try {
			LOG.info("Start getAllAlert");
			String query = "select a.id, a.guid, a.name, a.severity, a.monitorcondition, a.affectedresource, a.monitorservice, a.signaltype, a.brcsubscription, a.suppressionstate, a.resourcegroup, a.resources, a.firedtime, a.createdOn, a.updatedOn, a.alertState, a.client, a.clientUrl, a.description, a.details, a.incidentKey "
					+ " from Alert a";
			list = entityManager.createQuery(query).getResultList();
			for (Object o : list) {
				LOG.debug("Alert object : " + ((Alert)o).toString());
			}
			
		}catch(Exception e) {
			LOG.error("Exception : ",e);
		}
		
		LOG.info("End getAllAlert");
		return list;
	}

}