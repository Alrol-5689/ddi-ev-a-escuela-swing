package com.primertrimestre.ui.controllers;

import com.primertrimestre.auth.SessionContext;
import com.primertrimestre.persistence.dao.AdministratorDao;
import com.primertrimestre.persistence.dao.EnrollmentDao;
import com.primertrimestre.persistence.dao.ModuleDao;
import com.primertrimestre.persistence.dao.StudentDao;
import com.primertrimestre.persistence.dao.TeacherDao;
import com.primertrimestre.persistence.jpa.AdministratorDaoJpa;
import com.primertrimestre.persistence.jpa.EnrollmentDaoJpa;
import com.primertrimestre.persistence.jpa.ModuleDaoJpa;
import com.primertrimestre.persistence.jpa.StudentDaoJpa;
import com.primertrimestre.persistence.jpa.TeacherDaoJpa;
import com.primertrimestre.service.AdministratorService;
import com.primertrimestre.service.EnrollmentService;
import com.primertrimestre.service.ModuleService;
import com.primertrimestre.service.StudentService;
import com.primertrimestre.service.TeacherService;

/**
 * Centraliza las dependencias compartidas (DAOs, servicios y sesión) para
 * reutilizarlas entre controladores sin recrearlas en cada cambio de pantalla.
 * Es un singleton con constructor privado: nadie puede instanciarlo desde fuera,
 * se usa la única instancia creada al cargar la clase vía getInstance().
 */
public final class AppContext {

    // Singleton: esta instancia se crea una sola vez al cargar la clase y se reutiliza en toda la app
    private static final AppContext INSTANCE = new AppContext();

    private final SessionContext session = new SessionContext();

    private final StudentDao studentDao = new StudentDaoJpa();
    private final TeacherDao teacherDao = new TeacherDaoJpa();
    private final ModuleDao moduleDao = new ModuleDaoJpa();
    private final EnrollmentDao enrollmentDao = new EnrollmentDaoJpa();
    private final AdministratorDao administratorDao = new AdministratorDaoJpa();

    private final StudentService studentService = new StudentService(studentDao);
    private final TeacherService teacherService = new TeacherService(teacherDao);
    private final ModuleService moduleService = new ModuleService(moduleDao, teacherDao);
    private final EnrollmentService enrollmentService = new EnrollmentService(enrollmentDao, studentDao, moduleDao);
    private final AdministratorService administratorService = new AdministratorService(administratorDao);

    // Constructor privado: bloquea new AppContext() fuera de esta clase y garantiza un único punto de acceso.
    private AppContext() {
    }

    public static AppContext getInstance() {
        return INSTANCE;
    }

    public SessionContext getSession() {
        return session;
    }

    public StudentService getStudentService() {
        return studentService;
    }

    public TeacherService getTeacherService() {
        return teacherService;
    }

    public AdministratorService getAdministratorService() {
        return administratorService;
    }

    public ModuleService getModuleService() {
        return moduleService;
    }

    public EnrollmentService getEnrollmentService() {
        return enrollmentService;
    }
}
