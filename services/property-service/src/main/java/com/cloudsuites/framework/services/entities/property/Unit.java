package com.cloudsuites.framework.services.entities.property;

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

	@ManyToOne
	@JoinColumn(name = "floor_id")
	private Floor floor;

	@ManyToOne
	@JoinColumn(name = "building_id")
	private Building building;

	@Column(name = "unit_number")
	private String unitNumber;

	@Column(name = "square_footage")
	private Double squareFootage;

}


