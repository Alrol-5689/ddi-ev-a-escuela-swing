package com.primertrimestre.persistence.jpa;

import java.util.Collections;
import java.util.List;

import com.primertrimestre.model.Module;
import com.primertrimestre.model.Student;
import com.primertrimestre.persistence.dao.StudentDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class StudentDaoJpa extends GenericDaoJpa<Student, Long> implements StudentDao {
	
	public StudentDaoJpa() { super(Student.class); }
	
	@Override
	public Student findByUsername(String username) {
	    if (username == null) return null;
	    EntityManager em = em(); //--> No importamos JpaUtil porque lo hace GenericDaoJpa
	    try {
	        return em.createQuery(
	            "SELECT s FROM Student s WHERE s.username = :username", Student.class)
		            .setParameter("username", username)
		            .getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

	@Override
	public List<Module> findEnrolledModulesByStudentId(Long id) {
		if (id == null) return Collections.emptyList();
		try (EntityManager em = em()) {
			return em.createQuery(
					"SELECT e.module FROM Enrollment e WHERE e.student.id = :id", Module.class)
					.setParameter("id", id)
					.getResultList();
		}
	}
}
