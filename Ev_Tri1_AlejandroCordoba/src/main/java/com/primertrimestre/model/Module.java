package com.primertrimestre.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "modules")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Module implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private int creditsECTS; 
	
	@Column(nullable = false, length = 100)
	private String name;
	
	//@Setter(AccessLevel.NONE) No hace falta porque al escribir el setter lombok lo ignora
	@Column(nullable = false, unique = true, length = 10)
	private String code;
	
	@OneToMany(mappedBy = "module")
	private Set<Enrollment> enrollments = new HashSet<>(); //==>> HashSet para que no se repita un alumno en un módulo tampoco en la lógica del programa 
	
	@ManyToOne(optional = true) // Si fuera ManyToMay haríamos JoinTable creando otra tabla
	@JoinColumn(
			name = "teacher_id",
			nullable = true,
			foreignKey = @ForeignKey(name = "fk_modules_teachers"))
	private Teacher teacher;
	
	public void setShortName(String shortName) {this.code = shortName.toUpperCase();} // Lombok respeta este setter y no lo sobreescribe

    @Override
    public String toString() {
        return (code != null ? code : "SIN-CODIGO") + " - " + (name != null ? name : "Sin nombre");
    }
}
