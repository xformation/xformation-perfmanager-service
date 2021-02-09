/*
 * */
package com.synectiks.process.server.xformation.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.xformation.domain.Alert;
import com.synectiks.process.server.xformation.service.jpa.AlertService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RequiresAuthentication
@Api(value = "Xformation Alert", description = "Manage all xformation alerts")
@Path("/xformation/alerts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlertController extends RestResource {
    private static final Logger LOG = LoggerFactory.getLogger(AlertController.class);

    @GET
    @Timed
    @ApiOperation(value = "Get a list of all alerts originated from grafana dashboards")
    public List<Alert> getAllAlerts() {
    	LOG.info("Start getAllAlert");
    	
    	AlertService cs = GuiceInjectorHolder.getInjector().getInstance(AlertService.class);
    	List<Alert> list = cs.getAllAlerts();
    	
    	LOG.info("End getAllAlert");
    	return list;
    }
}
