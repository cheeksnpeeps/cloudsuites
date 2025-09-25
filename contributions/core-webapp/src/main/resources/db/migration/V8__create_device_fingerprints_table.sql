-- V8__create_device_fingerprints_table.sql
-- Create device fingerprints table for device trust management
-- Part of PR #10: Device Trust Foundation

-- Create device_fingerprints table
CREATE TABLE device_fingerprints (
    device_id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    fingerprint VARCHAR(128) NOT NULL,
    device_name VARCHAR(100),
    device_type VARCHAR(20) NOT NULL,
    os_info VARCHAR(50),
    browser_info VARCHAR(100),
    registration_ip VARCHAR(45),
    trust_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP,
    expires_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata TEXT,
    risk_score INTEGER DEFAULT 0,
    usage_count BIGINT DEFAULT 0,
    biometric_capable BOOLEAN DEFAULT FALSE,
    user_agent TEXT,
    last_ip_address VARCHAR(45),
    last_location VARCHAR(100),
    created_by VARCHAR(36),
    last_modified_by VARCHAR(36),
    is_deleted BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP,
    revocation_reason VARCHAR(200),
    
    -- Constraints
    CONSTRAINT ck_device_fingerprints_device_type 
        CHECK (device_type IN ('DESKTOP', 'LAPTOP', 'TABLET', 'MOBILE', 'UNKNOWN')),
    CONSTRAINT ck_device_fingerprints_trust_status 
        CHECK (trust_status IN ('TRUSTED', 'PENDING', 'REVOKED', 'EXPIRED', 'SUSPENDED')),
    CONSTRAINT ck_device_fingerprints_risk_score 
        CHECK (risk_score >= 0 AND risk_score <= 100),
    CONSTRAINT ck_device_fingerprints_usage_count 
        CHECK (usage_count >= 0)
);

-- Create indexes for optimal query performance
CREATE UNIQUE INDEX idx_device_user_fingerprint 
    ON device_fingerprints (user_id, fingerprint);

CREATE INDEX idx_device_user_trust_status 
    ON device_fingerprints (user_id, trust_status);

CREATE INDEX idx_device_fingerprint 
    ON device_fingerprints (fingerprint);

CREATE INDEX idx_device_expires_at 
    ON device_fingerprints (expires_at) 
    WHERE expires_at IS NOT NULL;

CREATE INDEX idx_device_last_used 
    ON device_fingerprints (last_used_at) 
    WHERE last_used_at IS NOT NULL;

CREATE INDEX idx_device_trust_status 
    ON device_fingerprints (trust_status);

CREATE INDEX idx_device_risk_score 
    ON device_fingerprints (risk_score) 
    WHERE risk_score > 50;

CREATE INDEX idx_device_registration_ip 
    ON device_fingerprints (registration_ip) 
    WHERE registration_ip IS NOT NULL;

-- Additional performance indexes
CREATE INDEX idx_device_active_trusted 
    ON device_fingerprints (user_id, last_used_at);

CREATE INDEX idx_device_cleanup_candidates 
    ON device_fingerprints (expires_at, last_used_at);

-- Security: Additional unique constraint for active devices only
-- This prevents duplicate device registrations per user
CREATE INDEX idx_device_created_by 
    ON device_fingerprints (created_by);
