package com.primertrimestre.persistence.dao;

import java.util.List;

import com.primertrimestre.model.Module;

public interface ModuleDao extends GenericDao<Module, Long> {

    List<Module> findByTeacherId(Long teacherId);

}
