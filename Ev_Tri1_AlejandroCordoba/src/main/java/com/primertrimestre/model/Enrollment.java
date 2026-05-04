package com.primertrimestre.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enrollments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"module_id", "student_id"}))

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "module_id", foreignKey = @ForeignKey(name = "fk_enrollments_modules"))
	private Module module;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "fk_enrollments_students"))
	private Student student;

	@Column(name = "grade")
	private Double grade;

}
