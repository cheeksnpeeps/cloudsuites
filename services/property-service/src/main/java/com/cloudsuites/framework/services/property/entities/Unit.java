package com.cloudsuites.framework.services.property.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "unit")
public class Unit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unit_id")
	private Long unitId;

	@OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Tenant> tenants;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Owner owner;

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


