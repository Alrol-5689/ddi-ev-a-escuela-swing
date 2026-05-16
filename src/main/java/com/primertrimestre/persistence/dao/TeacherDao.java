package com.primertrimestre.persistence.dao;

import com.primertrimestre.model.Teacher;

public interface TeacherDao extends GenericDao<Teacher, Long> {
	
	Teacher findByUsername(String username);
	
}
