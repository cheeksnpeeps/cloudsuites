-- =====================================================
-- CloudSuites Authentication: Audit Logging Tables
-- Version: V4
-- Purpose: Create comprehensive audit trail for authentication events
-- Author: CloudSuites Development Team
-- Date: September 10, 2025
-- =====================================================

-- Table: auth_audit_events
-- Purpose: Comprehensive audit logging for all authentication-related events
-- Note: Implements SEC-002: Audit logging for all authentication events
CREATE TABLE auth_audit_events (
    audit_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255),
    event_type VARCHAR(50) NOT NULL CHECK (event_type IN (
        'LOGIN_SUCCESS', 'LOGIN_FAILURE', 'LOGOUT', 'OTP_REQUEST', 'OTP_VERIFY_SUCCESS', 
        'OTP_VERIFY_FAILURE', 'PASSWORD_RESET_REQUEST', 'PASSWORD_RESET_SUCCESS',
        'PASSWORD_CHANGE', 'ACCOUNT_LOCKED', 'ACCOUNT_UNLOCKED', 'MFA_ENABLED',
        'MFA_DISABLED', 'DEVICE_TRUSTED', 'DEVICE_UNTRUSTED', 'TOKEN_REFRESH',
        'TOKEN_REVOKED', 'SESSION_EXPIRED', 'SUSPICIOUS_ACTIVITY', 'ADMIN_IMPERSONATION',
        'PERMISSION_DENIED', 'RATE_LIMIT_EXCEEDED', 'SECURITY_POLICY_VIOLATION'
    )),
    event_category VARCHAR(30) NOT NULL DEFAULT 'AUTHENTICATION' CHECK (event_category IN (
        'AUTHENTICATION', 'AUTHORIZATION', 'ACCOUNT_MANAGEMENT', 'SECURITY', 'ADMINISTRATION'
    )),
    event_description TEXT NOT NULL,
    risk_level VARCHAR(20) NOT NULL DEFAULT 'LOW' CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    risk_score INTEGER NOT NULL DEFAULT 0 CHECK (risk_score >= 0 AND risk_score <= 100),
    
    -- Request context
    ip_address INET,
    user_agent TEXT,
    request_path VARCHAR(500),
    http_method VARCHAR(10),
    
    -- Authentication context
    authentication_method VARCHAR(30) CHECK (authentication_method IN (
        'OTP_SMS', 'OTP_EMAIL', 'PASSWORD', 'BIOMETRIC', 'SSO', 'REFRESH_TOKEN', 'MFA'
    )),
    session_id VARCHAR(255),
    device_fingerprint VARCHAR(500),
    
    -- Geolocation context
    country_code VARCHAR(3),
    region VARCHAR(100),
    city VARCHAR(100),
    timezone VARCHAR(50),
    
    -- Additional context data
    additional_data JSONB,
    failure_reason VARCHAR(200),
    affected_resource VARCHAR(255),
    
    -- Metadata
    event_id VARCHAR(255), -- External event ID for correlation
    correlation_id VARCHAR(255), -- Request correlation ID
    trace_id VARCHAR(255), -- Distributed tracing ID
    
    -- Audit trail
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recorded_by VARCHAR(255) DEFAULT 'SYSTEM'
);

