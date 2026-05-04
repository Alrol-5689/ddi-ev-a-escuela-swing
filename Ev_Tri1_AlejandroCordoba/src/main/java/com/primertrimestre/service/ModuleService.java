package com.primertrimestre.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.primertrimestre.model.Module;
import com.primertrimestre.model.Teacher;
import com.primertrimestre.persistence.dao.ModuleDao;
import com.primertrimestre.persistence.dao.TeacherDao;

public class ModuleService {

    private final ModuleDao moduleDao;
    private final TeacherDao teacherDao;

    public ModuleService(ModuleDao moduleDao, TeacherDao teacherDao) {
        this.moduleDao = moduleDao;
        this.teacherDao = teacherDao;
    }

    public List<Module> listAll() {
        return moduleDao.findAll();
    }

    public Module findById(Long moduleId) {
        return moduleDao.findById(moduleId);
    }

    public List<Module> listByTeacher(Long teacherId) {
        return moduleDao.findByTeacherId(teacherId);
    }

    public Module assignTeacher(Long moduleId, Long teacherId) {
        if (moduleId == null || teacherId == null) {
            throw new IllegalArgumentException("Module and teacher identifiers are required");
        }
        Module module = moduleDao.findById(moduleId);
        if (module == null) throw new IllegalArgumentException("Module not found");
        Teacher teacher = teacherDao.findById(teacherId);
        if (teacher == null) throw new IllegalArgumentException("Teacher not found");
        module.setTeacher(teacher);
        return moduleDao.update(module);
    }

    public List<Module> listAvailableForStudent(List<Module> enrolledModules) {
        // Partimos de todos los módulos disponibles en base de datos
        List<Module> all = new ArrayList<>(moduleDao.findAll());
        // Si el alumno no tiene matrículas, no hay nada que filtrar
        if (enrolledModules == null || enrolledModules.isEmpty()) return all;
        // Guardamos los IDs de los módulos en los que ya está inscrito
        Set<Long> enrolledIds = new HashSet<>();
        for (Module module : enrolledModules) {
            if (module != null && module.getId() != null) {
                enrolledIds.add(module.getId());
            }
        }
        // Eliminamos de la lista los módulos cuyos IDs ya estén anotados
        all.removeIf(module -> module != null && enrolledIds.contains(module.getId()));
        return all;
    }

    public void removeTeacherFromModule(Long moduleId, Long teacherId) {
        if (moduleId == null || teacherId == null) {
            throw new IllegalArgumentException("Module and teacher identifiers are required");
        }
        Module module = moduleDao.findById(moduleId);
        if (module == null) throw new IllegalArgumentException("Module not found");
        Teacher teacher = teacherDao.findById(teacherId);
        if (teacher == null) throw new IllegalArgumentException("Teacher not found");
        if (module.getTeacher() == null || !module.getTeacher().getId().equals(teacherId)) {
            throw new IllegalArgumentException("The specified teacher is not assigned to this module");
        }
        module.setTeacher(null);
        moduleDao.update(module);
    }

    public void removeModule(Long moduleId) {
        if (moduleId == null) {
            throw new IllegalArgumentException("Module identifier is required");
        }
        Module module = moduleDao.findById(moduleId);
        if (module == null) {
            throw new IllegalArgumentException("Module not found");
        }
        moduleDao.delete(moduleId);
    }

    public Module createModule(String name, String code, int credits) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Module name is required");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Module code is required");
        }
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be greater than zero");
        }
        Module module = new Module();
        module.setName(name.trim());
        module.setCode(code.trim().toUpperCase());
        module.setCreditsECTS(credits);
        return moduleDao.create(module);
    }

    
}
