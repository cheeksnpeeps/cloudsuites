-- =====================================================
-- CloudSuites Authentication: User Table Enhancements
-- Version: V5
-- Purpose: Add authentication-related columns to existing user tables
-- Author: CloudSuites Development Team
-- Date: September 10, 2025
-- =====================================================

-- Add authentication-related columns to the identity table
-- Note: These columns support password fallback (REQ-004) and MFA (SEC-005)
ALTER TABLE identity ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);
ALTER TABLE identity ADD COLUMN IF NOT EXISTS password_salt VARCHAR(255);
ALTER TABLE identity ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMP;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS password_expires_at TIMESTAMP;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS password_reset_token VARCHAR(255);
ALTER TABLE identity ADD COLUMN IF NOT EXISTS password_reset_expires_at TIMESTAMP;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS password_reset_attempts INTEGER DEFAULT 0;

-- Multi-factor authentication columns
ALTER TABLE identity ADD COLUMN IF NOT EXISTS mfa_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS mfa_secret VARCHAR(500); -- For TOTP authenticator apps
ALTER TABLE identity ADD COLUMN IF NOT EXISTS mfa_backup_codes TEXT; -- JSON array of backup codes
ALTER TABLE identity ADD COLUMN IF NOT EXISTS mfa_recovery_codes_used INTEGER DEFAULT 0;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS mfa_enrolled_at TIMESTAMP;

-- Account security columns
ALTER TABLE identity ADD COLUMN IF NOT EXISTS failed_login_attempts INTEGER DEFAULT 0;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS account_locked_at TIMESTAMP;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS account_locked_until TIMESTAMP;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS last_successful_login_at TIMESTAMP;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS last_failed_login_at TIMESTAMP;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS last_password_change_ip INET;

-- Email verification for OTP channel
ALTER TABLE identity ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS email_verified_at TIMESTAMP;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS email_verification_token VARCHAR(255);
ALTER TABLE identity ADD COLUMN IF NOT EXISTS email_verification_expires_at TIMESTAMP;

-- Phone verification for OTP channel  
ALTER TABLE identity ADD COLUMN IF NOT EXISTS phone_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS phone_verified_at TIMESTAMP;

-- Security preferences
ALTER TABLE identity ADD COLUMN IF NOT EXISTS security_questions_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS biometric_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS notification_preferences JSONB;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS login_notification_enabled BOOLEAN DEFAULT TRUE;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS security_notification_enabled BOOLEAN DEFAULT TRUE;

-- Terms and privacy acceptance (compliance)
ALTER TABLE identity ADD COLUMN IF NOT EXISTS terms_accepted_at TIMESTAMP;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS terms_version VARCHAR(20);
ALTER TABLE identity ADD COLUMN IF NOT EXISTS privacy_policy_accepted_at TIMESTAMP;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS privacy_policy_version VARCHAR(20);
ALTER TABLE identity ADD COLUMN IF NOT EXISTS marketing_consent BOOLEAN DEFAULT FALSE;
ALTER TABLE identity ADD COLUMN IF NOT EXISTS marketing_consent_at TIMESTAMP;

-- Metadata for enhanced security
ALTER TABLE identity ADD COLUMN IF NOT EXISTS security_score INTEGER DEFAULT 50 CHECK (security_score >= 0 AND security_score <= 100);
ALTER TABLE identity ADD COLUMN IF NOT EXISTS risk_profile VARCHAR(20) DEFAULT 'NORMAL' CHECK (risk_profile IN ('LOW', 'NORMAL', 'ELEVATED', 'HIGH'));
ALTER TABLE identity ADD COLUMN IF NOT EXISTS timezone VARCHAR(50) DEFAULT 'UTC';
ALTER TABLE identity ADD COLUMN IF NOT EXISTS locale VARCHAR(10) DEFAULT 'en_US';

-- Add indexes for performance on new columns
CREATE INDEX IF NOT EXISTS idx_identity_password_reset_token ON identity(password_reset_token) 
    WHERE password_reset_token IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_identity_email_verification_token ON identity(email_verification_token) 
    WHERE email_verification_token IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_identity_account_locked ON identity(account_locked_until) 
    WHERE account_locked_until IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_identity_failed_attempts ON identity(failed_login_attempts) 
    WHERE failed_login_attempts > 0;
CREATE INDEX IF NOT EXISTS idx_identity_mfa_enabled ON identity(mfa_enabled) 
    WHERE mfa_enabled = TRUE;
