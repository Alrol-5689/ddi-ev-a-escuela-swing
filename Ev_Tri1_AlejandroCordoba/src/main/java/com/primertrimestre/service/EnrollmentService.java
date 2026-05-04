package com.primertrimestre.service;

import java.util.List;

import com.primertrimestre.model.Enrollment;
import com.primertrimestre.model.Module;
import com.primertrimestre.model.Student;
import com.primertrimestre.persistence.dao.EnrollmentDao;
import com.primertrimestre.persistence.dao.ModuleDao;
import com.primertrimestre.persistence.dao.StudentDao;

public class EnrollmentService {

    private final EnrollmentDao enrollmentDao;
    private final StudentDao studentDao;
    private final ModuleDao moduleDao;

    public EnrollmentService(EnrollmentDao enrollmentDao, StudentDao studentDao, ModuleDao moduleDao) {
        this.enrollmentDao = enrollmentDao;
        this.studentDao = studentDao;
        this.moduleDao = moduleDao;
    }

    public List<Enrollment> listByStudent(Long studentId) {
        return enrollmentDao.findByStudentId(studentId);
    }

    public List<Enrollment> listByModule(Long moduleId) {
        return enrollmentDao.findByModuleId(moduleId);
    }

    public Enrollment enrollStudent(Long studentId, Long moduleId) {
        if (studentId == null || moduleId == null) {
            throw new IllegalArgumentException("Student and module identifiers are required");
        }
        Enrollment existing = enrollmentDao.findByStudentAndModule(studentId, moduleId);
        if (existing != null) return existing;

        Student student = studentDao.findById(studentId);
        if (student == null) throw new IllegalArgumentException("Student not found");

        Module module = moduleDao.findById(moduleId);
        if (module == null) throw new IllegalArgumentException("Module not found");

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setModule(module);
        enrollment.setGrade(null);
        // JPA gestiona la relación sin tocar colecciones lazy fuera de sesión.
        // if (student.getEnrollments() != null) {
        //     student.getEnrollments().add(enrollment);
        // }
        // if (module.getEnrollments() != null) {
        //     module.getEnrollments().add(enrollment);
        // }
        return enrollmentDao.create(enrollment);
    }

    public void unenrollStudent(Long studentId, Long moduleId) {
        Enrollment enrollment = enrollmentDao.findByStudentAndModule(studentId, moduleId);
        if (enrollment != null) {
            // Evitamos tocar colecciones lazy fuera de sesión.
            // Module module = enrollment.getModule();
            // Student student = enrollment.getStudent();
            // if (student != null && student.getEnrollments() != null) {
            //     student.getEnrollments().remove(enrollment);
            // }
            // if (module != null && module.getEnrollments() != null) {
            //     module.getEnrollments().remove(enrollment);
            // }
            enrollmentDao.delete(enrollment.getId());
        }
    }

    public Enrollment updateGrade(Long studentId, Long moduleId, Double grade) {
        Enrollment enrollment = enrollmentDao.findByStudentAndModule(studentId, moduleId);
        if (enrollment == null) throw new IllegalArgumentException("Enrollment not found");
        enrollment.setGrade(grade);
        return enrollmentDao.update(enrollment);
    }

    public List<Module> availableModules(Long studentId) {
        return (List<Module>) enrollmentDao.findAvailableModulesByStudentId(studentId);
    }
}
