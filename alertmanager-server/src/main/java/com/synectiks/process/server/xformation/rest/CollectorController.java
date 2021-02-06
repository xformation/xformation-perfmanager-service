/*
 * */
package com.synectiks.process.server.xformation.rest;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Injector;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.xformation.domain.Collector;
import com.synectiks.process.server.xformation.service.jpa.CollectorService;
import com.synectiks.process.server.xformation.service.jpa.CollectorServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

//@RequiresAuthentication
@Api(value = "Collector", description = "Manage collectors")
@Path("/collector")
public class CollectorController extends RestResource {
    private static final Logger LOG = LoggerFactory.getLogger(CollectorController.class);

//    private CollectorService collectorService;
//    
//    @Inject
//    public CollectorController(CollectorService collectorService) {
//        this.collectorService = collectorService;
//    }
    
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
    	System.out.println("CollectorServiceImpl instance ........."+cs);
    	if(cs != null) {
    		cs.listAll();
    	}else {
    		System.out.println("CollectorServiceImpl instance is null.........");
    	}
//    	System.out.println("EntityManager object : "+em);
    	
    	System.out.println("Returning empty list");
//    	injector.getInstance(Key.get(PersistService.class, PostGsPu.class)).stop();
    	return Collections.emptyList();
    }
}
