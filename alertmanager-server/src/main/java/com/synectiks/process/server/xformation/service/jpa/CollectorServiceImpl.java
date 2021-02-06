package com.synectiks.process.server.xformation.service.jpa;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.Collector;

public class CollectorServiceImpl{// implements CollectorService {

	private static final Logger LOG = LoggerFactory.getLogger(CollectorServiceImpl.class);

	
	private EntityManager entityManager = null;

	@Inject
    public CollectorServiceImpl() {
		System.out.println("CollectorServiceImpl constructor ............");
		this.entityManager = GuiceInjectorHolder.getInjector().getInstance(EntityManager.class);
		if(this.entityManager == null) {
			System.out.println("Entitymanager is null......");
		}else {
			System.out.println("Entitymanager instance....... "+this.entityManager);
		}
    }

	
//	@Override
	public List<Collector> listAll() {
		List list = Collections.emptyList();
		try {
			System.out.println("Calling listAll **************************************");
//			String query = "from Collector";
//			list = entityManager.createQuery(query).getResultList();
//			for (Object o : list) {
//				System.out.println("db object : " + o);
//			}
//			list = repository.findAll();
		}catch(Exception e) {
			System.out.println("Exception ***************** ");
			e.printStackTrace();
		}
		
		
		return list;
	}

}