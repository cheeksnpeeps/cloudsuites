-- =====================================================
-- CloudSuites Authentication: Session Management Tables
-- Version: V3
-- Purpose: Create tables for refresh tokens and user sessions
-- Author: CloudSuites Development Team
-- Date: September 10, 2025
-- =====================================================

-- Table: user_sessions
-- Purpose: Track user sessions across devices with refresh token management
-- Note: Implements SEC-003: Refresh token rotation and session management
CREATE TABLE user_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    refresh_token_hash VARCHAR(255) NOT NULL, -- Hashed refresh token for security
    access_token_jti VARCHAR(255), -- JWT ID for access token tracking
    device_fingerprint VARCHAR(500),
    device_name VARCHAR(200),
    device_type VARCHAR(50) CHECK (device_type IN ('WEB', 'MOBILE_IOS', 'MOBILE_ANDROID', 'TABLET', 'DESKTOP')),
    user_agent TEXT,
    ip_address INET,
    location VARCHAR(200),
    is_trusted_device BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_activity_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(255)
);

-- Table: refresh_token_rotation
-- Purpose: Track refresh token rotation for security (SEC-003)
-- Note: Maintains audit trail of token usage and rotation
CREATE TABLE refresh_token_rotation (
    rotation_id VARCHAR(255) PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    old_token_hash VARCHAR(255) NOT NULL,
    new_token_hash VARCHAR(255) NOT NULL,
    rotation_reason VARCHAR(50) NOT NULL DEFAULT 'REFRESH' 
        CHECK (rotation_reason IN ('REFRESH', 'LOGOUT', 'SECURITY_REVOKE', 'EXPIRED', 'SUSPICIOUS_ACTIVITY')),
    rotated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_ip INET,
    user_agent TEXT
);

