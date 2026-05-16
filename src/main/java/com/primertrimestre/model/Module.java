package com.primertrimestre.model;

import java.io.Serializable;
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
public class Module implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private int creditsECTS;
    private String name;
    private String code;
    private Set<Enrollment> enrollments = new HashSet<>();
    private Teacher teacher;

    public void setShortName(String shortName) {
        this.code = shortName.toUpperCase();
    }

    @Override
    public String toString() {
        return (code != null ? code : "SIN-CODIGO") + " - " + (name != null ? name : "Sin nombre");
    }
}
