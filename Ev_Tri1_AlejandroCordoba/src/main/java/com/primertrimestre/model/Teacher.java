package com.primertrimestre.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teachers")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends User {
	
	private static final long serialVersionUID = 1L;
	
	@OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Module> modules = new HashSet<>();

    @Override
    public String toString() {
        if (getFullName() != null && !getFullName().isBlank()) {
            return getFullName();
        }
        return getUsername() != null ? getUsername() : "Profesor sin nombre";
    }
}
