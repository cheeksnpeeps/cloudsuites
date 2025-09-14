# GitHub Copilot Instructions for CloudSuites

## 🚨 Git Workflow - CRITICAL REQUIREMENT

**ALL DEVELOPMENT MUST BE DONE ON FEATURE BRANCHES. NEVER COMMIT DIRECTLY TO MAIN.**

### Required Branch Strategy
- **Feature Branches**: `feat/feature-name` for new features
- **Bug Fixes**: `fix/bug-description` for bug fixes  
- **Documentation**: `docs/topic` for documentation updates
- **Refactoring**: `refactor/scope` for code improvements
- **Testing**: `test/scope` for test enhancements

### Before Any Commits
```bash
# 1. Verify you're on a feature branch (NOT main)
git branch --show-current

# 2. If on main, immediately create feature branch
git checkout -b feat/your-feature-name

# 3. Run safety check before committing
./.github/scripts/check-branch-safety.sh
```

### Git Safety Rules
- ✅ **Always check current branch before committing**
- ✅ **Create descriptive branch names following conventions**
- ✅ **Use atomic commits with clear messages**
- ❌ **NEVER commit directly to main branch**
- ❌ **NEVER force push to main branch**
- ❌ **NEVER merge branches locally without PR review**

See [Git Workflow Standards](.github/git-workflow-standards.md) for complete guidelines.

## Project Overview

CloudSuites is a **property management platform** (Java 21 + Spring Boot 3.3.2) with a **multi-module Maven architecture**:
- `modules/` - JPA entities and core domain logic
- `services/` - Business services and DTOs  
- `contributions/core-webapp/` - REST controllers and web layer

**Critical pattern**: Services import from modules, webapp imports from services. Never reverse this dependency flow.

**Service Implementation Location**: All service implementations (classes ending in `*ServiceImpl`) belong in the `modules/` layer, NOT in the `services/` layer. The `services/` layer contains only interfaces and DTOs. This pattern ensures proper dependency separation where services define contracts and modules provide implementations.

**Repository Location**: All repository interfaces (classes ending in `*Repository`) belong in the `modules/` layer, specifically in `modules/*/src/main/java/com/cloudsuites/framework/modules/*/repository/`. Repositories are data access components that should be implemented in the modules layer where the actual data access logic resides.

## 🔐 Authentication Implementation (Active Development)

**When working on authentication features**, reference the comprehensive implementation plan:

- **Master Documentation**: `plan/backend-authentication-onboarding-platform-1.md` - Requirements, constraints, gap analysis
- **Implementation Roadmap**: `plan/auth-implementation-roadmap.md` - 6-week sprint plan with 50+ PRs
- **Daily Task Tracker**: `plan/auth-implementation-tracker.md` - Current sprint status and agent tasks
- **Agent Usage Guide**: `plan/auth-agent-guide.md` - How to use the documentation system
- **Copilot Addendum**: `.github/auth-copilot-instructions.md` - Authentication-specific development rules

**ALWAYS check the tracker first** for current sprint status before starting authentication work.

## Development Environment

**Docker-first development** - use `docker-compose up --build` for all development. The application requires:
- PostgreSQL 17 on port 59665
- Environment variables loaded from `.env` file
- Flyway migrations run automatically on startup

**Maven Configuration**: Always use the project's custom settings file in Maven commands:
```bash
mvn -s .mvn/settings.xml [command]
```
The custom settings force all dependencies through public Maven Central and Spring repositories for consistency.

Access points: http://localhost:8080 (app), http://localhost:8080/swagger-ui/index.html (API docs)

## Database & Entity Patterns

**UUID Primary Keys**: All entities use `String` IDs with `@Id` annotation (VARCHAR(255) in DB)
**Audit Fields**: Include `createdAt`, `lastModifiedAt`, `createdBy`, `lastModifiedBy` in entities
**Schema Management**: Flyway migrations in `src/main/resources/db/migration/` + Hibernate DDL auto-update
**Partitioning**: `amenity_booking` table partitioned by date for performance

Example entity pattern:
```java
@Entity
public class Amenity {
    @Id
    private String amenityId;  // UUID as String
    private LocalDateTime createdAt;
    private String createdBy;
    // ... other fields
}
```

## Security & API Patterns

**Role-based security** with `@PreAuthorize` annotations on controllers:
```java
@PreAuthorize("hasRole('TENANT') or hasRole('ADMIN')")
@GetMapping("/amenities")
```

**Complete Role Hierarchy**:
```
SUPER_ADMIN > BUSINESS_ADMIN
SUPER_ADMIN > BUILDINGS_ADMIN
BUILDINGS_ADMIN > THIRD_PARTY_ADMIN
THIRD_PARTY_ADMIN > ALL_ADMIN
ALL_ADMIN > PROPERTY_MANAGER
PROPERTY_MANAGER > BUILDING_SUPERVISOR
BUILDING_SUPERVISOR > BUILDING_SECURITY
ACCOUNTING_FINANCE_MANAGER > [LEASING_AGENT, CUSTOMER_SERVICE_REPRESENTATIVE, MAINTENANCE_TECHNICIAN, OTHER]
OTHER > ALL_STAFF
```

**Public Endpoints**: `/api/v1/auth/**`, `/actuator/**`, `/swagger-ui/**`, `/v3/api-docs/**`
**API Structure**: All endpoints under `/api/v1/` with building-scoped URLs: `/api/v1/buildings/{buildingId}/amenities/{amenityId}`
**DTO Mapping**: Use ModelMapper in service layer, separate DTOs for API contracts in webapp layer.

