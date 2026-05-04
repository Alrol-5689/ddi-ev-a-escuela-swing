package com.primertrimestre.service;

import com.primertrimestre.model.Teacher;
import com.primertrimestre.persistence.dao.TeacherDao;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class TeacherService {
	
	//===>> FIELDS <<===//
	
	private final TeacherDao teacherDao;
	private static final int BCRYPT_COST = 12;
	
	//===>> CONSTRUCTORS <<===//
	
	public TeacherService(TeacherDao teacherDao) {this.teacherDao = teacherDao;}

	//===>> METHODS <<===//
	
	public Teacher authenticate(String username, String password) {
        if (username == null || password == null) return null;
        Teacher st = teacherDao.findByUsername(username);
        if (st == null || st.getPassword() == null) return null;
        return BCrypt.checkpw(password, st.getPassword()) ? st : null;
	} 

    public void registerTeacher(Teacher teacher) {
        if (teacher == null || teacher.getUsername() == null || teacher.getPassword() == null) {
            throw new IllegalArgumentException("Username/password required");
        }
        if (teacherDao.findByUsername(teacher.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        String hashed = BCrypt.hashpw(teacher.getPassword(), BCrypt.gensalt(BCRYPT_COST));
        teacher.setPassword(hashed);
        teacherDao.create(teacher);
    }

    public Teacher findById(Long id) {
        return teacherDao.findById(id);
    }

    public List<Teacher> listTeachers() {
        return teacherDao.findAll();
    }
}
