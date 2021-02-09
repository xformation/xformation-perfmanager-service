package com.synectiks.process.server.xformation.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.synectiks.process.server.xformation.service.CollectorServiceImpl;

public class PostGsJpaModule extends AbstractModule {
	private static final Logger LOG = LoggerFactory.getLogger(PostGsJpaModule.class);
			
	@Override
    protected void configure() {
        install(new JpaPersistModule("postGsPu"));
        bind(JPAInitializer.class).asEagerSingleton();
        bind(CollectorServiceImpl.class);
        
    }

    @Singleton
    private static class JPAInitializer {
        @Inject
        public JPAInitializer(final PersistService service) {
        	LOG.info("Starting JPA persist service");
            service.start();
        }
    }

}
