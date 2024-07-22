package com.cloudsuites.framework.services.property.features.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.Tenant;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Data
@Entity
@Table(name = "unit")
public class Unit {

	private static final Logger logger = LoggerFactory.getLogger(Unit.class);

	@Id
	@Column(name = "unit_id", unique = true, nullable = false)
	private String unitId;

	@OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Tenant> tenants;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "owner_id")
	private Owner owner;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "floor_id")
	private Floor floor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "building_id")
	private Building building;

	@Column(name = "unit_number")
	private String unitNumber;

	@Column(name = "square_footage")
	private Double squareFootage;

	@PrePersist
	protected void onCreate() {
		this.unitId = IdGenerator.generateULID("UN-");
		logger.debug("Generated unitId: {}", this.unitId);
	}
}


