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

## 🏗️ Project Architecture & Structure

CloudSuites is a **property management platform** built with **Java 21** and **Spring Boot 3.3.2** using a **multi-module Maven architecture** that enforces clean separation of concerns.

### 📋 Boy Scout Rule - MANDATORY
**"Always leave the code cleaner than you found it"** - Every agent MUST:
- ✅ **Improve code quality** while making changes (refactor, add documentation, fix warnings)
- ✅ **Follow established patterns** and enhance them where possible
- ✅ **Add missing tests** for code you touch
- ✅ **Update documentation** when modifying functionality
- ✅ **Fix code smells** and technical debt in the area you're working
- ✅ **Ensure compliance** with project structure and naming conventions
- ❌ **Never leave code in worse state** than you found it

### 🏢 Multi-Module Architecture

#### **Core Architecture Layers** (Dependency Flow: webapp → services → modules)

```
cloudsuites/
├── contributions/core-webapp/     # 🌐 Web Layer (Controllers, REST APIs, DTOs)
├── services/                      # 📋 Service Contracts (Interfaces, DTOs)
└── modules/                       # ⚙️ Implementation Layer (Entities, Repos, ServiceImpls)
```

**CRITICAL DEPENDENCY RULE**: `webapp` imports from `services` → `services` imports from `modules` → **NEVER REVERSE THIS FLOW**

#### **1. contributions/core-webapp/ - Web & Presentation Layer**
```
contributions/core-webapp/
├── src/main/java/com/cloudsuites/framework/webapp/
│   ├── rest/                     # REST Controllers
│   │   ├── amenity/             # AmenityController
│   │   ├── property/            # PropertyController
│   │   └── authentication/      # AuthController
│   ├── dto/                     # Web-specific DTOs
│   ├── config/                  # Web configuration
│   └── security/                # Security configuration
├── src/main/resources/
│   ├── db/migration/           # Flyway database migrations
│   └── application.yml         # Main configuration
└── pom.xml                     # Web dependencies only
```

**Responsibilities:**
- ✅ REST controllers with `@RestController`
- ✅ Request/Response DTOs for API contracts
- ✅ Security configuration (`SecurityConfiguration.java`)
- ✅ Web-specific configuration and filters
- ✅ Swagger/OpenAPI documentation
- ✅ Flyway database migrations (centralized)
- ❌ **NO business logic** - delegate to services
- ❌ **NO direct entity access** - use service layer

#### **2. services/ - Service Contract Layer**
```
services/
├── amenity-service/src/main/java/com/cloudsuites/framework/services/amenity/
│   ├── AmenityService.java              # Service interface
│   ├── AmenityBookingService.java       # Service interface
│   └── dto/                            # Service DTOs
├── identity-service/src/main/java/com/cloudsuites/framework/services/user/
│   ├── UserService.java                # Service interface
│   ├── RefreshTokenService.java        # Service interface
│   └── dto/                            # Service DTOs
└── property-service/                   # Property management services
```

**Responsibilities:**
- ✅ Service interfaces defining business contracts
- ✅ Service-specific DTOs for data transfer
- ✅ Business operation definitions
- ✅ Service-level documentation
- ❌ **NO implementations** - only interfaces and DTOs
- ❌ **NO JPA entities** - use DTOs for data transfer
- ❌ **NO repositories** - define in modules layer

#### **3. modules/ - Implementation & Data Layer**
```
modules/
├── amenity-module/src/main/java/com/cloudsuites/framework/modules/amenity/
│   ├── entity/                         # JPA entities
│   │   ├── Amenity.java
│   │   └── AmenityBooking.java
│   ├── repository/                     # JPA repositories
│   │   ├── AmenityRepository.java
│   │   └── AmenityBookingRepository.java
│   └── service/impl/                   # Service implementations
│       ├── AmenityServiceImpl.java
│       └── AmenityBookingServiceImpl.java
├── identity-module/
│   ├── entity/                         # User, Identity, UserSession entities
│   ├── repository/                     # User repositories
│   └── service/impl/                   # UserServiceImpl, etc.
└── property-module/                    # Property entities and implementations
```

**Responsibilities:**
- ✅ JPA entities with `@Entity` annotations
- ✅ Repository interfaces extending `JpaRepository`
- ✅ Service implementations (`*ServiceImpl`) with `@Service`
- ✅ Data access logic and persistence
- ✅ Business logic implementation
- ✅ Database relationships and constraints
- ❌ **NO web concerns** - pure business and data logic

### 🔧 Technology Stack & Versions

#### **Core Technologies**
- **Java:** 21 (LTS) - Use modern features (virtual threads, pattern matching, records)
- **Spring Boot:** 3.3.2 - Latest stable with native compilation support
- **Spring Framework:** 6.x - Reactive and traditional stack
- **Maven:** 3.9+ - Multi-module build management

#### **Database Stack**
- **PostgreSQL:** 17 - Primary database with advanced features
- **JPA/Hibernate:** 6.x - ORM with auto DDL update
- **R2DBC:** Reactive database access for high-performance scenarios
- **Flyway:** Database migration management
- **HikariCP:** Connection pooling

