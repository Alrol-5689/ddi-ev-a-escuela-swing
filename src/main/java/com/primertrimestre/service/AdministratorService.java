package com.primertrimestre.service;

import org.mindrot.jbcrypt.BCrypt;

import com.primertrimestre.model.Administrator;
import com.primertrimestre.persistence.dao.AdministratorDao;

public class AdministratorService {
	
	//===>> FIELDS <<===//
	
	private final AdministratorDao administratorDao;
	private static final int BCRYPT_COST = 12;
	
	//===>> CONSTRUCTORS <<===//

	public AdministratorService(AdministratorDao administratorDao) {
		this.administratorDao = administratorDao;
	}

	public Administrator authenticate(String username, String password) {
        if (username == null || password == null) return null;
        Administrator admin = administratorDao.findByUsername(username);
        if (admin == null || admin.getPassword() == null) return null;
        return BCrypt.checkpw(password, admin.getPassword()) ? admin : null;
	}

    public void registerAdministrator(Administrator administrator) {
        if (administrator == null || administrator.getUsername() == null || administrator.getPassword() == null) {
            throw new IllegalArgumentException("Username/password required");
        }
        if (administratorDao.findByUsername(administrator.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        String hashed = BCrypt.hashpw(administrator.getPassword(), BCrypt.gensalt(BCRYPT_COST));
        administrator.setPassword(hashed);
        administratorDao.create(administrator);
    }

}
