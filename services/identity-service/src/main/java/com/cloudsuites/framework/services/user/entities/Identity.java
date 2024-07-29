package com.cloudsuites.framework.services.user.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "identity")
public class Identity {

	private static final Logger logger = LoggerFactory.getLogger(Identity.class);

	@Id
	@Column(name = "user_id", unique = true, nullable = false)
	private String userId;

	@Column(name = "username", unique = true, nullable = false)
	private String username;

	@Column
	private Gender gender;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "email")
	private String email;

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
		this.userId = IdGenerator.generateULID("ID-");
		logger.debug("Generated userId: {}", this.userId);
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.lastModifiedAt = LocalDateTime.now();
	}

	public void updateTenantIdentity(Identity identity) {
		if (StringUtils.hasText(identity.getFirstName())) {
			this.setFirstName(identity.getFirstName());
		}
		if (StringUtils.hasText(identity.getLastName())) {
			this.setLastName(identity.getLastName());
		}
		if (StringUtils.hasText(identity.getEmail())) {
			this.setEmail(identity.getEmail());
		}
		if (StringUtils.hasText(identity.getPhoneNumber())) {
			this.setPhoneNumber(identity.getPhoneNumber());
		}
		if (identity.getGender() != null) {
			this.setGender(identity.getGender());
		}
		if (StringUtils.hasText(identity.getUsername())) {
			this.setUsername(identity.getUsername());
		}
	}
}