## Data Access Patterns

**JPA/Hibernate**: Auto DDL update mode, HikariCP connection pooling, lazy loading disabled (`spring.jpa.open-in-view: false`)
**R2DBC Reactive**: Separate reactive repositories for high-performance scenarios
**Migration Strategy**: Flyway for structure + Hibernate for entity evolution

## Service Layer Conventions

**Service naming**: `[Entity]Service` for CRUD, `[Entity][Operation]Service` for complex operations
**Transaction boundaries**: `@Transactional` on service methods, not controllers
**Exception handling**: Use custom exceptions like `NotFoundResponseException`, `InvalidOperationException`
**Package naming**: `com.cloudsuites.framework.*`

## Key Integration Points

**Authentication**: JWT tokens (256-bit secret) + Auth0 integration via `SecurityConfiguration.java`
**Database**: Dual JPA (synchronous) + R2DBC (reactive) support configured in `application.yml`
**API Documentation**: All controllers must have Swagger annotations (`@Operation`, `@ApiResponse`)
**Frontend**: React 18 + Bootstrap 5 integrated via Maven frontend plugin

## Business Domain Context

**Property Management**: Multi-building platform with management companies, units, floors, owners, and tenants
**Amenity Booking**: 18 amenity types (pools, gyms, courts, etc.) with request/approval workflow, custom rules, and fee management
**User Personas**: Identity system supporting multiple roles per user (admin, staff, tenant, owner) with building associations
**Hierarchical Structure**: Building → Floor → Unit → Tenant/Owner relationships

## Environment & Deployment

**Profiles**: `dev`, `prod`, `test` profiles with different configurations
**Docker**: Multi-stage build (Maven → JRE Alpine), non-root user, container-aware JVM settings
**Health Checks**: Application and database health monitoring via Spring Boot Actuator
**Monitoring**: Structured logging with correlation IDs, connection pool metrics

## Common Workflows

**Git workflow for any code changes**:
1. ⚠️ **CRITICAL**: Verify you're on a feature branch with `git branch --show-current`
2. If on main, immediately create feature branch: `git checkout -b feat/your-feature-name`
3. Run safety check: `./.github/scripts/check-branch-safety.sh`
4. Make your changes on the feature branch
5. Commit with descriptive messages: `git commit -m "type: description"`
6. Push feature branch: `git push origin feat/your-feature-name`
7. Create Pull Request via GitHub UI

**Adding new API endpoint**:
1. Create/update entity in appropriate `modules/` 
2. Add service method in `services/`
3. Create DTO and controller in `contributions/core-webapp/`
4. Add security annotations and Swagger docs

**Database changes**: Create Flyway migration script, let Hibernate handle entity updates automatically.
**Testing**: Access Swagger UI for API testing, check actuator health endpoint, use Docker logs for debugging.
**Maven commands**: Always use `-s .mvn/settings.xml` parameter for consistent dependency resolution.

## Common Issues & Solutions

**Schema Mismatches**: Run Flyway migrations before schema validation
**JWT Expiration**: Check token validity periods in environment variables
**CORS Errors**: Verify allowed origins configuration
**Migration Conflicts**: Resolve version conflicts in flyway_schema_history table
**Application won't start**: Check Docker logs and database connectivity
**Database issues**: Reset with `docker-compose down -v && docker-compose up --build`
**Configuration problems**: Verify all environment variables in `.env`

## Testing & API Validation

**Comprehensive API Test Script**: Use `test-all-apis.sh` for complete API validation:
```bash
# Run with default 1-year JWT token (recommended)
./test-all-apis.sh

# Run with custom JWT token (optional)
./test-all-apis.sh "JWT_TOKEN_HERE"

# Example with custom token:
./test-all-apis.sh "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBRE0tMDFLM1o0TkgxVkdHVlBWUjFaNThFTjJNS0YiLCJhdWQiOlsiQ2xvdWRTdWl0ZXMiXSwidHlwZSI6IkFETUlOIiwidXNlcklkIjoiSUQtMDFLM1o0TkgxVEZFRzEyTjlUOVBNRjlRWEoiLCJpc3MiOiJjbG91ZHN1aXRlcyIsImlhdCI6MTc1NzI5Njg5MCwiZXhwIjoxNzg4ODMyODkwfQ.ycFZdWAYfTts6VrmJ714eTbIjyHJIxh5-kFZRq7dQAhDlqrI1qtawmYFPAkOdw"
```

**Getting JWT Token**: Use the long-lived test token below (1-year expiration):
```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBRE0tMDFLM1o0TkgxVkdHVlBWUjFaNThFTjJNS0YiLCJhdWQiOlsiQ2xvdWRTdWl0ZXMiXSwidHlwZSI6IkFETUlOIiwidXNlcklkIjoiSUQtMDFLM1o0TkgxVEZFRzEyTjlUOVBNRjlRWEoiLCJpc3MiOiJjbG91ZHN1aXRlcyIsImlhdCI6MTc1NzI5Njg5MCwiZXhwIjoxNzg4ODMyODkwfQ.ycFZdWAYfTts6VrmJ714eTbIjyHJIgkeVGteMghJc18Jaq_7nnKJIxh5-kFZRq7dQAhDlqrI1qtawmYFPAkOdw
```
