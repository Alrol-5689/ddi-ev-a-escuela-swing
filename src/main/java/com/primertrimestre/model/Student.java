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
public class Student extends User {
	
	private static final long serialVersionUID = 1L;
	
	private Set<Enrollment> enrollments = new HashSet<>();
	
}
