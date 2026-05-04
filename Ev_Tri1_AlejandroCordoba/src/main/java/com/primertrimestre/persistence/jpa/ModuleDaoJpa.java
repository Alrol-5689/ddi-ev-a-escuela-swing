package com.primertrimestre.persistence.jpa;

import java.util.Collections;
import java.util.List;

import com.primertrimestre.model.Module;
import com.primertrimestre.persistence.dao.ModuleDao;

import jakarta.persistence.EntityManager;

public class ModuleDaoJpa extends GenericDaoJpa<Module, Long> implements ModuleDao {

    public ModuleDaoJpa() {super(Module.class);}

    @Override
    public List<Module> findByTeacherId(Long teacherId) {
        if (teacherId == null) return Collections.emptyList();
        try (EntityManager em = em()) { //--> método em() de GenericDaoJpa ya que importa JpaUtil
            return em.createQuery(
                    "SELECT m FROM Module m WHERE m.teacher.id = :teacherId",
                    Module.class)
                    .setParameter("teacherId", teacherId)
                    .getResultList();
        }
    }
}
