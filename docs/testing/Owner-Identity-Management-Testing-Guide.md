# Owner Identity Management - Testing Guide

## Overview

This document provides comprehensive testing guidance for the Owner Identity Management business logic implemented in `OwnerServiceImpl`. The implementation follows the decision table and business rules defined in [ADR-001](../adr/ADR-001-Owner-Identity-Management.md).

## Test Coverage

### 1. Decision Table Scenarios (ADR-001)

Our comprehensive test suite covers all 5 scenarios from the business decision table:

| Test Method | Scenario | Description | Expected Result |
|-------------|----------|-------------|-----------------|
| `scenario1NoIdentityExistsCreatesIdentityAndOwner()` | 1 | No identity exists | Creates Identity + Owner (201) |
| `scenario2IdentityExistsNoOwnerCreatesOwnerWithExistingIdentity()` | 2 | Identity exists, no owner | Creates Owner with existing Identity (201) |
| `scenario3IdentityAndOwnerExistThrowsUserAlreadyExistsException()` | 3 | Identity + owner exist | Throws UserAlreadyExistsException (409) |
| `scenario4InvalidEmailThrowsInvalidOperationException()` | 4 | Invalid email | Throws InvalidOperationException (400) |
| `scenario5MissingIdentityThrowsInvalidOperationException()` | 5 | Missing identity | Throws InvalidOperationException (400) |

### 2. Email Normalization Tests

- **Test**: `emailNormalizationTrimsAndConvertsToLowercase()`
- **Purpose**: Verifies that emails like `"  TEST@EXAMPLE.COM  "` are normalized to `"test@example.com"`
- **Business Rule**: Consistent email handling prevents duplicate issues

### 3. Concurrency Tests

- **Test**: `concurrentCreationHandlesConstraintViolation()`
- **Purpose**: Validates proper handling of concurrent owner creation attempts
- **Mechanism**: Uses `CountDownLatch` and `CompletableFuture` to simulate race conditions
- **Expected**: One creation succeeds, one fails with `DataIntegrityViolationException`

### 4. User Role Management Tests

- **With Role**: `ownerWithUserRoleSavesUserRoleAfterOwnerCreation()`
- **Without Role**: `ownerWithoutUserRoleLogsWarningNoUserRoleSaved()`
- **Purpose**: Ensures proper user role persistence after owner creation

### 5. Building/Unit Association Tests

- **Valid Association**: `createOwnerWithBuildingAndUnitAssociatesCorrectly()`
- **Null Building**: `createOwnerWithNullBuildingThrowsNotFoundResponseException()`
- **Null Unit**: `createOwnerWithNullUnitThrowsNotFoundResponseException()`
- **Invalid Association**: `createOwnerWithUnitNotBelongingToBuildingThrowsInvalidOperationException()`

## Running the Tests

### Unit Tests Only
```bash
mvn test -Dtest=OwnerServiceImplTest
```

### All Property Module Tests
```bash
mvn test -pl modules/property-module
```

### Integration Tests with Docker
```bash
docker-compose up --build
# Wait for application startup
mvn test -Dspring.profiles.active=test
```

## Manual Testing Scenarios

### Scenario 1: New User Registration
```bash
curl -X POST http://localhost:8080/api/v1/buildings/building-123/owners \
  -H "Content-Type: application/json" \
  -d '{
    "identity": {
      "email": "newuser@example.com",
      "firstName": "John",
      "lastName": "Doe"
    },
    "status": "ACTIVE"
  }'
```
**Expected**: 201 Created with new identity and owner

### Scenario 2: Existing Identity, New Owner
```bash
# First, create a tenant with the same email
curl -X POST http://localhost:8080/api/v1/buildings/building-123/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "identity": {
      "email": "existing@example.com",
      "firstName": "Jane",
      "lastName": "Smith"
    }
  }'

# Then create owner with same email
curl -X POST http://localhost:8080/api/v1/buildings/building-123/owners \
  -H "Content-Type: application/json" \
  -d '{
    "identity": {
      "email": "existing@example.com",
      "firstName": "Jane",
      "lastName": "Smith"
    },
    "status": "ACTIVE"
  }'
```
**Expected**: 201 Created, reuses existing identity

### Scenario 3: Duplicate Owner Creation
```bash
# Create owner first
curl -X POST http://localhost:8080/api/v1/buildings/building-123/owners \
  -H "Content-Type: application/json" \
  -d '{
    "identity": {
      "email": "duplicate@example.com",
      "firstName": "Bob",
      "lastName": "Wilson"
    }
  }'

# Try to create again
curl -X POST http://localhost:8080/api/v1/buildings/building-123/owners \
  -H "Content-Type: application/json" \
  -d '{
    "identity": {
      "email": "duplicate@example.com",
      "firstName": "Bob",
      "lastName": "Wilson"
    }
  }'
```
**Expected**: 409 Conflict with UserAlreadyExistsException

