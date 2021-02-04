/*
 * 
 * 
 * */
package com.synectiks.process.server.perfservice.rest;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.synectiks.process.common.security.UserContext;
import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.perfservice.config.Constants;
import com.synectiks.process.server.perfservice.domain.Collector;
import com.synectiks.process.server.perfservice.service.CollectorServiceImpl;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.plugin.streams.StreamRule;
import com.synectiks.process.server.rest.resources.streams.StreamResource;
import com.synectiks.process.server.rest.resources.streams.requests.CreateStreamRequest;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

//@RequiresAuthentication
@Api(value = "Collector", description = "Manage collectors")
@Path("/collector")
public class CollectorController extends RestResource {
	private static final Logger logger = LoggerFactory.getLogger(CollectorController.class);

//    private CollectorService collectorService;
//    
//    @Inject
//    public CollectorController(CollectorService collectorService) {
//        this.collectorService = collectorService;
//    }
	@POST
	@Timed
	@ApiOperation(value = "Create a stream")
	@RequiresPermissions(RestPermissions.STREAMS_CREATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@AuditEvent(type = AuditEventTypes.STREAM_CREATE)
	public Response create(@ApiParam(name = "name", required = true) String name,
			@ApiParam(name = "type", required = true) String type,
			@ApiParam(name = "description", required = false) String description,
			@ApiParam(name = "userName", required = false) String userName, @Context UserContext userContext)
			throws ValidationException {
		logger.info(String.format("Request to create a Collector. Collector name : %s, type : %s", name, type));
		Collector collector = new Collector();
		collector.setName(name);
		collector.setType(type);
		collector.setDescription(description);
		if (!StringUtils.isBlank(userName)) {
			collector.setCreatedBy(userName);
			collector.setUpdatedBy(userName);
		} else {
			collector.setCreatedBy(Constants.SYSTEM_ACCOUNT);
			collector.setUpdatedBy(Constants.SYSTEM_ACCOUNT);
		}
		Instant now = Instant.now();
		collector.setCreatedOn(now);
		collector.setUpdatedOn(now);
		Injector injector = GuiceInjectorHolder.getInjector();
		CollectorServiceImpl cs = injector.getInstance(CollectorServiceImpl.class);
		cs.save(collector);
		return Response.ok().build();
	}

	@GET
	@Timed
	@ApiOperation(value = "Get a list of all collectors")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Collector> getAll() {
//        final List<Collector> list = this.collectorService.listAll();
//        for(Collector c: list) {
//        	LOG.info("Collector Id: "+c.getId());
//        }
//        return list;
		System.out.println("Staring getAll() .........");
		Injector injector = GuiceInjectorHolder.getInjector();
		System.out.println("Injector instance .........");
//    	injector.getBindings().forEach( (k,v) -> System.out.println("Key: " + k + ": Value: " + v));
//    	injector.getInstance(Key.get(PersistService.class, PostGsPu.class)).start();
//        injector.getInstance(Key.get(UnitOfWork.class, PostGsPu.class)).begin();

		CollectorServiceImpl cs = injector.getInstance(CollectorServiceImpl.class);
		System.out.println("CollectorServiceImpl instance ........." + cs);
		if (cs != null) {
			cs.listAll();
		} else {
			System.out.println("CollectorServiceImpl instance is null.........");
		}
//    	System.out.println("EntityManager object : "+em);

		System.out.println("Returning empty list");
//    	injector.getInstance(Key.get(PersistService.class, PostGsPu.class)).stop();
		return Collections.emptyList();
	}
}
