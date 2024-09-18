
package com.cloudsuites.framework.services.property.features.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.cloudsuites.framework.services.user.entities.Address;
import com.cloudsuites.framework.services.user.entities.Identity;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "building")
public class Building {

	private static final Logger logger = LoggerFactory.getLogger(Building.class);

	@Id
	@Column(name = "building_id", unique = true, nullable = false)
	private String buildingId;

	@Column(name = "name")
	private String name;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "management_company_id")
	private Company company;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private Address address;

	@OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Floor> floors;

	@OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Unit> units;

	@Column(name = "total_floors")
	private Integer totalFloors;

	@Column(name = "year_built")
	private Integer yearBuilt;

	@JoinColumn(name = "created_by")
	@OneToOne(cascade = CascadeType.ALL)
	private Identity createdBy;

	@JoinColumn(name = "last_modified_by")
	@OneToOne(cascade = CascadeType.ALL)
	private Identity lastModifiedBy;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "last_modified_at")
	private LocalDateTime lastModifiedAt;


	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.buildingId = IdGenerator.generateULID("BLD-");
		logger.debug("Generated buildingId: {}", this.buildingId);
	}

	@PreUpdate
	protected void onUpdate() {
		this.lastModifiedAt = LocalDateTime.now();
	}

}
