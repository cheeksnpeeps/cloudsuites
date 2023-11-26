package com.cloudsuites.framework.services.common.entities.property;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "building")
public class Building {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "building_id")
	private Long buildingId;

	@Column(name = "name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "company_id")
	private PropertyManagementCompany propertyManagementCompany;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "address_id")
	private Address address;

	@OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
	private List<Floor> floors;

	// Other building attributes
	@Column(name = "total_floors")
	private Integer totalFloors;

	@Column(name = "year_built")
	private Integer yearBuilt;

	// Constructors, getters, and setters

	// Additional methods if needed
}
