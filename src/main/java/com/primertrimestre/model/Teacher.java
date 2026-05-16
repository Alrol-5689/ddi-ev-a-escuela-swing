package com.primertrimestre.model;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends User {
	
	private static final long serialVersionUID = 1L;
	
	private Set<Module> modules = new HashSet<>();

    @Override
    public String toString() {
        if (getFullName() != null && !getFullName().isBlank()) {
            return getFullName();
        }
        return getUsername() != null ? getUsername() : "Profesor sin nombre";
    }
}