#### **Development Tools**
- **Docker:** Containerized development environment
- **Docker Compose:** Multi-service orchestration
- **Maven Frontend Plugin:** React integration
- **Lombok:** Code generation (ensure Java 21 compatibility)

### 📦 Maven Dependency Management

#### **Parent POM** (`/pom.xml`) - Central Dependency Management
```xml
<!-- Generic dependencies that ALL modules need -->
<dependencyManagement>
    <dependencies>
        <!-- Spring Boot BOM -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
        </dependency>
        
        <!-- Common dependencies -->
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>
        
        <!-- Testing dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**Parent POM Rules:**
- ✅ **Version management** for all dependencies (BOM imports)
- ✅ **Common dependencies** used across multiple modules
- ✅ **Plugin configuration** (compiler, surefire, JaCoCo)
- ✅ **Global properties** (Java version, encoding, etc.)
- ❌ **NO module-specific dependencies** - keep in respective modules

#### **Module-Specific Dependencies**

**modules/[module]/pom.xml** - Implementation Dependencies
```xml
<dependencies>
    <!-- JPA for entities -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Service layer interfaces -->
    <dependency>
        <groupId>com.cloudsuites.framework</groupId>
        <artifactId>amenity-service</artifactId>
    </dependency>
</dependencies>
```

**services/[service]/pom.xml** - Interface Dependencies
```xml
<dependencies>
    <!-- Only what's needed for interfaces and DTOs -->
    <dependency>
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
    </dependency>
</dependencies>
```

**contributions/core-webapp/pom.xml** - Web Dependencies
```xml
<dependencies>
    <!-- Web starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- All service modules -->
    <dependency>
        <groupId>com.cloudsuites.framework</groupId>
        <artifactId>amenity-service</artifactId>
    </dependency>
    
    <!-- All implementation modules -->
    <dependency>
        <groupId>com.cloudsuites.framework</groupId>
        <artifactId>amenity-module</artifactId>
    </dependency>
</dependencies>
```

### 🎯 Development Guidelines

#### **When Adding New Features:**

1. **Start with Service Interface** (`services/[domain]-service/`)
   ```java
   public interface AmenityBookingService {
       BookingDto createBooking(CreateBookingRequest request);
       List<BookingDto> getBookings(String buildingId);
   }
   ```

2. **Create/Update Entities** (`modules/[domain]-module/entity/`)
   ```java
   @Entity
   @Table(name = "amenity_bookings")
   public class AmenityBooking {
       @Id private String bookingId;
       // ... other fields
   }
   ```

3. **Add Repository** (`modules/[domain]-module/repository/`)
   ```java
   public interface AmenityBookingRepository extends JpaRepository<AmenityBooking, String> {
       List<AmenityBooking> findByBuildingId(String buildingId);
   }
   ```

4. **Implement Service** (`modules/[domain]-module/service/impl/`)
   ```java
   @Service
   @Transactional
   public class AmenityBookingServiceImpl implements AmenityBookingService {
       // Implementation here
   }
   ```

5. **Create Controller** (`contributions/core-webapp/rest/[domain]/`)
   ```java
   @RestController
   @RequestMapping("/api/v1/buildings/{buildingId}/amenities")
   public class AmenityController {
       // REST endpoints here
   }
   ```

#### **Dependency Addition Rules:**

**✅ Add to Parent POM when:**
- Used by 3+ modules
- Core framework dependency (Spring, validation, testing)
- Version management needed across modules

**✅ Add to Module POM when:**
- Module-specific functionality
- Implementation details (JPA, specific libraries)
- Only used by that module

**❌ Never add to wrong layer:**
- No JPA dependencies in service layer
- No web dependencies in modules layer
- No implementation details in parent POM

### 📋 Naming Conventions & Quality Standards

#### **Package Structure**
```
com.cloudsuites.framework.
├── webapp.[domain]                 # Controllers, web DTOs
├── services.[domain]               # Service interfaces, service DTOs  
└── modules.[domain]                # Entities, repositories, implementations
    ├── entity/                     # JPA entities
    ├── repository/                 # Data access interfaces
    └── service.impl/               # Service implementations
```

#### **Class Naming Standards**
- **Entities**: `Amenity.java`, `AmenityBooking.java` (singular nouns)
- **Repositories**: `AmenityRepository.java`, `AmenityBookingRepository.java`
- **Service Interfaces**: `AmenityService.java`, `AmenityBookingService.java`
- **Service Implementations**: `AmenityServiceImpl.java`, `AmenityBookingServiceImpl.java`
- **Controllers**: `AmenityController.java`, `PropertyController.java`
- **DTOs**: `AmenityDto.java`, `CreateAmenityRequest.java`, `AmenityResponse.java`

#### **Code Quality Requirements**
- ✅ **JavaDoc** on all public methods and classes
- ✅ **Validation annotations** on DTOs and entities (`@NotNull`, `@Size`, etc.)
- ✅ **Transactional boundaries** on service methods
- ✅ **Exception handling** with meaningful messages
- ✅ **Logging** with appropriate levels (debug, info, warn, error)
- ✅ **Unit tests** for all business logic
- ✅ **Integration tests** for controllers and repositories

#### **Entity Standards**
```java
@Entity
@Table(name = "amenity_bookings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AmenityBooking {
    @Id
    @Column(name = "booking_id")
    private String bookingId;  // UUID as String
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "last_modified_by")  
    private String lastModifiedBy;
    
    // Business fields...
}
```

#### **Service Implementation Standards**
```java
@Service
@Transactional
@Slf4j
public class AmenityServiceImpl implements AmenityService {
    