CREATE INDEX IF NOT EXISTS idx_identity_email_verified ON identity(email_verified);
CREATE INDEX IF NOT EXISTS idx_identity_phone_verified ON identity(phone_verified);
CREATE INDEX IF NOT EXISTS idx_identity_last_login ON identity(last_successful_login_at);

-- Create index for password reset tokens (without time-based predicate due to PostgreSQL IMMUTABLE requirement)
CREATE INDEX IF NOT EXISTS idx_identity_password_reset_token ON identity(password_reset_token) 
    WHERE password_reset_token IS NOT NULL;

-- Add constraints for data integrity
-- Ensure password reset token has expiration
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_password_reset_consistency') THEN
        ALTER TABLE identity ADD CONSTRAINT chk_password_reset_consistency 
            CHECK ((password_reset_token IS NULL AND password_reset_expires_at IS NULL) 
                   OR (password_reset_token IS NOT NULL AND password_reset_expires_at IS NOT NULL));
    END IF;
END
$$;

-- Ensure email verification token has expiration
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_email_verification_consistency') THEN
        ALTER TABLE identity ADD CONSTRAINT chk_email_verification_consistency 
            CHECK ((email_verification_token IS NULL AND email_verification_expires_at IS NULL) 
                   OR (email_verification_token IS NOT NULL AND email_verification_expires_at IS NOT NULL));
    END IF;
END
$$;

-- Ensure MFA secret exists when MFA is enabled
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_mfa_consistency') THEN
        ALTER TABLE identity ADD CONSTRAINT chk_mfa_consistency 
            CHECK ((mfa_enabled = FALSE) OR (mfa_enabled = TRUE AND mfa_secret IS NOT NULL));
    END IF;
END
$$;

-- Ensure account lock has expiration when locked
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_account_lock_consistency') THEN
        ALTER TABLE identity ADD CONSTRAINT chk_account_lock_consistency 
            CHECK ((account_locked_at IS NULL AND account_locked_until IS NULL) 
                   OR (account_locked_at IS NOT NULL AND account_locked_until IS NOT NULL));
    END IF;
END
$$;

-- Add trigger to automatically hash passwords (placeholder - actual implementation in service layer)
-- Note: Password hashing should be done in the application layer using BCrypt
CREATE OR REPLACE FUNCTION update_identity_security_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    -- Update password_changed_at when password_hash changes
    IF OLD.password_hash IS DISTINCT FROM NEW.password_hash AND NEW.password_hash IS NOT NULL THEN
        NEW.password_changed_at = CURRENT_TIMESTAMP;
    END IF;
    
    -- Update email_verified_at when email_verified changes to true
    IF OLD.email_verified = FALSE AND NEW.email_verified = TRUE THEN
        NEW.email_verified_at = CURRENT_TIMESTAMP;
    END IF;
    
    -- Update phone_verified_at when phone_verified changes to true
    IF OLD.phone_verified = FALSE AND NEW.phone_verified = TRUE THEN
        NEW.phone_verified_at = CURRENT_TIMESTAMP;
    END IF;
    
    -- Update mfa_enrolled_at when MFA is first enabled
    IF OLD.mfa_enabled = FALSE AND NEW.mfa_enabled = TRUE THEN
        NEW.mfa_enrolled_at = CURRENT_TIMESTAMP;
    END IF;
    
    -- Update last_modified_at (inherited from existing audit fields)
    NEW.last_modified_at = CURRENT_TIMESTAMP;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_identity_security_updates') THEN
        CREATE TRIGGER trigger_identity_security_updates
            BEFORE UPDATE ON identity
            FOR EACH ROW
            EXECUTE FUNCTION update_identity_security_timestamp();
    END IF;
END
$$;

-- Function to check if account is locked
CREATE OR REPLACE FUNCTION is_account_locked(user_identity_id VARCHAR(255))
RETURNS BOOLEAN AS $$
DECLARE
    locked_until TIMESTAMP;
BEGIN
    SELECT account_locked_until INTO locked_until
    FROM identity 
    WHERE user_id = user_identity_id;
    
    -- Return true if locked and lock hasn't expired
    RETURN (locked_until IS NOT NULL AND locked_until > CURRENT_TIMESTAMP);
END;
$$ LANGUAGE plpgsql;

-- Function to lock account after failed attempts
CREATE OR REPLACE FUNCTION check_and_lock_account(user_identity_id VARCHAR(255))
RETURNS BOOLEAN AS $$
DECLARE
    current_attempts INTEGER;
    max_attempts INTEGER := 5; -- Configurable via application properties
    lock_duration INTERVAL := '30 minutes'; -- Configurable via application properties
