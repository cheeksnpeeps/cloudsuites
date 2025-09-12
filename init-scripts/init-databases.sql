-- Create required database roles for CloudSuites
-- These roles are referenced in migration scripts (V1__Initial_Schema.sql)

-- Create csuser role if it doesn't exist (for schema ownership)
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'csuser') THEN
        CREATE ROLE csuser WITH LOGIN PASSWORD 'csPassw0rd';
        GRANT ALL PRIVILEGES ON DATABASE cloudsuites TO csuser;
    END IF;
END $$;

-- Create root role if it doesn't exist (for admin operations)
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'root') THEN
        CREATE ROLE root WITH LOGIN PASSWORD 'root' SUPERUSER;
    END IF;
END $$;

-- Grant necessary permissions to csuser
GRANT CREATE ON DATABASE cloudsuites TO csuser;
GRANT CONNECT ON DATABASE cloudsuites TO csuser;

-- Create the test database for integration tests
CREATE DATABASE cloudsuites_test;

-- Grant permissions on test database to csuser
GRANT ALL PRIVILEGES ON DATABASE cloudsuites_test TO csuser;
GRANT CREATE ON DATABASE cloudsuites_test TO csuser;
GRANT CONNECT ON DATABASE cloudsuites_test TO csuser;