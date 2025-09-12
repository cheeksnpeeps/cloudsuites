# Database Setup for CloudSuites

## Overview
CloudSuites requires specific PostgreSQL roles for proper operation. This document explains the database setup for both local development and CI environments.

## Required Roles

### csuser
- **Purpose**: Primary application database user
- **Permissions**: Database owner, schema management
- **Used by**: Application connection, Flyway migrations
- **Password**: `csPassw0rd` (local), configurable in CI

### root  
- **Purpose**: Administrative operations
- **Permissions**: SUPERUSER
- **Used by**: Database administration, advanced operations
- **Password**: `root`

## Local Development (Docker)
Local development uses Docker Compose with PostgreSQL 17. The database is automatically initialized with required roles via:

```yaml
# compose.yaml
postgres:
  environment:
    - POSTGRES_USER=csuser
    - POSTGRES_PASSWORD=csPassw0rd
  volumes:
    - ./init-scripts:/docker-entrypoint-initdb.d
```

**Database**: `cloudsuites` (main), `cloudsuites_test` (tests)
**Port**: `59665`

## CI Environment (GitHub Actions)
GitHub Actions uses PostgreSQL service container with automatic role creation:

```yaml
# .github/workflows/ci.yml
services:
  postgres:
    env:
      POSTGRES_USER: csuser
      POSTGRES_PASSWORD: csPassw0rd
      POSTGRES_DB: cloudsuites
```

The workflow includes a database setup step that:
1. Creates the 'root' role with SUPERUSER privileges
2. Uses shell logic to check and create 'cloudsuites_test' database 
3. Grants proper permissions to csuser

**Database**: `cloudsuites_test`
**Port**: `5432`

## Troubleshooting

### "role does not exist" errors
If you see errors like:
```
FATAL: role "csuser" does not exist
FATAL: role "root" does not exist
```

**Solution**: Ensure `init-scripts/init-databases.sql` is properly executed during database initialization.

### CI Test Failures

1. Check that GitHub Actions PostgreSQL service is configured with correct environment variables
2. Verify database setup step runs successfully
3. Ensure test configuration matches CI database connection parameters

### PostgreSQL Syntax Errors in CI

If you see errors like:

```bash
syntax error at or near "\gexec"
```

**Solution**: The `\gexec` meta-command only works in interactive `psql` sessions, not in `-c` flag commands. Use shell logic instead:

```bash
# BAD: Using \gexec in -c command
psql -c "SELECT 'CREATE DATABASE test' WHERE NOT EXISTS (...)\gexec"

# GOOD: Using shell logic
if ! psql -lqt | grep -qw test_db; then
  createdb test_db
fi
```

### Local Development Issues

1. Run `docker-compose down -v` to reset database volumes
2. Check PostgreSQL container logs: `docker-compose logs postgres`
3. Verify environment variables in `.env` file

## Files Involved

- `init-scripts/init-databases.sql` - Database role creation and permissions
- `.github/workflows/ci.yml` - CI environment database setup
- `src/main/resources/application-test.yml` - Test database configuration

## Migration Expectations

Flyway expects tables to be owned by the `csuser` role:

```sql

### Local Development Issues
1. Run `docker-compose down -v` to reset database volumes
2. Run `docker-compose up --build` to reinitialize with latest scripts
3. Check that init scripts are properly mounted in container

## Files Involved
- `init-scripts/init-databases.sql` - Database role creation and permissions
- `compose.yaml` - Local Docker PostgreSQL configuration  
- `.github/workflows/ci.yml` - CI PostgreSQL service configuration
- `contributions/core-webapp/src/test/resources/application-test.yml` - Test database configuration

## Migration Expectations
All Flyway migrations in `db/migration/` expect tables to be owned by `csuser`:
```sql
ALTER TABLE public.address OWNER TO csuser;
```

This is automatically handled when migrations run with proper role setup.
