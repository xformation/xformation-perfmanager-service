package com.synectiks.process.server.xformation.service;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.Collector;

public class CollectorServiceImpl {// implements CollectorService {

	private static final Logger LOG = LoggerFactory.getLogger(CollectorServiceImpl.class);

	private EntityManager entityManager = null;

	@Inject
	public CollectorServiceImpl() {
		System.out.println("CollectorServiceImpl constructor ............");
		this.entityManager = GuiceInjectorHolder.getInjector().getInstance(EntityManager.class);
		if (this.entityManager == null) {
			System.out.println("Entitymanager is null......");
		} else {
			System.out.println("Entitymanager instance....... " + this.entityManager);
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
		} catch (Exception e) {
			System.out.println("Exception ***************** ");
			e.printStackTrace();
		}

		return list;
	}

	public List<Collector> save(Collector collector) {
		try {
			System.out.println("Enity manager :: "+entityManager);
			EntityTransaction entityTransaction = entityManager.getTransaction();
			System.out.println("Enity transaction :: "+entityTransaction);
			entityTransaction.begin();
			System.out.println("collector is going to persist in database");
			entityManager.persist(collector);
			System.out.println("collector is going to persisted in database");
			entityTransaction.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		List list = Collections.emptyList();
		try {
			System.out.println("Calling listAll **************************************");
			String query = "from Collector";
			list = entityManager.createQuery(query).getResultList();
			for (Object o : list) {
				System.out.println("db object : " + o);
			}
//			list = repository.findAll();
		} catch (Exception e) {
			System.out.println("Exception ***************** ");
			e.printStackTrace();
		}

		return list;

	}

}