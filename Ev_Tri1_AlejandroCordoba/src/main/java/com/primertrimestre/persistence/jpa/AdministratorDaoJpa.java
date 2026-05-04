package com.primertrimestre.persistence.jpa;

import com.primertrimestre.model.Administrator;
import com.primertrimestre.persistence.dao.AdministratorDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class AdministratorDaoJpa extends GenericDaoJpa<Administrator, Long> implements AdministratorDao {

	public AdministratorDaoJpa() {super(Administrator.class);}

	@Override
	public Administrator findByUsername(String username) {
	    if (username == null) return null;
	    EntityManager em = em();
	    try {
	        return em.createQuery(
	            "SELECT a FROM Administrator a WHERE a.username = :username", Administrator.class)
		            .setParameter("username", username)
		            .getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}
}