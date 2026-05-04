package com.primertrimestre.persistence.jpa;

import java.util.Collections;
import java.util.List;

import com.primertrimestre.model.Enrollment;
import com.primertrimestre.model.Module;
import com.primertrimestre.persistence.dao.EnrollmentDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class EnrollmentDaoJpa extends GenericDaoJpa<Enrollment, Long> implements EnrollmentDao {

    public EnrollmentDaoJpa() {
        super(Enrollment.class);
    }

    @Override
    public List<Enrollment> findByStudentId(Long studentId) {
        if (studentId == null) return Collections.emptyList();
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT e FROM Enrollment e "
                    + "JOIN FETCH e.module "
                    + "WHERE e.student.id = :studentId",
                    Enrollment.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        }
    }

    @Override
    public List<Enrollment> findByModuleId(Long moduleId) {
        if (moduleId == null) return Collections.emptyList();
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT e FROM Enrollment e "
                    + "JOIN FETCH e.student "
                    + "WHERE e.module.id = :moduleId",
                    Enrollment.class)
                    .setParameter("moduleId", moduleId)
                    .getResultList();
        }
    }

    @Override
    public Enrollment findByStudentAndModule(Long studentId, Long moduleId) {
        if (studentId == null || moduleId == null) return null;
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT e FROM Enrollment e "
                    + "WHERE e.student.id = :studentId AND e.module.id = :moduleId",
                    Enrollment.class)
                    .setParameter("studentId", studentId)
                    .setParameter("moduleId", moduleId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<Module> findAvailableModulesByStudentId(Long studentId) {
        if (studentId == null) return Collections.emptyList();
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT m FROM Module m "
                    + "WHERE NOT EXISTS ("
                    + "    SELECT 1 FROM Enrollment e "
                    + "    WHERE e.module = m AND e.student.id = :studentId"
                    + ")",
                    Module.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        }
    }
}
