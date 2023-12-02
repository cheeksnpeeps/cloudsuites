package com.cloudsuites.framework.services.entities.property;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "unit")
public class Unit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unit_id")
	private Long unitId;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "floor_id")
	private Floor floor;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "building_id")
	private Building building;

	// Other unit attributes
	@Column(name = "unit_number")
	private String unitNumber;

	@Column(name = "square_footage")
	private Double squareFootage;

	// Constructors, getters, and setters

	// Additional methods if needed
}