BEGIN
    SELECT failed_login_attempts INTO current_attempts
    FROM identity 
    WHERE user_id = user_identity_id;
    
    IF current_attempts >= max_attempts THEN
        UPDATE identity 
        SET account_locked_at = CURRENT_TIMESTAMP,
            account_locked_until = CURRENT_TIMESTAMP + lock_duration
        WHERE user_id = user_identity_id;
        RETURN TRUE;
    END IF;
    
    RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

-- Function to unlock account (admin action)
CREATE OR REPLACE FUNCTION unlock_account(user_identity_id VARCHAR(255))
RETURNS BOOLEAN AS $$
BEGIN
    UPDATE identity 
    SET account_locked_at = NULL,
        account_locked_until = NULL,
        failed_login_attempts = 0
    WHERE user_id = user_identity_id;
    
    RETURN FOUND;
END;
$$ LANGUAGE plpgsql;

-- Cleanup function for expired tokens
CREATE OR REPLACE FUNCTION cleanup_expired_auth_tokens()
RETURNS INTEGER AS $$
DECLARE
    cleaned_count INTEGER := 0;
BEGIN
    -- Clear expired password reset tokens
    UPDATE identity 
    SET password_reset_token = NULL,
        password_reset_expires_at = NULL
    WHERE password_reset_expires_at IS NOT NULL 
      AND password_reset_expires_at < CURRENT_TIMESTAMP;
    
    GET DIAGNOSTICS cleaned_count = ROW_COUNT;
    
    -- Clear expired email verification tokens
    UPDATE identity 
    SET email_verification_token = NULL,
        email_verification_expires_at = NULL
    WHERE email_verification_expires_at IS NOT NULL 
      AND email_verification_expires_at < CURRENT_TIMESTAMP;
    
    -- Clear expired account locks
    UPDATE identity 
    SET account_locked_at = NULL,
        account_locked_until = NULL
    WHERE account_locked_until IS NOT NULL 
      AND account_locked_until < CURRENT_TIMESTAMP;
    
    RETURN cleaned_count;
END;
$$ LANGUAGE plpgsql;

-- Add comments for documentation
COMMENT ON COLUMN identity.password_hash IS 'BCrypt hashed password for fallback authentication (REQ-004)';
COMMENT ON COLUMN identity.password_salt IS 'Password salt (if not using BCrypt built-in salt)';
COMMENT ON COLUMN identity.mfa_enabled IS 'Whether multi-factor authentication is enabled (SEC-005)';
COMMENT ON COLUMN identity.mfa_secret IS 'TOTP secret for authenticator apps (base32 encoded)';
COMMENT ON COLUMN identity.mfa_backup_codes IS 'JSON array of backup codes for MFA recovery';
COMMENT ON COLUMN identity.failed_login_attempts IS 'Counter for failed login attempts (SEC-005)';
COMMENT ON COLUMN identity.account_locked_until IS 'Account lock expiration timestamp';
COMMENT ON COLUMN identity.email_verified IS 'Whether email address has been verified for OTP delivery';
COMMENT ON COLUMN identity.phone_verified IS 'Whether phone number has been verified for OTP delivery';
COMMENT ON COLUMN identity.security_score IS 'Calculated security score based on user behavior and settings (0-100)';
COMMENT ON COLUMN identity.risk_profile IS 'Risk assessment profile: LOW, NORMAL, ELEVATED, HIGH';
COMMENT ON COLUMN identity.notification_preferences IS 'JSON object for user notification preferences';
COMMENT ON COLUMN identity.terms_accepted_at IS 'Timestamp when user accepted terms of service (compliance)';
COMMENT ON COLUMN identity.privacy_policy_accepted_at IS 'Timestamp when user accepted privacy policy (GDPR compliance)';

-- Add example data migration for existing users (update existing users to have default security settings)
UPDATE identity 
SET email_verified = CASE 
    WHEN email IS NOT NULL AND email != '' THEN TRUE 
    ELSE FALSE 
END,
phone_verified = CASE 
    WHEN phone_number IS NOT NULL AND phone_number != '' THEN TRUE 
    ELSE FALSE 
END,
security_score = 50,
risk_profile = 'NORMAL',
timezone = 'UTC',
locale = 'en_US'
WHERE email_verified IS NULL; -- Only update records that haven't been updated yet
