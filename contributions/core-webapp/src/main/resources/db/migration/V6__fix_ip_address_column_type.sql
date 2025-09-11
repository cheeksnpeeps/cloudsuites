-- Fix IP address column type mismatch
-- Change last_password_change_ip from INET to VARCHAR to match Java String type

ALTER TABLE identity ALTER COLUMN last_password_change_ip TYPE VARCHAR(45) USING last_password_change_ip::text;
