package com.primertrimestre.model;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    protected Long id;

    protected String username;

    protected String password;

    protected String fullName;
}
