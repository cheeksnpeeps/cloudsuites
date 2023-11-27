package com.cloudsuites.framework.services.common.entities.property;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "unit")
@NoArgsConstructor
@AllArgsConstructor
public class Unit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unit_id")
	private Long unitId;

	@ManyToOne
	@JoinColumn(name = "floor_id")
	private Floor floor;

	// Other unit attributes
	@Column(name = "unit_number")
	private String unitNumber;

	@Column(name = "square_footage")
	private Double squareFootage;

	// Constructors, getters, and setters

	// Additional methods if needed
}