-- Table: security_violations
-- Purpose: Track security policy violations and anomalous behavior
-- Note: Supports advanced threat detection and compliance reporting
CREATE TABLE security_violations (
    violation_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255),
    violation_type VARCHAR(50) NOT NULL CHECK (violation_type IN (
        'BRUTE_FORCE', 'RATE_LIMIT_EXCEEDED', 'INVALID_ACCESS_PATTERN', 
        'GEO_ANOMALY', 'DEVICE_ANOMALY', 'TIME_ANOMALY', 'PRIVILEGE_ESCALATION',
        'CONCURRENT_SESSION_ABUSE', 'TOKEN_ABUSE', 'SUSPICIOUS_API_USAGE'
    )),
    severity VARCHAR(20) NOT NULL CHECK (severity IN ('INFO', 'WARNING', 'MAJOR', 'CRITICAL')),
    violation_description TEXT NOT NULL,
    
    -- Detection context
    detection_method VARCHAR(50) NOT NULL DEFAULT 'RULE_BASED',
    confidence_score INTEGER NOT NULL DEFAULT 0 CHECK (confidence_score >= 0 AND confidence_score <= 100),
    
    -- Request context at time of violation
    ip_address INET,
    user_agent TEXT,
    session_id VARCHAR(255),
    request_path VARCHAR(500),
    
    -- Violation details
    threshold_value NUMERIC,
    actual_value NUMERIC,
    time_window_minutes INTEGER,
    
    -- Response actions
    action_taken VARCHAR(50) CHECK (action_taken IN (
        'NONE', 'LOG_ONLY', 'RATE_LIMITED', 'TEMPORARY_BLOCK', 'ACCOUNT_LOCKED', 
        'SESSION_TERMINATED', 'ADMIN_NOTIFIED', 'SECURITY_TEAM_ALERTED'
    )),
    auto_remediated BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Resolution tracking
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'INVESTIGATING', 'RESOLVED', 'FALSE_POSITIVE')),
    resolved_at TIMESTAMP,
    resolved_by VARCHAR(255),
    resolution_notes TEXT,
    
    -- Additional context
    additional_data JSONB,
    related_audit_ids TEXT[], -- Array of related audit event IDs
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: compliance_audit_log
-- Purpose: Specific audit events for compliance reporting (SOC2, GDPR, etc.)
-- Note: Supports COMP-001 and COMP-002 compliance requirements
CREATE TABLE compliance_audit_log (
    compliance_audit_id VARCHAR(255) PRIMARY KEY,
    compliance_framework VARCHAR(50) NOT NULL CHECK (compliance_framework IN (
        'SOC2', 'GDPR', 'HIPAA', 'PCI_DSS', 'ISO27001', 'CCPA', 'INTERNAL'
    )),
    requirement_id VARCHAR(100) NOT NULL, -- e.g., 'SOC2-CC6.1', 'GDPR-Art32'
    event_type VARCHAR(50) NOT NULL,
    
    -- Subject and object of audit event
    data_subject_id VARCHAR(255), -- User whose data is involved
    data_controller_id VARCHAR(255), -- Admin/system performing action
    data_processor_id VARCHAR(255), -- Service/module processing data
    
    -- Data handling context
    data_category VARCHAR(50) CHECK (data_category IN (
        'PERSONAL_DATA', 'SENSITIVE_DATA', 'FINANCIAL_DATA', 'HEALTH_DATA', 
        'AUTHENTICATION_DATA', 'BEHAVIORAL_DATA', 'TECHNICAL_DATA'
    )),
    processing_purpose VARCHAR(100),
    legal_basis VARCHAR(50), -- GDPR legal basis
    
    -- Action details
    action_performed VARCHAR(100) NOT NULL,
    data_accessed TEXT, -- Description of data accessed
    data_modified TEXT, -- Description of data modified
    retention_period VARCHAR(50),
    
    -- Consent and authorization
    consent_obtained BOOLEAN,
    consent_id VARCHAR(255),
    authorization_level VARCHAR(50),
    
    -- Technical details
    system_component VARCHAR(100),
    ip_address INET,
    encryption_used BOOLEAN DEFAULT FALSE,
    
    -- Compliance metadata
    compliance_status VARCHAR(20) DEFAULT 'COMPLIANT' CHECK (compliance_status IN (
        'COMPLIANT', 'NON_COMPLIANT', 'UNDER_REVIEW', 'EXCEPTION_GRANTED'
    )),
    retention_expires_at TIMESTAMP,
    
    additional_metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Performance indexes for auth_audit_events
CREATE INDEX idx_auth_audit_user_id ON auth_audit_events(user_id) WHERE user_id IS NOT NULL;
CREATE INDEX idx_auth_audit_event_type ON auth_audit_events(event_type);
CREATE INDEX idx_auth_audit_created_at ON auth_audit_events(created_at);
CREATE INDEX idx_auth_audit_risk_level ON auth_audit_events(risk_level) WHERE risk_level IN ('HIGH', 'CRITICAL');
CREATE INDEX idx_auth_audit_ip_address ON auth_audit_events(ip_address) WHERE ip_address IS NOT NULL;
CREATE INDEX idx_auth_audit_session_id ON auth_audit_events(session_id) WHERE session_id IS NOT NULL;
CREATE INDEX idx_auth_audit_correlation_id ON auth_audit_events(correlation_id) WHERE correlation_id IS NOT NULL;
CREATE INDEX idx_auth_audit_event_category ON auth_audit_events(event_category);

-- Performance indexes for security_violations
CREATE INDEX idx_security_violations_user_id ON security_violations(user_id) WHERE user_id IS NOT NULL;
CREATE INDEX idx_security_violations_type_severity ON security_violations(violation_type, severity);
CREATE INDEX idx_security_violations_created_at ON security_violations(created_at);
CREATE INDEX idx_security_violations_status ON security_violations(status) WHERE status = 'OPEN';
CREATE INDEX idx_security_violations_ip_address ON security_violations(ip_address) WHERE ip_address IS NOT NULL;

-- Performance indexes for compliance_audit_log
CREATE INDEX idx_compliance_audit_framework ON compliance_audit_log(compliance_framework);
CREATE INDEX idx_compliance_audit_subject_id ON compliance_audit_log(data_subject_id) WHERE data_subject_id IS NOT NULL;
CREATE INDEX idx_compliance_audit_controller_id ON compliance_audit_log(data_controller_id) WHERE data_controller_id IS NOT NULL;
CREATE INDEX idx_compliance_audit_created_at ON compliance_audit_log(created_at);
CREATE INDEX idx_compliance_audit_requirement ON compliance_audit_log(compliance_framework, requirement_id);
CREATE INDEX idx_compliance_audit_retention ON compliance_audit_log(retention_expires_at) WHERE retention_expires_at IS NOT NULL;

-- Foreign key constraints
ALTER TABLE auth_audit_events 
    ADD CONSTRAINT fk_auth_audit_user_id 
    FOREIGN KEY (user_id) REFERENCES identity(user_id) ON DELETE SET NULL;

ALTER TABLE security_violations 
    ADD CONSTRAINT fk_security_violations_user_id 
    FOREIGN KEY (user_id) REFERENCES identity(user_id) ON DELETE SET NULL;

ALTER TABLE compliance_audit_log 
    ADD CONSTRAINT fk_compliance_audit_subject_id 
    FOREIGN KEY (data_subject_id) REFERENCES identity(user_id) ON DELETE SET NULL;

ALTER TABLE compliance_audit_log 
    ADD CONSTRAINT fk_compliance_audit_controller_id 
    FOREIGN KEY (data_controller_id) REFERENCES identity(user_id) ON DELETE SET NULL;

-- Partitioning for performance (partition by month for large-scale deployments)
-- Note: Uncomment if implementing time-based partitioning
-- ALTER TABLE auth_audit_events PARTITION BY RANGE (created_at);
-- ALTER TABLE security_violations PARTITION BY RANGE (created_at);
-- ALTER TABLE compliance_audit_log PARTITION BY RANGE (created_at);

-- Trigger for automatic timestamp updates
CREATE OR REPLACE FUNCTION update_security_violations_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_security_violations_update_timestamp
    BEFORE UPDATE ON security_violations
    FOR EACH ROW
    EXECUTE FUNCTION update_security_violations_timestamp();

-- Audit retention cleanup function
CREATE OR REPLACE FUNCTION cleanup_audit_logs(retention_days INTEGER DEFAULT 2555) -- ~7 years default
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER := 0;
    cutoff_date TIMESTAMP;
BEGIN
    cutoff_date := CURRENT_TIMESTAMP - (retention_days || ' days')::INTERVAL;
    
    -- Clean up old audit events (keep longer for compliance)
    DELETE FROM auth_audit_events 
    WHERE created_at < cutoff_date 
      AND risk_level NOT IN ('HIGH', 'CRITICAL'); -- Keep high-risk events longer
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    
    -- Clean up resolved security violations older than retention period
    DELETE FROM security_violations 
    WHERE created_at < cutoff_date 
      AND status = 'RESOLVED';
    
    -- Clean up compliance logs based on retention_expires_at
    DELETE FROM compliance_audit_log 
    WHERE retention_expires_at IS NOT NULL 
      AND retention_expires_at < CURRENT_TIMESTAMP;
    
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Table and column comments for documentation
COMMENT ON TABLE auth_audit_events IS 'Comprehensive audit log for all authentication and authorization events (SEC-002)';
COMMENT ON COLUMN auth_audit_events.risk_score IS 'Calculated risk score (0-100) based on event context and user behavior';
COMMENT ON COLUMN auth_audit_events.additional_data IS 'JSON field for extended event context and metadata';

COMMENT ON TABLE security_violations IS 'Security policy violations and anomalous behavior tracking for threat detection';
COMMENT ON COLUMN security_violations.confidence_score IS 'ML/rule confidence in violation detection (0-100)';
COMMENT ON COLUMN security_violations.related_audit_ids IS 'Array of related auth_audit_events.audit_id values';

COMMENT ON TABLE compliance_audit_log IS 'Compliance-specific audit events for SOC2, GDPR, and other regulatory requirements';
COMMENT ON COLUMN compliance_audit_log.legal_basis IS 'GDPR Article 6 legal basis for processing (consent, contract, legal_obligation, etc.)';
COMMENT ON COLUMN compliance_audit_log.retention_expires_at IS 'When this audit record should be deleted per retention policy';
