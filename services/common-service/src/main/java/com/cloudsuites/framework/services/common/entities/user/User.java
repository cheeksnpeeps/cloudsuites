package com.cloudsuites.framework.services.common.entities.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false, unique = true)
	private Gender gender;

	@ManyToOne
	@JoinColumn(name = "contact_info_id")
	private ContactInfo contactInfo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserType userType;

	@Column(name = "created_by")
	@OneToOne(cascade = CascadeType.ALL)
	private User createdBy;

	@Column(name = "last_modified_by")
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

