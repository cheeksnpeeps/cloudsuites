-- Fix notification_preferences column type from JSONB to VARCHAR to match Java String field
-- This resolves the error: column "notification_preferences" is of type jsonb but expression is of type character varying

ALTER TABLE identity ALTER COLUMN notification_preferences TYPE VARCHAR(1000);

-- Fix additional_data columns in auth tables from JSONB to VARCHAR to match Java String fields
ALTER TABLE auth_audit_events ALTER COLUMN additional_data TYPE VARCHAR(2000);
ALTER TABLE session_audit ALTER COLUMN additional_data TYPE VARCHAR(2000);

-- Fix ip_address columns from INET to VARCHAR to match Java String fields
ALTER TABLE auth_audit_events ALTER COLUMN ip_address TYPE VARCHAR(45);
ALTER TABLE user_sessions ALTER COLUMN ip_address TYPE VARCHAR(45);
ALTER TABLE otp_codes ALTER COLUMN ip_address TYPE VARCHAR(45);

-- Update comments
COMMENT ON COLUMN identity.notification_preferences IS 'JSON preferences stored as string';
COMMENT ON COLUMN auth_audit_events.additional_data IS 'JSON field for extended event context and metadata (stored as string)';
COMMENT ON COLUMN session_audit.additional_data IS 'JSON data for extended event information (stored as string)';
COMMENT ON COLUMN auth_audit_events.ip_address IS 'IP address stored as string';
COMMENT ON COLUMN user_sessions.ip_address IS 'IP address stored as string';
COMMENT ON COLUMN otp_codes.ip_address IS 'IP address stored as string';
