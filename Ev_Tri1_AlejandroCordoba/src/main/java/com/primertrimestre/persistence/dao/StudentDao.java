package com.primertrimestre.persistence.dao;

import java.util.List;

import com.primertrimestre.model.Module;
import com.primertrimestre.model.Student;

public interface StudentDao extends GenericDao<Student, Long> {
	
	Student findByUsername(String username);

    List<Module> findEnrolledModulesByStudentId(Long id);
	
}