    private final AmenityRepository amenityRepository;
    private final ModelMapper modelMapper;
    
    public AmenityServiceImpl(AmenityRepository amenityRepository, ModelMapper modelMapper) {
        this.amenityRepository = amenityRepository;
        this.modelMapper = modelMapper;
    }
    
    @Override
    public AmenityDto createAmenity(CreateAmenityRequest request) {
        log.debug("Creating amenity for building: {}", request.getBuildingId());
        
        // Validation, business logic, persistence
        
        log.info("Successfully created amenity: {}", amenity.getAmenityId());
        return modelMapper.map(amenity, AmenityDto.class);
    }
}
```

#### **Controller Standards**
```java
@RestController
@RequestMapping("/api/v1/buildings/{buildingId}/amenities")
@Validated
@Slf4j
public class AmenityController {
    
    private final AmenityService amenityService;
    
    @PostMapping
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @Operation(summary = "Create amenity", description = "Creates a new amenity for the building")
    @ApiResponse(responseCode = "201", description = "Amenity created successfully")
    public ResponseEntity<AmenityDto> createAmenity(
            @PathVariable String buildingId,
            @Valid @RequestBody CreateAmenityRequest request) {
        
        AmenityDto amenity = amenityService.createAmenity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(amenity);
    }
}
```

### 🔄 Development Workflow

#### **Feature Development Process**
1. **Create Feature Branch**: `git checkout -b feat/amenity-booking-calendar`
2. **Design Service Interface**: Define contracts in `services/` layer
3. **Create/Update Entities**: Add JPA entities in `modules/` layer  
4. **Implement Repository**: Add data access in `modules/` layer
5. **Implement Service**: Add business logic in `modules/` layer
6. **Create Controller**: Add REST API in `webapp/` layer
7. **Add Tests**: Unit and integration tests
8. **Update Documentation**: README, API docs, comments
9. **Run Quality Checks**: Tests, security scan, code style
10. **Create Pull Request**: With proper description and reviews

#### **Code Review Checklist**
- [ ] ✅ **Architecture compliance** - correct layer placement
- [ ] ✅ **Dependency direction** - webapp → services → modules
- [ ] ✅ **Naming conventions** - follows established patterns
- [ ] ✅ **Quality standards** - tests, documentation, validation
- [ ] ✅ **Security considerations** - authorization, input validation
- [ ] ✅ **Performance impact** - N+1 queries, caching, indexing
- [ ] ✅ **Boy Scout rule applied** - improved existing code
- [ ] ✅ **Documentation updated** - README, comments, API docs

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
- **PostgreSQL 17** on port 59665 (primary database)
- **Redis 7** on port 6379 (rate limiting, caching, sessions)
- Environment variables loaded from `.env` file
- Flyway migrations run automatically on startup

### Redis Integration
**Rate Limiting Service** - Redis-backed with in-memory fallback:
- **Service Location**: `RedisRateLimitServiceImpl` in `modules/auth-module`
- **Configuration**: See `cloudsuites.rate-limiting` in `application.yml`
- **Docker Service**: `redis` container with password authentication
- **Health Check**: Redis connection validated on startup
- **Fallback**: Graceful degradation to in-memory when Redis unavailable

### Redis Configuration
```yaml
spring.data.redis:
  host: redis          # Docker service name
  port: 6379          # Standard Redis port
  database: 1         # Separate DB for rate limiting
  password: csRedisPass123
```

### Environment Variables (`.env`)
```bash
# Redis Connection
REDIS_HOST=redis
REDIS_PASSWORD=csRedisPass123

# Rate Limiting
RATE_LIMITING_ENABLED=true
RATE_LIMITING_REDIS_ENABLED=true
RATE_LIMIT_LOGIN_ATTEMPTS=5
```

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

### Redis-Specific Issues
**Redis Connection Failed**: 
- Check Redis container: `docker-compose logs redis`
- Verify password: `REDIS_PASSWORD=csRedisPass123`
- Test connection: `docker exec -it cloudsuites-redis redis-cli -a csRedisPass123 ping`

**Rate Limiting Not Working**:
- Check `RATE_LIMITING_ENABLED=true` in `.env`
- Verify Redis service health: `docker-compose ps redis`
- Monitor logs: `docker-compose logs cloudsuites-app | grep -i redis`
- Fallback active: Look for "using in-memory fallback" in logs

**Performance Issues**:
- Monitor Redis memory: `docker exec cloudsuites-redis redis-cli -a csRedisPass123 INFO memory`
- Check connection pool: Adjust `REDIS_POOL_MAX_ACTIVE` if needed
- Review rate limit windows: Shorter windows = better performance

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
