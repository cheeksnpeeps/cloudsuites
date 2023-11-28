package com.cloudsuites.framework.services.common.entities.property;

import com.cloudsuites.framework.services.common.entities.Address;
import com.cloudsuites.framework.services.common.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

	@JoinColumn(name = "created_by")
	@OneToOne(cascade = CascadeType.ALL)
	private User createdBy;

	@JoinColumn(name = "last_modified_by")
	@OneToOne(cascade = CascadeType.ALL)
	private User lastModifiedBy;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "last_modified_at")
	private LocalDateTime lastModifiedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.lastModifiedAt = LocalDateTime.now();
	}
}
