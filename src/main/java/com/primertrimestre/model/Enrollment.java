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
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Module module;
    private Student student;
    private Double grade;
}