### Scenario 4: Invalid Email
```bash
curl -X POST http://localhost:8080/api/v1/buildings/building-123/owners \
  -H "Content-Type: application/json" \
  -d '{
    "identity": {
      "email": "",
      "firstName": "Invalid",
      "lastName": "User"
    }
  }'
```
**Expected**: 400 Bad Request with InvalidOperationException

### Scenario 5: Missing Identity
```bash
curl -X POST http://localhost:8080/api/v1/buildings/building-123/owners \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ACTIVE"
  }'
```
**Expected**: 400 Bad Request with InvalidOperationException

## Email Normalization Testing

### Case and Whitespace Handling
```bash
curl -X POST http://localhost:8080/api/v1/buildings/building-123/owners \
  -H "Content-Type: application/json" \
  -d '{
    "identity": {
      "email": "  TEST@EXAMPLE.COM  ",
      "firstName": "Test",
      "lastName": "User"
    }
  }'
```
**Expected**: Email normalized to "test@example.com" before processing

## Concurrency Testing

### Parallel Creation Attempts
Use a tool like Apache Bench to simulate concurrent requests:

```bash
# Create test data file
echo '{
  "identity": {
    "email": "concurrent@example.com",
    "firstName": "Concurrent",
    "lastName": "User"
  }
}' > owner_data.json

# Run concurrent requests
ab -n 10 -c 5 -T application/json -p owner_data.json \
   http://localhost:8080/api/v1/buildings/building-123/owners
```
**Expected**: Only one request succeeds (201), others fail with 409 Conflict

## Database State Verification

### Check Identity Creation
```sql
SELECT * FROM identity WHERE email = 'test@example.com';
```

### Check Owner Creation
```sql
SELECT o.*, i.email 
FROM owner o 
JOIN identity i ON o.identity_user_id = i.user_id 
WHERE i.email = 'test@example.com';
```

### Check Multi-Persona Support
```sql
-- Should show same identity used for multiple roles
SELECT 
  i.email,
  i.first_name,
  i.last_name,
  CASE WHEN o.owner_id IS NOT NULL THEN 'Owner' END as owner_role,
  CASE WHEN t.tenant_id IS NOT NULL THEN 'Tenant' END as tenant_role
FROM identity i
LEFT JOIN owner o ON i.user_id = o.identity_user_id
LEFT JOIN tenant t ON i.user_id = t.identity_user_id
WHERE i.email = 'multi@example.com';
```

## Performance Testing

### Load Testing with K6
```javascript
import http from 'k6/http';
import { check } from 'k6';

export let options = {
  stages: [
    { duration: '2m', target: 100 }, // Ramp up
    { duration: '5m', target: 100 }, // Stay at 100 users
    { duration: '2m', target: 0 },   // Ramp down
  ],
};

export default function() {
  let payload = JSON.stringify({
    identity: {
      email: `user-${__VU}-${__ITER}@example.com`,
      firstName: 'Load',
      lastName: 'Test'
    }
  });

  let response = http.post(
    'http://localhost:8080/api/v1/buildings/building-123/owners',
    payload,
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(response, {
    'status is 201': (r) => r.status === 201,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
}
```

## Troubleshooting

### Common Issues

1. **Tests Failing Due to Network**: Maven repository connectivity issues
   - **Solution**: Run tests in offline mode or use local repository

2. **Database Connection Issues**: PostgreSQL not accessible
   - **Solution**: Ensure Docker Compose is running: `docker-compose up -d`

3. **Transaction Rollback Issues**: Test data persistence
   - **Solution**: Check `@Transactional` annotations and test configuration

4. **Mockito Verification Failures**: Mock interactions not as expected
   - **Solution**: Review mock setup and verify call order/arguments

### Debug Logging

Enable detailed logging for troubleshooting:

```yaml
logging:
  level:
    com.cloudsuites.framework.modules.property.personas.module.OwnerServiceImpl: DEBUG
    org.springframework.transaction: DEBUG
    org.hibernate.SQL: DEBUG
```

## Success Criteria

All tests should pass with:
- ✅ 100% decision table scenario coverage
- ✅ Email normalization verification
- ✅ Concurrency handling validation
- ✅ Transaction integrity maintenance
- ✅ Proper exception handling
- ✅ Multi-persona support confirmation

## References

- [ADR-001: Owner Identity Management](../adr/ADR-001-Owner-Identity-Management.md)
- [OwnerServiceImpl.java](../modules/property-module/src/main/java/com/cloudsuites/framework/modules/property/personas/module/OwnerServiceImpl.java)
- [OwnerServiceImplTest.java](../modules/property-module/src/test/java/com/cloudsuites/framework/modules/property/personas/module/OwnerServiceImplTest.java)
