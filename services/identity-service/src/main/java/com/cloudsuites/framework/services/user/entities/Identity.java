package com.cloudsuites.framework.services.user.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "identity")
public class Identity {

	private static final Logger logger = LoggerFactory.getLogger(Identity.class);

	@Id
	@Column(name = "user_id", unique = true, nullable = false)
	private String userId;

	@Column
	private Gender gender;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "phone_number")
	private String phoneNumber;

	@NotNull(message = "Email is mandatory")
	@Email(message = "Email should be valid")
	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@JoinColumn(name = "created_by")
	@OneToOne(cascade = CascadeType.ALL)
	private Identity createdBy;

	@JoinColumn(name = "last_modified_by")
	@OneToOne(cascade = CascadeType.ALL)
	private Identity lastModifiedBy;

	@Column(name = "created_at", nullable = false, updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime createdAt;

	@Column(name = "last_modified_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime lastModifiedAt;

	// ============================================================================
	// AUTHENTICATION FIELDS (Added in V5 migration)
	// ============================================================================
	
	// Password authentication (fallback option)
	@JsonIgnore
	@Column(name = "password_hash")
	private String passwordHash;
	
	@JsonIgnore
	@Column(name = "password_salt")
	private String passwordSalt;
	
	@Column(name = "password_changed_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime passwordChangedAt;
	
	@Column(name = "password_expires_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime passwordExpiresAt;
	
	@JsonIgnore
	@Column(name = "password_reset_token")
	private String passwordResetToken;
	
	@Column(name = "password_reset_expires_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime passwordResetExpiresAt;
	
	@Column(name = "password_reset_attempts")
	private Integer passwordResetAttempts = 0;

	// Multi-factor authentication
	@Column(name = "mfa_enabled")
	private Boolean mfaEnabled = false;
	
	@JsonIgnore
	@Column(name = "mfa_secret", length = 500)
	private String mfaSecret;
	
	@JsonIgnore
	@Column(name = "mfa_backup_codes", columnDefinition = "TEXT")
	private String mfaBackupCodes; // JSON array as string
	
	@Column(name = "mfa_recovery_codes_used")
	private Integer mfaRecoveryCodesUsed = 0;
	
	@Column(name = "mfa_enrolled_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime mfaEnrolledAt;

	// Account security
	@Column(name = "failed_login_attempts")
	private Integer failedLoginAttempts = 0;
	
	@Column(name = "account_locked_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime accountLockedAt;
	
	@Column(name = "account_locked_until")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime accountLockedUntil;
	
	@Column(name = "last_successful_login_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime lastSuccessfulLoginAt;
	
	@Column(name = "last_failed_login_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime lastFailedLoginAt;
	
	@Column(name = "last_password_change_ip", columnDefinition = "inet")
	private String lastPasswordChangeIp;

	// Email/Phone verification for OTP channels
	@Column(name = "email_verified")
	private Boolean emailVerified = false;
	
	@Column(name = "email_verified_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime emailVerifiedAt;
	
	@JsonIgnore
	@Column(name = "email_verification_token")
	private String emailVerificationToken;
	
	@Column(name = "email_verification_expires_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime emailVerificationExpiresAt;
	
	@Column(name = "phone_verified")
	private Boolean phoneVerified = false;
	
	@Column(name = "phone_verified_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime phoneVerifiedAt;

	// Security preferences
	@Column(name = "security_questions_enabled")
	private Boolean securityQuestionsEnabled = false;
	
	@Column(name = "biometric_enabled")
	private Boolean biometricEnabled = false;
	
	@Column(name = "notification_preferences", columnDefinition = "jsonb")
	private String notificationPreferences; // JSON object as string
	
	@Column(name = "login_notification_enabled")
	private Boolean loginNotificationEnabled = true;
	
	@Column(name = "security_notification_enabled")
	private Boolean securityNotificationEnabled = true;

	// Compliance and terms acceptance
	@Column(name = "terms_accepted_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime termsAcceptedAt;
	
	@Column(name = "terms_version", length = 20)
	private String termsVersion;
	
	@Column(name = "privacy_policy_accepted_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime privacyPolicyAcceptedAt;
	
	@Column(name = "privacy_policy_version", length = 20)
	private String privacyPolicyVersion;
	
	@Column(name = "marketing_consent")
	private Boolean marketingConsent = false;
	
	@Column(name = "marketing_consent_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
	private LocalDateTime marketingConsentAt;

	// Enhanced security metadata
	@Column(name = "security_score")
	private Integer securityScore = 50; // 0-100 range enforced by database constraint
	
	@Enumerated(EnumType.STRING)
	@Column(name = "risk_profile", length = 20)
	private RiskProfile riskProfile = RiskProfile.NORMAL;
	
	@Column(name = "timezone", length = 50)
	private String timezone = "UTC";
	
	@Column(name = "locale", length = 10)
	private String locale = "en_US";

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

	public void updateIdentity(Identity identity) {
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
	}
}

