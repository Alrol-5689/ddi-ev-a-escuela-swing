package com.primertrimestre.persistence.dao;

import com.primertrimestre.model.Administrator;

public interface AdministratorDao extends GenericDao<Administrator, Long> {

	Administrator findByUsername(String username);

}
