package com.cloudsuites.framework.webapp.rest.user.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String username;

	@Column
	private Gender gender;

	@ManyToOne
	@JoinColumn(name = "contact_info_id")
	private ContactInfoDTO contactInfo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserType userType;

	@JoinColumn(name = "created_by")
	@OneToOne(cascade = CascadeType.ALL)
	private UserDTO createdBy;

	@JoinColumn(name = "last_modified_by")
	@OneToOne(cascade = CascadeType.ALL)
	private UserDTO lastModifiedBy;

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

