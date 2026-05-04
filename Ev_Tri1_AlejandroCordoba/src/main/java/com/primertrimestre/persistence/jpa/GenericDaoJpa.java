package com.primertrimestre.persistence.jpa;

import java.util.List;

import com.primertrimestre.persistence.dao.GenericDao;
import com.primertrimestre.persistence.util.JpaUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class GenericDaoJpa<T, ID> implements GenericDao<T, ID> {

    //===>> FIELDS <<===//
	
    private final Class<T> entityClass;
    
    //===>> CONSTRUCTOR <<===//
    
    protected GenericDaoJpa(Class<T> entityClass) {this.entityClass = entityClass;}
    
    //===>> METHODS <<===//
    
    protected EntityManager em() {return JpaUtil.getEntityManager();}

    @Override
    public T findById(ID id) {
        try (EntityManager em = em()) {
            return em.find(entityClass, id);
        }
    }

    @Override
    public List<T> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery("from " + entityClass.getSimpleName(), entityClass)
                    .getResultList();
        }
    }

    @Override
    public T create(T entity) {
        if (entity == null) return null;
        try (EntityManager em = em()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(entity);
                tx.commit();
                return entity;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public T update(T entity) {
        if (entity == null) return null;
        try (EntityManager em = em()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                T merged = em.merge(entity);
                tx.commit();
                return merged;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public T createOrUpdate(T entity) {
        if (entity == null) return null;
        try (EntityManager em = em()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Object identifier = em.getEntityManagerFactory()
                        .getPersistenceUnitUtil()
                        .getIdentifier(entity);
                T managed;
                if (identifier == null) {
                    em.persist(entity);
                    managed = entity;
                } else {
                    managed = em.merge(entity);
                }
                tx.commit();
                return managed;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(ID id) {
        if (id == null) return;
        try (EntityManager em = em()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                T ref = em.find(entityClass, id);
                if (ref != null) em.remove(ref);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public long count() {
        try (EntityManager em = em()) {
            return em.createQuery("select count(e) from " + entityClass.getSimpleName() + " e", Long.class)
                    .getSingleResult();
        }
    }

}
