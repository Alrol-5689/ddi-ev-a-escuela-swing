package com.primertrimestre.persistence.dao;

import java.util.List;

public interface GenericDao<T, ID> {
	
    T findById(ID id);

    List<T> findAll();

    T create(T entity);

    T update(T entity);

    T createOrUpdate(T entity);

    void delete(ID id);

    long count();

}
