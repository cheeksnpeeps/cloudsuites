-- =====================================================
-- CloudSuites Authentication: OTP Tables Migration
-- Version: V2
-- Description: Creates tables for OTP code management and rate limiting
-- Requirements: REQ-001 (OTP-first authentication), SEC-001 (Rate limiting)
-- =====================================================

-- Create enum for delivery method
CREATE TYPE otp_delivery_method AS ENUM ('SMS', 'EMAIL');

-- OTP codes table for temporary verification codes
CREATE TABLE otp_codes (
    otp_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(255) NOT NULL,
    otp_code VARCHAR(10) NOT NULL, -- Configurable length (6-8 digits)
    delivery_method otp_delivery_method NOT NULL,
    phone_number VARCHAR(20), -- Required for SMS delivery
    email_address VARCHAR(320), -- Required for EMAIL delivery (max email length per RFC)
    is_used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    verified_at TIMESTAMP WITH TIME ZONE,
    attempts_count INTEGER DEFAULT 0,
    max_attempts INTEGER DEFAULT 3,
    ip_address INET, -- Track IP for security
    user_agent TEXT, -- Track user agent for security
    
    -- Ensure delivery contact info is provided
    CONSTRAINT check_delivery_contact 
        CHECK (
            (delivery_method = 'SMS' AND phone_number IS NOT NULL) OR
            (delivery_method = 'EMAIL' AND email_address IS NOT NULL)
        ),
    
    -- Ensure OTP code format (digits only)
    CONSTRAINT check_otp_format CHECK (otp_code ~ '^[0-9]+$'),
    
    -- Ensure max attempts is reasonable
    CONSTRAINT check_max_attempts CHECK (max_attempts BETWEEN 1 AND 10),
    
    -- Ensure expires_at is after created_at
    CONSTRAINT check_expiry_time CHECK (expires_at > created_at)
);

-- Rate limiting table for OTP requests
CREATE TABLE otp_rate_limits (
    rate_limit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(255) NOT NULL,
    delivery_method otp_delivery_method NOT NULL,
    contact_info VARCHAR(320) NOT NULL, -- phone number or email
    attempt_count INTEGER DEFAULT 1,
    window_start TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    window_end TIMESTAMP WITH TIME ZONE NOT NULL,
    blocked_until TIMESTAMP WITH TIME ZONE, -- NULL if not blocked
    is_blocked BOOLEAN DEFAULT FALSE,
    ip_address INET,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Unique constraint to prevent duplicate rate limit entries per user/method/contact within time window
    CONSTRAINT unique_rate_limit_window 
        UNIQUE (user_id, delivery_method, contact_info, window_start),
    
    -- Ensure window_end is after window_start
    CONSTRAINT check_window_time CHECK (window_end > window_start),
    
    -- Ensure blocked_until is reasonable if set
    CONSTRAINT check_block_time CHECK (blocked_until IS NULL OR blocked_until > created_at)
);

-- Indexes for performance
CREATE INDEX idx_otp_codes_user_id ON otp_codes(user_id);
CREATE INDEX idx_otp_codes_expires_at ON otp_codes(expires_at);
CREATE INDEX idx_otp_codes_created_at ON otp_codes(created_at);
CREATE INDEX idx_otp_codes_delivery_method ON otp_codes(delivery_method);
CREATE INDEX idx_otp_codes_is_used ON otp_codes(is_used) WHERE is_used = FALSE;
CREATE INDEX idx_otp_codes_phone_email ON otp_codes(phone_number, email_address);

CREATE INDEX idx_rate_limits_user_id ON otp_rate_limits(user_id);
CREATE INDEX idx_rate_limits_contact_method ON otp_rate_limits(delivery_method, contact_info);
CREATE INDEX idx_rate_limits_window_start ON otp_rate_limits(window_start);
CREATE INDEX idx_rate_limits_blocked_until ON otp_rate_limits(blocked_until) WHERE blocked_until IS NOT NULL;

-- Constraints and foreign keys
-- Note: user_id references identity table (created in V1)
ALTER TABLE otp_codes 
    ADD CONSTRAINT fk_otp_codes_user_id 
    FOREIGN KEY (user_id) REFERENCES identity(user_id) ON DELETE CASCADE;

ALTER TABLE otp_rate_limits 
    ADD CONSTRAINT fk_rate_limits_user_id 
    FOREIGN KEY (user_id) REFERENCES identity(user_id) ON DELETE CASCADE;

-- Functions for OTP management
-- Note: These functions provide secure OTP operations and rate limiting

-- Function to clean up expired OTP codes (for scheduled cleanup)
CREATE OR REPLACE FUNCTION cleanup_expired_otp_codes()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM otp_codes 
    WHERE expires_at < CURRENT_TIMESTAMP 
       OR (is_used = TRUE AND verified_at < CURRENT_TIMESTAMP - INTERVAL '1 day');
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Function to check if user is rate limited for OTP requests
CREATE OR REPLACE FUNCTION is_user_rate_limited(
    user_identity_id VARCHAR(255),
    method otp_delivery_method,
    contact VARCHAR(320)
) RETURNS BOOLEAN AS $$
DECLARE
    current_limit RECORD;
    rate_limit_window INTERVAL := '15 minutes'; -- Configurable
    max_attempts_per_window INTEGER := 3; -- Configurable
BEGIN
    -- Check for active rate limit
    SELECT * INTO current_limit
    FROM otp_rate_limits
    WHERE user_id = user_identity_id 
      AND delivery_method = method 
      AND contact_info = contact
      AND window_end > CURRENT_TIMESTAMP
    ORDER BY created_at DESC
    LIMIT 1;
    
    -- If no recent rate limit record, user is not limited
    IF NOT FOUND THEN
        RETURN FALSE;
    END IF;
    
    -- Check if user is explicitly blocked
    IF current_limit.is_blocked AND current_limit.blocked_until > CURRENT_TIMESTAMP THEN
        RETURN TRUE;
    END IF;
    
    -- Check attempt count within window
    IF current_limit.attempt_count >= max_attempts_per_window THEN
        RETURN TRUE;
    END IF;
    
    RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_rate_limit_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_rate_limit_timestamp
    BEFORE UPDATE ON otp_rate_limits
    FOR EACH ROW
    EXECUTE FUNCTION update_rate_limit_timestamp();

-- Comments for documentation
COMMENT ON TABLE otp_codes IS 'Stores temporary OTP codes for user authentication with expiration and attempt tracking';
COMMENT ON TABLE otp_rate_limits IS 'Manages rate limiting for OTP requests to prevent abuse and brute force attacks';
COMMENT ON FUNCTION cleanup_expired_otp_codes() IS 'Removes expired and used OTP codes to maintain database performance';
COMMENT ON FUNCTION is_user_rate_limited(VARCHAR, otp_delivery_method, VARCHAR) IS 'Checks if user has exceeded OTP request rate limits';