-- Table: device_trust
-- Purpose: Manage trusted devices for "remember me" functionality (REQ-008)
-- Note: Implements device trust and "keep me logged in" functionality
CREATE TABLE device_trust (
    trust_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    device_fingerprint VARCHAR(500) NOT NULL,
    device_name VARCHAR(200),
    trust_level VARCHAR(20) NOT NULL DEFAULT 'NONE' 
        CHECK (trust_level IN ('NONE', 'BASIC', 'TRUSTED', 'VERIFIED')),
    trust_score INTEGER NOT NULL DEFAULT 0 CHECK (trust_score >= 0 AND trust_score <= 100),
    last_seen_ip INET,
    last_seen_location VARCHAR(200),
    first_trusted_at TIMESTAMP,
    last_verified_at TIMESTAMP,
    trust_expires_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: session_audit
-- Purpose: Audit trail for session-related security events
-- Note: Supports SEC-002: Audit logging for authentication events
CREATE TABLE session_audit (
    audit_id VARCHAR(255) PRIMARY KEY,
    session_id VARCHAR(255),
    user_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL 
        CHECK (event_type IN ('SESSION_START', 'SESSION_END', 'TOKEN_REFRESH', 'TOKEN_REVOKE', 'DEVICE_TRUST_CHANGE', 'SUSPICIOUS_ACTIVITY')),
    event_description TEXT,
    ip_address INET,
    user_agent TEXT,
    device_fingerprint VARCHAR(500),
    risk_score INTEGER DEFAULT 0 CHECK (risk_score >= 0 AND risk_score <= 100),
    additional_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Performance indexes
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_active ON user_sessions(user_id, is_active) WHERE is_active = TRUE;
CREATE INDEX idx_user_sessions_expires_at ON user_sessions(expires_at);
CREATE INDEX idx_user_sessions_last_activity ON user_sessions(last_activity_at);
CREATE INDEX idx_user_sessions_device_fingerprint ON user_sessions(device_fingerprint) WHERE device_fingerprint IS NOT NULL;
CREATE INDEX idx_user_sessions_refresh_token_hash ON user_sessions(refresh_token_hash);

CREATE INDEX idx_refresh_token_rotation_session_id ON refresh_token_rotation(session_id);
CREATE INDEX idx_refresh_token_rotation_rotated_at ON refresh_token_rotation(rotated_at);
CREATE INDEX idx_refresh_token_rotation_old_token ON refresh_token_rotation(old_token_hash);

CREATE INDEX idx_device_trust_user_id ON device_trust(user_id);
CREATE INDEX idx_device_trust_fingerprint ON device_trust(device_fingerprint);
CREATE INDEX idx_device_trust_active ON device_trust(user_id, is_active) WHERE is_active = TRUE;
CREATE INDEX idx_device_trust_expires_at ON device_trust(trust_expires_at) WHERE trust_expires_at IS NOT NULL;

CREATE INDEX idx_session_audit_user_id ON session_audit(user_id);
CREATE INDEX idx_session_audit_session_id ON session_audit(session_id) WHERE session_id IS NOT NULL;
CREATE INDEX idx_session_audit_event_type ON session_audit(event_type);
CREATE INDEX idx_session_audit_created_at ON session_audit(created_at);
CREATE INDEX idx_session_audit_risk_score ON session_audit(risk_score) WHERE risk_score > 50;

-- Foreign key constraints
ALTER TABLE user_sessions 
    ADD CONSTRAINT fk_user_sessions_user_id 
    FOREIGN KEY (user_id) REFERENCES identity(user_id) ON DELETE CASCADE;

ALTER TABLE refresh_token_rotation 
    ADD CONSTRAINT fk_refresh_token_rotation_session_id 
    FOREIGN KEY (session_id) REFERENCES user_sessions(session_id) ON DELETE CASCADE;

ALTER TABLE device_trust 
    ADD CONSTRAINT fk_device_trust_user_id 
    FOREIGN KEY (user_id) REFERENCES identity(user_id) ON DELETE CASCADE;

ALTER TABLE session_audit 
    ADD CONSTRAINT fk_session_audit_user_id 
    FOREIGN KEY (user_id) REFERENCES identity(user_id) ON DELETE CASCADE;

ALTER TABLE session_audit 
    ADD CONSTRAINT fk_session_audit_session_id 
    FOREIGN KEY (session_id) REFERENCES user_sessions(session_id) ON DELETE SET NULL;

-- Unique constraints
-- One active refresh token per session
CREATE UNIQUE INDEX idx_user_sessions_unique_refresh_token 
    ON user_sessions(refresh_token_hash) 
    WHERE is_active = TRUE;

-- One device trust record per user-device combination
CREATE UNIQUE INDEX idx_device_trust_unique_user_device 
    ON device_trust(user_id, device_fingerprint) 
    WHERE is_active = TRUE;

-- Triggers for automatic timestamp updates
CREATE OR REPLACE FUNCTION update_user_sessions_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified_at = CURRENT_TIMESTAMP;
    -- Update last_activity_at on any change except creation
    IF TG_OP = 'UPDATE' AND OLD.last_activity_at = NEW.last_activity_at THEN
        NEW.last_activity_at = CURRENT_TIMESTAMP;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_user_sessions_update_timestamp
    BEFORE UPDATE ON user_sessions
    FOR EACH ROW
    EXECUTE FUNCTION update_user_sessions_timestamp();

CREATE OR REPLACE FUNCTION update_device_trust_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_device_trust_update_timestamp
    BEFORE UPDATE ON device_trust
    FOR EACH ROW
    EXECUTE FUNCTION update_device_trust_timestamp();

-- Cleanup function for expired sessions (can be called by scheduled job)
CREATE OR REPLACE FUNCTION cleanup_expired_sessions()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    -- Mark expired sessions as inactive
    UPDATE user_sessions 
    SET is_active = FALSE, last_modified_at = CURRENT_TIMESTAMP
    WHERE expires_at < CURRENT_TIMESTAMP AND is_active = TRUE;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    
    -- Clean up old rotation records (older than 90 days)
    DELETE FROM refresh_token_rotation 
    WHERE rotated_at < CURRENT_TIMESTAMP - INTERVAL '90 days';
    
    -- Clean up old audit records (older than 2 years)
    DELETE FROM session_audit 
    WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '2 years';
    
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Table and column comments
COMMENT ON TABLE user_sessions IS 'User session management with refresh token rotation and device tracking';
COMMENT ON COLUMN user_sessions.refresh_token_hash IS 'Hashed refresh token (BCrypt) for security';
COMMENT ON COLUMN user_sessions.device_fingerprint IS 'Browser/device fingerprint for identification';
COMMENT ON COLUMN user_sessions.is_trusted_device IS 'Whether device is trusted for reduced authentication';

COMMENT ON TABLE refresh_token_rotation IS 'Audit trail for refresh token rotation implementing SEC-003';
COMMENT ON COLUMN refresh_token_rotation.rotation_reason IS 'Reason for token rotation (refresh, logout, security)';

COMMENT ON TABLE device_trust IS 'Device trust management for "remember me" functionality (REQ-008)';
COMMENT ON COLUMN device_trust.trust_level IS 'Trust level: NONE, BASIC, TRUSTED, VERIFIED';
COMMENT ON COLUMN device_trust.trust_score IS 'Numerical trust score (0-100) based on usage patterns';

COMMENT ON TABLE session_audit IS 'Session-related security audit events supporting SEC-002';
COMMENT ON COLUMN session_audit.risk_score IS 'Risk assessment score (0-100) for the session event';
COMMENT ON COLUMN session_audit.additional_data IS 'JSON data for extended event information';
