# Property Management Platform Onboarding & Authentication Implementation Plan

**Status:** Planned

This implementation plan defines the complete onboarding and authentication system for the CloudSuites property management platform, supporting 7 distinct user personas with OTP-first authentication, multi-factor security, and BuildingLink feature parity.

## 1. Requirements & Constraints

### Authentication Requirements

- **REQ-001**: OTP-first authentication for streamlined, passwordless user experience
- **REQ-002**: Support for SMS and email OTP delivery with 6-digit codes (5-minute expiry)
- **REQ-003**: JWT-based authentication with 15-minute access tokens and 30-day refresh tokens
- **REQ-004**: Password fallback option for all user personas except guests
- **REQ-005**: Multi-factor authentication enforcement for admin roles
- **REQ-006**: SSO integration for corporate users (staff, building admins, company admins)
- **REQ-007**: Biometric authentication support for mobile devices
- **REQ-008**: Device trust and "keep me logged in" functionality

### User Persona Requirements

- **REQ-009**: Tenant onboarding via building admin invitation with unit assignment
- **REQ-010**: Owner account creation with property ownership verification
- **REQ-011**: Board member role elevation with enhanced permissions
- **REQ-012**: Staff multi-building access with role-based scoping
- **REQ-013**: Building admin single/multi-property management capabilities
- **REQ-014**: Company admin portfolio-wide access and user management
- **REQ-015**: Super admin system-wide administration and impersonation

### Security Requirements

- **SEC-001**: Rate limiting on OTP requests (3 attempts per 5 minutes)
- **SEC-002**: Audit logging for all authentication events and admin actions
- **SEC-003**: Refresh token rotation on each use for enhanced security
- **SEC-004**: Role-based access control (RBAC) with JWT claims validation
- **SEC-005**: Account lockout after multiple failed attempts with admin unlock
- **SEC-006**: Secure password reset via email with temporary tokens
- **SEC-007**: Hardware token/authenticator app support for super admins

### Technical Constraints

- **CON-001**: Spring Boot 3.3.2 backend with Spring Security JWT implementation
- **CON-002**: PostgreSQL 17 database with UUID primary keys for all entities
- **CON-003**: React 18 frontend with TypeScript and shadcn/ui components
- **CON-004**: Maven multi-module architecture (modules/services/contributions)
- **CON-005**: Docker containerization with docker-compose development environment
- **CON-006**: Integration with existing CloudSuites API structure
- **CON-007**: Twilio integration for SMS OTP (external verify service)
- **CON-008**: Redis availability for caching and rate limiting (for scaling out)
- **CON-009**: Use of Auth0/Okta libraries for potential SSO integration (config present)

### Business Constraints

- **CON-010**: BuildingLink feature parity for competitive compatibility
- **CON-011**: Support for offline users via printable login letters
- **CON-012**: Customizable email templates by building/company administrators
- **CON-013**: Multi-property access for staff and administrative users
- **CON-014**: Guest and vendor temporary access without full account creation

### Performance Requirements

- **PERF-001**: OTP delivery within 30 seconds via SMS and email
- **PERF-002**: Authentication response time under 200ms for valid tokens
- **PERF-003**: Support for 10,000+ concurrent users across all personas
- **PERF-004**: Database connection pooling with HikariCP optimization

### Compliance Requirements

- **COMP-001**: GDPR compliance for user data handling and deletion
- **COMP-002**: SOC 2 Type II audit trail requirements
- **COMP-003**: Multi-tenant data isolation and security
- **COMP-004**: Accessibility compliance (WCAG 2.1 AA) for all authentication flows

## 2. Implementation Steps

### Implementation Phase 1: Core Authentication Infrastructure

**GOAL-001**: Establish foundational authentication system with OTP-first design and JWT token management.

This phase focuses on creating the core authentication framework that will support all user personas with modern security practices.

### Implementation Phase 2: User Persona Models and Services

**GOAL-002**: Create distinct user persona models with role-based access control and multi-property support.

This phase implements the entity models and service layers for all seven user personas with their specific business rules.

### Implementation Phase 3: Authentication Endpoints and Services

**GOAL-003**: Implement comprehensive authentication endpoints for all user personas with OTP and password support.

This phase creates the REST API endpoints that handle authentication flows for each persona type.

### Implementation Phase 4: Frontend Authentication Components

**GOAL-004**: Develop React-based authentication interface supporting all user personas and authentication methods.

This phase builds the user interface components for authentication flows with responsive design and accessibility.

### Implementation Phase 5: Advanced Features and Security

**GOAL-005**: Implement advanced security features, audit systems, and administrative tools.

This phase adds enterprise-grade security features including audit logging, session management, and administrative controls.

### Implementation Phase 6: Testing, Integration, and Documentation

**GOAL-006**: Comprehensive testing, API documentation, and deployment preparation.

This phase ensures system reliability through testing and prepares for production deployment.

## 3. Alternatives

### Alternative Approaches Considered

- **ALT-001**: **Password-first authentication with OTP as secondary factor** – *Rejected.* We decided to continue prioritizing an OTP-first approach (passwordless initial login) to maximize accessibility and user convenience. Traditional password-first flows were deemed less ideal for infrequent users and more prone to forgotten credentials, whereas OTP login provides a quick, on-demand authentication method.

- **ALT-002**: **Separate authentication microservice** – *Rejected for now.* While having a standalone auth microservice is considered an industry best practice for large-scale platforms (centralizing authentication concerns and enabling independent scaling), we chose a centralized auth module within the existing architecture for the initial implementation due to lower complexity and faster integration with current systems. We will follow design best practices (clear interfaces, decoupling) so that the authentication component could be extracted into a microservice in the future if needed for scalability or organizational reasons.

- **ALT-003**: **Third-party authentication provider (Auth0, AWS Cognito)** – *Rejected.* Using an external Auth provider was ruled out mainly due to cost implications and limitations in customization. The CloudSuites platform requires a high degree of customization (multi-persona flows, integration with internal modules) and owning the user data/control. Current infrastructure (Spring Security + JWT) is sufficient and more cost-effective given our scale.

- **ALT-004**: **Single unified user entity vs. persona-specific models** – *Rejected.* We considered simplifying the domain model by using one unified User entity for all personas (with flags or subtype fields). However, this was rejected in favor of distinct models per persona for clarity, maintainability, and to better mirror domain concepts. Separate entities (Tenant, Owner, Staff, etc.) make the permission and attribute differences explicit, at the cost of some additional complexity in management. The team agreed this clarity is worth the trade-off.

- **ALT-005**: **Magic link-only authentication (email links without codes)** – *Rejected.* While magic links (passwordless via email URL) are user-friendly, we had concerns about email deliverability and security (email-forwarding could inadvertently share a login link). OTP codes provide a uniform method that can be delivered via SMS or email and feel more secure/tangible to users. We may consider magic links as an option in the future for certain user groups, but not as the sole method.

- **ALT-006**: **Separate frontend applications per user persona** – *Rejected.* Maintaining distinct frontends for, say, tenants vs admins, was considered to reduce complexity in single-app conditional rendering. However, that approach introduces significant maintenance overhead and fragmentation of the user experience. Instead, we will build a single React application that dynamically adjusts to user roles, which is feasible given our component-based approach and context-driven UI.

- **ALT-007**: **Database-per-tenant architecture** – *Rejected for initial phase.* The idea of having separate databases for each client (or building) was considered for security and scalability. Currently, our scale doesn't necessitate this and a single database with proper tenant scoping is more manageable. However, as an enterprise-focused **future requirement**, this is acknowledged as a likely need. We will design the data layer in a way that can be partitioned by tenant if we scale to a point where isolation or data volume warrants it. (In other words, while we proceed with a multi-tenant shared DB now, we anticipate moving to a per-tenant database model for enterprise clients down the line as a must-do for scalability and data isolation.)

## 4. Dependencies

### External Dependencies

- **DEP-001**: **Twilio API** for SMS OTP delivery – Required for production OTP sending (the system currently uses Twilio's Verify service for SMS). We will abstract this so we can swap providers or add email channel easily.

- **DEP-002**: **SMTP Email Service** for email OTP and notifications – Critical for all email-based flows (password resets, welcome emails, etc.). We have a Mailhog for dev/testing but need a production-grade SMTP or email API (SendGrid, AWS SES) configured.

- **DEP-003**: **Redis** (or Twilio verify service) for session storage and rate limiting – Required for stateless JWT management at scale (storing refresh tokens or tracking OTP attempts). Redis cluster will be used for caching and distributing rate-limit counters.

- **DEP-004**: **SSL/TLS certificates** – All authentication endpoints must be served over HTTPS in production for security.

- **DEP-005**: **SSO Identity Provider Configurations** – For each corporate SSO integration, we need metadata (SAML certificates, OAuth client IDs/secrets) from customers' IdPs (Okta, Azure AD, etc.). This will be needed during implementation of the SSO features.

### Internal Dependencies

- **DEP-006**: **PostgreSQL 17 database** – Must have the necessary schema migrations applied (Flyway scripts for new auth tables). Proper indexing (on user identifiers, tokens, etc.) is needed for performance.

- **DEP-007**: **Dockerized Dev Environment** – The Docker Compose setup must include new components (if any) like Redis. Our docker-compose.yml already has Postgres, Redis, Mailhog configured for development.

- **DEP-008**: **Property and Building Management modules** – The onboarding flows depend on existing modules for buildings, units, and companies. E.g., inviting a tenant requires a valid buildingId/unitId, which comes from the Property module.

- **DEP-009**: **Notification service** – For sending out emails or push notifications (e.g., OTP via email, or security alerts). If a dedicated notification service exists, we should integrate with it; otherwise, extend it as needed for auth-related messages.

- **DEP-010**: **Existing CloudSuites API structure** – The new auth endpoints should conform to the current API versioning and error handling format. They will be added to the core-webapp contribution, aligning with how other modules expose APIs.

### Development Dependencies

- **DEP-011**: **Spring Boot 3.3.2 & Spring Security 6.x** – Our chosen framework version, which supports modern security features. Spring Security will be leveraged for JWT filters and method security.

- **DEP-012**: **React 18 + TypeScript** – The frontend tech stack, including the design system (shadcn/ui). All new UI components will use this stack.

- **DEP-013**: **Testing frameworks** – JUnit 5 for backend unit tests, Mockito for mocking, and for front-end, Jest and React Testing Library for unit tests, plus Cypress for end-to-end tests. These need to be configured and possibly some libraries added (e.g., testing-library/jest-dom).

- **DEP-014**: **API documentation tools** – We use Springdoc OpenAPI for generating docs. Ensure annotations are in place in controllers and models so that CloudSuites API Documentation reflects the new endpoints.

- **DEP-015**: **Security scanning tools** – We should run code analysis (SpotBugs, SonarQube) and dependency vulnerability scans (OWASP Dependency Check, npm audit for frontend) as part of the build pipeline, especially for the auth components.

## 5. Files

### Backend Module Structure

- **FILE-001**: `modules/auth-module/` – New Authentication domain module following CloudSuites multi-module pattern (will contain entities and repository interfaces for auth, e.g., OTP code, session, audit).

- **FILE-002**: `services/auth-service/` – New Authentication service layer module (business logic for login, OTP verification, token issuance).

- **FILE-003**: `contributions/core-webapp/src/main/java/com/cloudsuites/framework/webapp/auth/` – REST API controllers for authentication flows. This includes:
  - `AuthController.java` (if a universal login controller is introduced) and/or specific controllers:
  - `TenantAuthController.java`, `OwnerAuthController.java`, `StaffAuthController.java`, `AdminAuthController.java` – existing persona-specific controllers which may be refactored or extended.
  - `UserRegistrationController.java` – might handle the shared parts of registration flows.
  - `PasswordController.java` – to manage password reset and change if implemented.
  - `SsoController.java` – to handle SSO callbacks.
  - `BiometricController.java` – for biometric registration/verification.

### Database Migration Files

- **FILE-004**: `src/main/resources/db/migration/` – Flyway migration scripts for new auth tables:
  - `V2__create_auth_otp_tables.sql` – Create table for OTP codes (if storing codes or Twilio verification status), and possibly a table for OTP send attempts for rate limiting.
  - `V3__create_auth_session_tables.sql` – Create table for refresh tokens or sessions (if we implement persistent sessions for refresh/MFA).
  - `V4__create_auth_audit_table.sql` – Create table for audit_events.
  - `V5__alter_user_tables_for_auth.sql` – Any alterations to user tables (e.g., adding password hash column, mfa enabled flag, etc.).

### Existing Identity Modules

- **FILE-005**: The existing identity and user modules:
  - `modules/identity-module/src/main/java/com/cloudsuites/framework/modules/jwt/JwtTokenProvider.java` – Existing JWT token generation logic (which uses a secret key and HS256 currently).
  - `modules/identity-module/src/main/java/com/cloudsuites/framework/modules/otp/TwilioOtpService.java` – Existing implementation for OTP sending via Twilio.
  - `modules/identity-module/src/main/java/com/cloudsuites/framework/modules/user/` – Contains BaseUser or identity entities and repositories.
  
  These files will be referenced or extended when building the new auth module (e.g., some code might move to the auth module).

### Email Templates

- **FILE-006**: `contributions/core-webapp/src/main/resources/templates/` – Email templates:
  - `welcome-email.html` – Template for new user welcome/invitation email.
  - `otp-email.html` – Template for sending OTP via email (to implement).
  - `password-reset.html` – Template for password reset instructions.
  - `login-letter.html` – Template for generating printable login letters (to implement).
  - `account-locked.html` – Template to notify user of account lockout.

  These will be filled with proper content and placeholders, and possibly made editable by admins.

### Frontend Components

- **FILE-007**: Frontend source files for auth (under `contributions/property-management-web/`):
  - `src/components/auth/LoginPage.tsx` – The login page component (to be created/updated for OTP-first).
  - `src/components/auth/OtpVerification.tsx` – OTP input component.
  - `src/components/auth/PasswordReset.tsx` – Password reset flow components.
  - `src/components/auth/FirstTimeSetup.tsx` – New user onboarding wizard.
  - `src/components/auth/BiometricSetup.tsx` – Flow for enabling biometrics.
  - `src/components/auth/ContextSwitcher.tsx` – For switching building context in UI.
  - `src/components/auth/AdminInvite.tsx` – UI for admins to invite/register new users.

### Frontend State Management

- **FILE-008**: Frontend state management and services:
  - `src/hooks/useAuth.ts` – React hook or context provider for authentication state (login/logout, token refresh, etc.).
  - `src/services/authService.ts` – Wrapper for calling auth API endpoints (login, OTP verify, refresh, etc.).

  Ensure this is updated to use the unified login endpoint and handle the multiple persona cases gracefully.

### Configuration Files

- **FILE-009**: Configurations:
  - `application.yml` / `application-*.yml` – Spring Boot config files where new properties will be added:
    - JWT settings (key paths or algorithm config if switching to RSA, token expiry times).
    - Twilio and SMTP settings for OTP (currently only Twilio SMS is present, we will add SMTP config).
    - Rate limit settings, lockout thresholds, etc.
  - `docker-compose.yml` – Updated if needed to include any new service (though likely just ensure Redis is running).
  - `.env` – Make sure to add any new required environment variables (e.g., SMTP credentials, JWT keys if not using default secret).

## 6. Testing

### Testing Strategy

- **TEST-001**: **Unit Testing** – We will write unit tests for all new service classes:
  - OTP service tests: simulate OTP generation, Twilio API call (mocked), OTP verification success/failure scenarios, and rate limiting logic.
  - JWT service tests: token generation and parsing, ensuring claims are correctly set and token expiration is handled.
  - Password service tests: password hashing and validation, including breach check (which might be stubbed if it calls external API).
  - Controllers tests: use Spring Boot test slicing to ensure each auth endpoint returns correct HTTP codes and responses for various scenarios (valid OTP, invalid OTP, locked account, etc.). We expect a high coverage on the auth module given its critical nature.

- **TEST-002**: **Integration Testing** – Using Spring Boot's test context or Postman/Newman collections to simulate flows:
  - Complete signup flow for each persona: e.g., register tenant → request OTP → verify OTP → get JWT → refresh token.
  - Password reset flow: ensure that a reset token actually allows a password change and invalid/expired tokens are handled.
  - Multi-factor auth flow: for an admin with MFA enabled, simulate login with OTP + additional factor.
  
  These tests ensure that all components (DB, services, controllers) work together as expected.

- **TEST-003**: **End-to-End Testing** – Use a tool like Cypress for front-end E2E:
  - Simulate a user using the web app: e.g., admin invites a tenant, tenant receives OTP (we can intercept or stub SMS), tenant logs in, browses some protected page.
  - Test various personas logging in and switching context (for multi-building staff).
  - Ensure UI shows appropriate content per role (e.g., tenant cannot see admin dashboard).
  
  We'll create scenarios for each persona and important flows. These tests will run against a test instance of the full application (with possibly some test doubles for external services like Twilio).

- **TEST-004**: **Security Testing** – We will perform dedicated security tests:
  - Brute-force OTP: attempt more than allowed OTP guesses and ensure the system locks or rejects further attempts (testing SEC-001 and SEC-005).
  - JWT validation: try tampering with JWT to ensure it's rejected (test our signing algorithm and secret management).
  - Access control: verify that role-based restrictions work (e.g., a tenant token cannot access admin endpoints, etc.).
  - Vulnerability scanning: run OWASP ZAP or similar against the dev server to catch any obvious issues (e.g., missing security headers, injection possibilities).
  - If possible, invite an external pen-test or use automated tools to catch things we might miss.

- **TEST-005**: **Performance Testing** – We will script load tests focusing on the auth endpoints:
  - High volume OTP requests (to ensure Twilio integration and any caching layer can handle bursts, and that rate limiting effectively throttles abuse).
  - Sustained login attempts and token refresh under load (e.g., simulate 10k concurrent users refreshing tokens every 15 minutes).
  - Monitor resource usage (CPU, memory, DB connections) during these tests and ensure response times meet PERF-002 even under load. Optimize query performance or add caching as needed based on results.

All testing will follow CloudSuites' existing testing patterns and best practices, ensuring that new code does not regress existing functionality (run the full test suite for the platform regularly).

## 7. Risks & Assumptions

### Technical Risks

- **RISK-001**: **SMS delivery reliability** – If Twilio SMS experiences downtime or delays, OTP login could be impacted.
  **Mitigation:** Integrate a backup SMS provider or channel (email as backup if SMS fails). Also implement proper error handling and user messaging when OTP cannot be delivered promptly.

- **RISK-002**: **Email deliverability** – OTP or reset emails might go to spam or get blocked.
  **Mitigation:** Use verified sender domains, set up SPF/DKIM/DMARC for cloudsuites.com emails, and possibly allow SMS as a backup for email OTP. Monitor bounce rates and spam complaints especially after rollout of email OTP.

- **RISK-003**: **JWT security vulnerabilities** – Using JWT introduces risks (stolen tokens, XSS leading to token theft, etc.).
  **Mitigation:** We will use robust signing (moving to RSA256 with a private key) and short access token life. Also implement token revocation via rotation (SEC-003) and store tokens securely (HttpOnly cookies for web, secure storage in mobile). Regularly update libraries to pick up security fixes.

- **RISK-004**: **Database performance** – The addition of new tables (audit logs, sessions, OTPs) and high-frequency writes (each login attempt) could impact DB performance.
  **Mitigation:** Ensure indexes on critical columns (e.g., userId in audit_events), consider partitioning large log tables by date, and offload some ephemeral data to Redis. Connection pooling (using HikariCP, already in place) will be tuned for the expected load.

- **RISK-005**: **Race conditions in multi-property context switching** – If a user rapidly switches contexts or if there are concurrent requests on different contexts, we might see inconsistent behavior.
  **Mitigation:** Clearly define context switch operations to be atomic (maybe through backend only allowing one active context per token), or use separate tokens per context. Implement proper locking or state management in UserContextService if needed.

### Security Risks

- **RISK-006**: **Account takeover via OTP interception** – An attacker could intercept OTPs (if they have access to someone's SMS or email).
  **Mitigation:** Provide an option for users to enable a second factor (e.g., an authenticator app or security question) especially for sensitive roles. Also, implement device recognition: if a new device is used, require re-verification or notify the user. Rate limiting (SEC-001) and short OTP expiry reduce the window of attack.

- **RISK-007**: **Privilege escalation** – Bugs in role checking could allow a user to perform actions beyond their role (e.g., a tenant accessing admin API).
  **Mitigation:** Extensive testing of RBAC (TEST-004) and use of a single source of truth for user roles (the RoleHierarchyService and Spring Security's context). Also implement defense in depth: check permissions at both the API gateway (annotations) and in the business logic.

- **RISK-008**: **Session hijacking & token theft** – If JWT tokens are stolen (via XSS or device theft), an attacker can impersonate a user.
  **Mitigation:** Use HttpOnly cookies for web so scripts can't access tokens. Encourage use of biometric/Pin on mobile to secure stored tokens. Implement an option for users to review and revoke active sessions/devices (like a "logout from all devices" feature). Rotate refresh tokens on use and possibly invalidate refresh tokens on suspicious activity.

- **RISK-009**: **Social engineering of support/admin** – Attackers might trick support or admins into resetting passwords or providing OTPs.
  **Mitigation:** Train support staff on verification processes. For admin-initiated resets, enforce policies like contacting the user or requiring multiple admin approvals for role changes (SEC-007 for super admins requiring hardware token for critical actions).

- **RISK-010**: **Insider threats (Super admin misuse)** – A super admin has broad powers that could be misused.
  **Mitigation:** As planned, every impersonation or sensitive action is logged (SEC-002 audit trail). Additionally, consider requiring at least two super admins to approve certain actions (e.g., deleting a company, etc.), though this may be beyond auth scope. Regularly review the audit logs for unusual super admin activity (this could be part of SOC2 controls).

### Business Risks

- **RISK-011**: **User adoption challenges with OTP-first** – Some users (especially less tech-savvy) might find OTP login confusing or burdensome, or not have reliable cell service.
  **Mitigation:** Provide clear instructions and support. The fallback to password (REQ-004) is there for those who prefer a set password. Also, provide offline methods like login letters (CON-011) for initial onboarding. Over time, if many request a password, we can encourage OTP by highlighting convenience and security (no password to remember).

- **RISK-012**: **Integration complexity** – Merging this new auth system with the existing CloudSuites system is complex. There might be unforeseen issues with linking the new user records with existing data (e.g., ensuring that tenant IDs in the property module match identity IDs in the auth module).
  **Mitigation:** Use staging environments to test integration thoroughly. Possibly roll out in beta with a subset of users. Also, maintain backward compatibility where needed (for instance, if existing mobile apps expect the old auth behavior).

- **RISK-013**: **Compliance audit failures** – If our implementation doesn't meet GDPR or SOC2 requirements (COMP-001, COMP-002), it could lead to penalties or lost deals.
  **Mitigation:** Engage compliance experts early to review data handling (e.g., ensure we have a way to delete user data on request, log access to personal data, etc.). Document everything (for auditors) and consider building admin tools for data export/deletion.

- **RISK-014**: **Scalability issues at peak usage** – If a large property management company with thousands of users onboard at once, OTP sending or authentication could become a bottleneck.
  **Mitigation:** The design is already stateless and horizontally scalable. We can scale out the authentication service instances and have multiple SMS providers if needed. Load testing (TEST-005) will guide any capacity planning. Also consider using Twilio's bulk APIs or increasing throughput limits ahead of big launches.

- **RISK-015**: **Cost overruns from third-party services** – Heavy use of SMS (Twilio) and emails can incur significant costs as user base grows.
  **Mitigation:** Monitor usage and costs closely. Negotiate better rates with Twilio or consider alternative providers for SMS. Encourage use of email or authenticator apps for frequent users to reduce SMS sends. Also, caching and rate limiting will reduce waste (e.g., prevent spamming OTP).

### Operational Assumptions

- **ASSUMPTION-001**: Users will have access to at least one of email or mobile phone. Since OTP is primary, we assume no user is completely without these. For edge cases (no cell phone), email OTP can be used, and vice versa.

- **ASSUMPTION-002**: Corporate customers (with many staff) will want SSO and will provide necessary IdP details within ~6 months of us delivering the feature. We assume a moderate adoption of SSO; the system will still fully support non-SSO logins for those who don't set it up.

- **ASSUMPTION-003**: Building administrators will manage inviting tenants/owners diligently (the platform doesn't allow self-signup for tenants without an invite). We assume they will use the provided tools to keep resident data up-to-date.

- **ASSUMPTION-004**: Third-party services (Twilio, Email SMTP, etc.) will maintain high uptime (99.9%) and any outages will be rare. We have mitigation plans for short outages (like queueing OTPs or failing over to email), but a long outage might effectively lock users out until resolved.

- **ASSUMPTION-005**: Current multi-tenant single-DB architecture will suffice for at least the next 1-2 years of growth. By the time we onboard significantly larger clients (e.g., property management companies managing hundreds of buildings), we will have prepared the system (and possibly the architecture ALT-007) to handle that scale.

### Technical Assumptions

- **ASSUMPTION-006**: Spring Security 6.x JWT support will cover our needs for verifying tokens and we won't hit major bugs. (Our plan to implement our own JwtTokenService is mostly for generating tokens; we will still rely on Spring Security for parsing and authentication filters.)

- **ASSUMPTION-007**: The React frontend can handle complex state (multiple contexts, role-specific views) without significant performance issues. Using context and hooks (like useAuth) is assumed to be sufficient; we do not foresee needing a state management library like Redux given limited global state.

- **ASSUMPTION-008**: Docker containerization will reflect a production-like environment sufficiently. We assume no major discrepancies between dev/test environment and production (for example, networking, config) that would cause unexpected behavior in auth flows.

- **ASSUMPTION-009**: Redis will be available and reliable for caching and temporary data. If Redis goes down, features like rate limiting and perhaps session tracking might be affected. We assume we'll run Redis in a highly available mode or can tolerate a brief downtime (worst case, rate limits aren't enforced for a short window).

- **ASSUMPTION-010**: The existing CloudSuites API and data model can accommodate these changes without major refactoring. For example, adding a password field to users, or linking tenant records to identity records, can be done with additive changes (new columns, foreign keys) rather than redesigning core schemas.

## 8. Implementation References

### Database Schema

We will create new Flyway migration scripts (as listed in FILE-004) to introduce authentication-specific tables. These will be designed to link with existing tables where appropriate (e.g., tenant_id in an OTP table to reference the tenants table for auditing).

The current schema includes tables for tenants, owners, staff, etc., but no tables for OTP codes or sessions. Our migrations will add those. We will ensure backward compatibility (existing tables remain unchanged except for adding columns or indexes as needed).

### API Contracts

All new endpoints will use RESTful patterns and follow CloudSuites conventions (plural nouns, versioned under `/api/v1/auth/`). We will utilize HTTP response codes appropriately (200 for OK, 201 for created, 400 for validation errors, 401 for unauthorized, 409 for conflicts, etc.).

OpenAPI documentation will be updated so that CloudSuites Framework API Documentation reflects these endpoints in the "Authentication" sections. For instance, after implementation, the API docs will list the unified login endpoint and any new paths for password reset, etc.

### Frontend Components

The front-end will follow existing project structure. We will place new components under `src/components/auth` (FILE-007) and use existing UI library components for consistency.

The design for OTP inputs, modal dialogs (for Terms & Conditions, etc.) will follow the style guide provided by shadcn/UI.

We'll also integrate with the global state or context (e.g., update useAuth hook FILE-008 and possibly a global context for user profile and active building).

Any text or labels will consider i18n (if the app is localized) – ensure new strings can be translated.

### Security Configuration

The Spring Security configuration (FILE-009 snippet shown for SecurityConfiguration.java) will be enhanced. The current config in application.yml indicates JWT is being used with a symmetric secret and an Auth0 issuer for validation. We will likely remove Auth0 as an issuer (since we're not using Auth0 as the primary auth now) and instead configure our own JWT signing keys.

We will also set up CORS properly (currently allowed-origins is localhost for dev; in production, it should allow the actual domain).

After changes, the security config will have entries for our new endpoints (most will be public like `/auth/*` except those under `/auth/admin` or `/auth/super` which require roles). It will also incorporate a filter for rate limiting if we implement that at the filter level.

## 9. Related Specifications / Further Reading

### Internal Documentation

- **CloudSuites API Documentation** – The current OpenAPI spec for CloudSuites endpoints, which provides insight into existing auth endpoints and data models. (This was used to identify current capabilities and gaps).

- **CloudSuites GitHub Copilot Instructions** – Development guidelines that ensure consistency (e.g., coding styles, branching strategy).

- **Database Schema Documentation** – Documentation of current database structure. We will update this as we add new tables.

- **Multi-Module Architecture Guide** – Overview of how modules, services, and contributions interact in CloudSuites, useful for placing our new code correctly.

### External References

- **BuildingLink Staff Help** – BuildingLink's feature documentation, used for ensuring our feature set is at parity or better. Particularly useful to understand what onboarding and login features property management staff expect.

- **JWT Best Practices (RFC7519)** – Official JWT standards. We adhere to these for claims and security (no sensitive data in payload, proper signing).

- **OWASP Authentication Cheat Sheet** – Security best practices for implementing authentication (we referenced this for decisions on OTP length, password policies, account lockout, etc.).

- **Spring Security Reference** – Documentation for implementing JWT, method security, and customizing authentication in Spring (our primary backend framework).

- **React Context Docs** – How we manage global auth state in the React app, ensuring that components respond to login/logout events properly.

- **GDPR Consent Requirements** – Guidelines to ensure our onboarding (which collects user contact info) and account deletion flows comply with GDPR.

- **SOC 2 Framework** – We align our auditing and logging to meet SOC 2 Type II criteria (especially security and access control monitoring).

- **WCAG 2.1 AA Checklist** – We will ensure the new UIs (like OTP input, error messages) meet accessibility standards (e.g., screen-reader labels, color contrast).

## 🔍 CloudSuites Backend Implementation Gap Analysis

*Based on a comprehensive codebase review conducted on September 10, 2025, we identified the following in the existing CloudSuites platform relative to the requirements above:*

### What's Already Implemented

#### Core Infrastructure (STRONG FOUNDATION)

- **Spring Boot 3.3.2** is in use with a robust security configuration. The project has a clear multi-module Maven structure and a core-webapp that handles the web layer.

- **JWT Token Provider** exists (`JwtTokenProvider.java`) which currently uses an HMAC SHA-256 **secret key** approach for signing tokens. Token generation and parsing logic are present, providing a basis to extend (though using symmetric keys).

- **Multi-Module Architecture:** The backend is already split into modules (e.g., identity-module, user-module, property-module) and services, indicating a clean separation of concerns. This will ease integration of a new auth module.

- **PostgreSQL 17 + Flyway**: The database is up and running with migrations through at least version 6 applied. Core tables for users (tenants, owners, staff, admins) and properties exist with proper relationships and constraints.

- **Dockerized Environment:** A Docker Compose setup is present with PostgreSQL, Redis, and Mailhog (for email testing) services defined. This provides a ready sandbox for adding new services or trying out changes in an isolated way.

#### User Management & Personas (WELL DEVELOPED)

- **Identity Entities:** Separate JPA entities for Tenant, Owner, Staff, Admin (and underlying base Identity) are already defined. They each have repositories and service layers (e.g., `UserServiceImpl.java` for common user operations).

- **Role-Based Access:** A role hierarchy system is partially implemented. For example, there are management endpoints for roles (Tenant Roles, Staff Roles, Owner Roles in the API docs) and a `UserRole` entity in the identity module. Spring Security is configured with roles like ROLE_TENANT, ROLE_ADMIN, etc.

- **User Role Management:** The platform supports assigning and updating roles for users via API (e.g., `/api/v1/tenants/{tenantId}/roles` with GET/PUT/DELETE), indicating that RBAC groundwork is laid.

- **Multi-Tenancy in Data:** All user entities carry references to building or company contexts (e.g., Tenant has building/unit, Staff has company/building). This confirms the system was built with multi-property management in mind, aligning with our context-switching requirement.

#### Authentication Foundation (SOLID CORE)

- **Spring Security & JWT Filter:** The current security setup includes JWT validation for protected endpoints. The application is already using JWT as evidenced by the need for a refresh token mechanism in the API and config files.

- **Custom Auth Controllers:** There are existing controllers for authentication per persona (found in core-webapp/authentication/: `TenantAuthController`, `OwnerAuthController`, `StaffAuthController`, `AdminAuthController`). These provide endpoints like *register*, *request-otp*, *verify-otp*, and *refresh-token* for each user type as seen in the API documentation.

- **OTP via Twilio (Basic):** An OTP service interface exists (`OtpService`) with a Twilio implementation. The code and config show Twilio Verify is integrated (with accountSid, serviceSid, etc.) to send OTPs via SMS. The API endpoints for OTP request and verification are operational for phone-based OTP across tenants, owners, staff, admins.

- **Refresh Tokens:** The API provides refresh-token endpoints for each persona. This means the concept of refresh tokens is present (likely as JWTs with longer expiry). However, it appears to be a basic implementation (passing refresh token as query param and getting a new JWT) with no rotation or revocation list currently in place.

- **Basic Audit Logging:** All entities have audit fields (`created_at`, `updated_at`, etc.), and some auditing via Spring Data is enabled. Also, the application exposes an Actuator endpoint for `auditevents`, hinting that Spring Security's default auth event logging might be partially enabled. But a dedicated audit trail for authentication events is not yet implemented.

#### OTP Integration (BASIC IMPLEMENTATION)

- **Twilio SMS OTP**: The platform sends OTPs to user phones via Twilio. The TwilioOtpService uses Twilio's API to trigger a verification code SMS. This covers OTP delivery for the primary channel (SMS).

- **OTP Request/Verify Endpoints:** For example, a tenant can be invited (registered) and then OTP is requested via `/api/v1/auth/.../tenants/{tenantId}/request-otp` and verified via `/.../verify-otp` as shown in the API docs. Similar endpoints exist for staff, owners, and admins. These return HTTP 200 and presumably a JWT in the response body upon successful verification.

- **Basic Rate Limiting (Twilio)**: Twilio's Verify service inherently limits OTP attempts (usually 5 attempts by default on the same code). However, on the application side, no additional logic is implemented to prevent spamming the request-otp endpoint aside from perhaps client-side measures.

#### Database Schema (COMPREHENSIVE CORE)

- **Core Tables Exist:** Tables for companies, buildings, units, tenants, owners, staff, admins, etc., exist with proper foreign keys. The schema enforces unique constraints on emails and phone numbers at least within each persona type (from code review, the Identity model has uniqueness on contact info).

- **Relationships:** The data model links tenants/owners to units and buildings, staff to companies, etc., which supports the context-aware authentication flows (like requiring buildingId/unitId in tenant auth endpoints).

- **Audit Fields:** The schema includes audit columns in each table for created/updated timestamps and created_by/updated_by (though the latter might not be filled yet for all operations).

- **Existing Migrations:** Migrations up to V6 cover initial schema and perhaps some incremental additions (amenities, etc.). This indicates that adding new tables (OTP, sessions, etc.) as V7+ is a straightforward extension.

### ⚠️ Critical Gaps Requiring Implementation

Despite the solid base, several critical features needed by the requirements are **absent or incomplete** in the current implementation:

#### Authentication Infrastructure (HIGH PRIORITY)

- **OTP Persistence & Multi-Channel:** There is no **OTP storage** on our side. Currently, OTP codes are managed externally by Twilio (we send and verify via Twilio's API, meaning the backend doesn't store the code). We lack an otp_codes table or similar, which we may need for email OTP or our own rate limiting logic. Implementing a secure store for OTP (or at least logging attempts) is necessary to support email channel and to not rely entirely on Twilio.

- **Session Management / Refresh Token Store:** The system does not maintain a server-side session store or refresh token database. Refresh tokens are JWTs with long expiry, and there is no mechanism to revoke them individually. This means if a refresh token is stolen, it could be used until it expires. We need a user_sessions table or token blacklist/whitelist to support **refresh token rotation** (SEC-003) and logout from all devices.

- **Audit Event Logging:** Aside from basic entity audit fields, there's no dedicated **audit log** for security events. Failed logins, OTP verifications, password changes, etc., are not being recorded in a centralized way. Without an audit_events table or log management, it's impossible to meet SEC-002 and SOC2 requirements. This is a significant gap for security oversight.

- **Rate Limiting Controls:** No application-level rate limiting is implemented for OTP or login attempts. While Twilio might throttle OTP sends and there is a MAX_LOGIN_ATTEMPTS config in `.env`, there is no code backing it as of now. We must implement rate limiting (e.g., via Redis or in-memory for a single instance) to prevent abuse (SEC-001).

- **Multi-Factor Authentication:** The current system is essentially single-factor (just OTP or password alone). There is no built-in support for requiring a second factor for admins or super admins (SEC-005, SEC-007). For compliance and high-security roles, this is a critical missing piece.

- **Device Trust:** No mechanism exists to remember trusted devices or provide "keep me logged in" functionality (REQ-008). Every login currently would require OTP or password, which can be cumbersome. Implementing device fingerprints and long-lived refresh tokens tied to devices is needed.

#### Security Features (HIGH PRIORITY)

- **Password Management:** The platform currently has *no password login or reset functionality* exposed. There's no UI or endpoint for "forgot password" or "change password," and likely no password hash stored (except possibly for legacy reasons). We need to introduce password support (REQ-004) for users who prefer it or as a backup method. This includes storing hashed passwords, enforcing complexity, and building reset flows (SEC-006).

- **Account Lockout:** There is no logic to lock an account after multiple failed attempts. Given the high risk of OTP or password guessing attacks, not having lockout (SEC-005) is a security hole. We see config for max attempts, but it's not active. This needs to be implemented to deter brute force attacks.

- **Biometric Auth:** There's no support for biometric login (fingerprint/FaceID) yet. While not trivial to implement, modern mobile usage would benefit from this (REQ-007). We plan for it, but currently nothing exists for it in the backend or app.

- **IP Whitelisting / Geo Restrictions:** Particularly for super admins or sensitive accounts, the system doesn't support restricting access by IP or sending alerts on new IP logins. This is an advanced feature that is currently missing (some references to ipWhitelist in SuperAdmin assumptions, but no implementation).

- **Backup Codes & 2FA Apps:** No support for authenticator apps (like Google Authenticator) or backup codes exists. This leaves OTP via SMS/email as the only factor, which may not be enough for high-security needs. Implementing TOTP (Time-based OTP) or similar is a gap to fill for SEC-007.

#### API Endpoints (MEDIUM PRIORITY)

- **Unified Login Endpoint:** There is no single endpoint that clients can hit with a username/phone/email and password/OTP and get logged in. Instead, the client has to know which type of user they are and call the corresponding OTP endpoint. This is cumbersome for the frontend. We need an `/api/v1/auth/login` that abstracts over tenant/owner/staff/admin and handles identification. Its absence complicates the frontend logic and is a gap in usability.

- **Password Reset & Change APIs:** As noted, there are no `/forgot-password` or `/reset-password` endpoints. If a user forgets their password (for those who have one), there's no way to recover other than an admin manually intervening. This is a standard feature we need to add.

- **SSO Callback Endpoints:** While the config hints at Auth0/Okta integration, there are no API endpoints to handle SSO logins (e.g., no `/auth/sso/callback` in the docs). This means SSO isn't actually usable yet. We will have to create the necessary endpoints for SAML or OAuth2 logins for corporate users.

- **Admin Tools:** There are no endpoints for impersonation or for super admins to manage accounts (unlock, force logout, etc.). Given our requirements for super admin functions, the lack of these endpoints is a gap. For example, no `PUT /auth/super/unlock-account/{id}` exists yet.

- **Guest Access:** There is no facility in the API for generating guest or vendor access links or tokens. Currently, every user must be a Tenant/Owner/Staff etc. to log in. The requirement for temporary access (CON-011) will require new endpoints and logic, currently non-existent.

#### Email/SMS Infrastructure (MEDIUM PRIORITY)

- **Email OTP Delivery:** The current OTP implementation is SMS-only. There is no code to send OTP via email. Many users (especially in certain regions or those who prefer not to use their phone) might want an email option. We need to integrate an email sending capability for OTP (perhaps via an SMTP server or email API). The absence of email OTP could limit adoption or exclude users without cell phones.

- **Email Templates & Customization:** The system lacks HTML templates for emails like OTP code, welcome emails, password reset, etc. Templates like `otp-email.html` or `welcome-email.html` need to be created. Moreover, allowing customization by admins (CON-009) will require storing templates or template variables in the DB, which isn't in place.

- **SMTP Configuration:** In application.yml and environment, we don't see configured SMTP settings (the Mailhog is for dev testing only). We need to set up proper SMTP configurations for production (or integrate with a service like SendGrid). Without this, any email functionality (OTP, invites, alerts) is not actually deliverable in a real environment.

- **Multi-Provider SMS**: Currently Twilio is the only SMS provider integrated. If Twilio fails or if we need region-specific SMS, there's no backup. While not immediate, this is a gap for robustness.

### What Needs Modification/Enhancement

Several existing components are present but need improvements to meet our goals:

#### Existing OTP Service (ENHANCEMENT REQUIRED)

- *Current State:* OtpService with TwilioOtpService covers basic SMS sending and verification via Twilio's external system.

- *Needed:* Extend this service to support **email OTP sending** (perhaps create an EmailOtpService that uses JavaMail or similar). Introduce in-memory or Redis caching of OTP codes if we move away from Twilio verify for email (since Twilio won't handle email OTP). Also, incorporate **rate limiting** in this service – e.g., track how many OTPs sent to a number/email in the last X minutes (SEC-001). Essentially, the OTP service should become multi-channel and smarter about security.

- *Redis Integration:* To facilitate rate limiting and OTP storage, integrate Redis (which is already available in our dev environment). Use it to store OTP attempts or even OTP codes for quick verification lookups if not using Twilio's verify for a channel.

- *Expiration & Resend:* Ensure the OTP codes expire after 5 minutes and that resends issue a new code only after a delay. This logic will be added to the service.

#### JWT Token Provider (ENHANCEMENT REQUIRED)

- *Current State:* Using a single secret key (HMAC). Token expiration is set to 1 day for access and 7 days for refresh in config, which is longer than desired. No custom claims beyond default (likely just username and roles).

- *Needed:* Migrate to **RSA256** signing for JWTs. This means generating a key pair and storing the private key securely (and possibly publishing the public key or using JWKS endpoint for future microservice validation). This change improves security (as the secret key isn't shared for signing and verification).

- *Token Structure:* Add the required claims: userId (so that we don't rely on parsing subject as string IDs in different formats), an array of roles, and maybe current building context. Possibly include a claim for "persona" or "userType" (tenant/owner etc.) to quickly identify in services.

- *Shorter Expiry:* Align token life with REQ-003: 15 minutes for access tokens. Refresh tokens 30 days (or less if security requires). The refresh endpoint logic must then be updated to issue new tokens with these new settings.

- *Rotation:* Implement refresh token rotation. Each time a refresh token is used, invalidate the old one (which means we need to store a token identifier or have a strategy to track used tokens). If using JWT for refresh, maybe switch to opaque tokens or maintain a hash in DB to revoke.

- *Revoke on Logout:* Provide a mechanism to invalidate a refresh token when a user logs out manually (could be done by keeping a blacklist of token IDs or a timestamp of revocation in the user record).

#### Authentication Controllers (RESTRUCTURING NEEDED)

- *Current State:* There are four separate controllers for different user types, which duplicate a lot of logic (request OTP, verify OTP, refresh token are conceptually similar across types, differing mainly in path and underlying service call).

- *Needed:* Introduce a **unified login controller** (`AuthController`) that handles the initial login request for any user. This can accept a neutral credential (email or phone, plus possibly password or OTP code) and then internally route to the correct service. This simplifies the client interaction.

- *Registration Process:* The existing separated registration endpoints can remain but should possibly delegate to a common user registration service under the hood. We might keep them separate since the inputs differ (tenant registration requires building and unit).

- *Password Endpoints:* Add a PasswordController (or integrate into AuthController) to handle forgot/reset/change password flows. Right now, there's no such controller.

- *MFA and Others:* Potentially add endpoints for second-factor verification if we implement MFA (e.g., `/auth/mfa/verify` after initial login).

- *Error Handling:* Ensure all controllers return consistent error responses. If currently each one has its own error handling, factor that into BaseAuthController so things like "OTP expired" or "Invalid credentials" produce uniform responses.

#### Security Configuration (ENHANCEMENT REQUIRED)

- *Current State:* Basic JWT filter is in place; method security is enabled. But certain aspects are not configured:
  - CORS is only allowing localhost (dev), need to update for production domains.
  - No global exception handler for Spring Security – currently, an unauthorized might return a default HTML/error. We should configure an AuthenticationEntryPoint to return JSON errors.
  - Rate limiting filter not present.

- *Needed:* Update SecurityConfiguration to include:
  - Custom filter or aspect for rate limiting login attempts (could be done in controllers too, but a filter can globally catch rapid-fire attempts).
  - Exception translation: map security exceptions to our standardized error JSON (for example, handle BadCredentialsException, LockedException etc.).
  - Method Security: review @PreAuthorize usage for any new admin endpoints (make sure super admin endpoints require super role).
  - JWT Decoder: if we switch to RSA, configure the JwtDecoder with our public key (or issuer if we publish JWKS).

- *MFA Enforcement:* Adjust security config to require MFA where appropriate. This might be handled at the application logic level rather than config (e.g., a flag in user claims that triggers a second step).

- *Session Management:* Although we are stateless, Spring Security session management is set to STATELESS already. We will maintain that.

#### Database Schema (ADDITIONS REQUIRED)

- *Current State:* No tables for OTP, sessions, or audit as mentioned.

- *Needed:*
  - **OTP Table:** To log OTP codes (if we choose to store them) or at least OTP send attempts. Fields might include id, userId, code (hashed perhaps), channel, sendTime, expireTime, consumedTime, etc. This can help audit and allow manual code verification if needed.
  - **Session/Refresh Table:** To store active refresh tokens or device sessions. Fields: sessionId (UUID), userId, deviceId (if known), refreshToken (hashed or encrypted), issuedAt, expiresAt, lastUsedAt, etc. This helps in implementing logout-all and token invalidation.
  - **Audit Table:** To store security events. Fields: eventId, userId (nullable if not authenticated), eventType (LOGIN_SUCCESS, LOGIN_FAIL, OTP_REQUEST, etc.), timestamp, ip, userAgent, details (JSON for extra info like device or reason).
  - Possibly **Device Table:** If we implement trusted devices, a table to store known device fingerprints per user.

- *Updates to User Tables:*
  - Add a password_hash column to identity or respective user tables (if not already present).
  - Add an mfa_secret or mfa_enabled field for those who set up authenticator apps or require MFA.
  - Add an account_status field (ACTIVE, LOCKED, etc.) to easily query locked accounts.

- *Flyway Migrations:* Prepare these in incremental migrations as outlined (V7, V8, V9...). Ensure to script adding any new reference data (e.g., default roles or permissions if needed).

## 📋 Updated Implementation Priority Matrix

Given the gaps identified, we have re-ordered some implementation tasks and added new ones to address critical missing pieces first:

### Phase 1: Critical Authentication Infrastructure (Immediate)

- **TASK-001-NEW:** Create OTP code storage and rate limiting mechanism (e.g., schema for OTP if needed, integration with Redis). This covers building the backend support for multi-channel OTP and preventing abuse.

- **TASK-002-NEW:** Implement password management service and database fields. Even if OTP-first, we need the fallback ready. This includes hashing passwords and storing them, plus the email flow for resets.

- **TASK-003-NEW:** Add session management and refresh token rotation logic. Decide on approach (JWT with blacklist vs opaque tokens in DB) and implement accordingly. This is crucial for security improvements.

- **TASK-004-NEW:** Introduce audit logging for auth events. Create AuditEvent entity and service to log events at key points (login success/fail, OTP verified, etc.). Ensure logs can be retrieved by admin endpoints.

- **TASK-005-NEW:** Enhance OTP service to send emails and enforce rate limits (as discussed in OTP Service enhancement).

### Phase 2: Enhanced Security Features (High Priority)

- **TASK-006-MOD:** Upgrade the JWT token provider to use RSA keys and embed additional claims. Modify config to use keys (maybe read from keystore or env).

- **TASK-007-NEW:** Implement account lockout on too many failed attempts. Likely integrate with the authentication process to count failures (could be in the database or an in-memory cache that persists counts for a time window).

- **TASK-008-NEW:** Device trust system. Develop a way to mark a device as trusted (perhaps via a long-lived refresh token or a device cookie) so that subsequent logins from that device skip OTP (if user opted to trust it). This involves both backend (storing device ID, issuing persistent token) and front-end (generating a device fingerprint or using local storage).

- **TASK-009-NEW:** Build the password reset & change endpoints (and corresponding email template for reset). This is now a priority since password support is being added in Phase 1.

- **TASK-010-MOD:** Restructure authentication controllers: implement the universal login endpoint and refactor common logic into it or a service. Ensure backward compatibility (the old endpoints could remain for now but mark them deprecated if we plan to unify).

### Phase 3: Advanced Features (Medium Priority)

- **TASK-011-NEW:** SSO integration endpoints implementation. Since core auth is handled, now focus on enabling SSO for those who need it (likely use Spring Security SAML or OAuth support).

- **TASK-012-NEW:** Super admin tools and impersonation features. Leverage the audit logging from Phase 1 to log these actions.

- **TASK-013-NEW:** Guest/Vendor access system. Define how guests will be represented (likely in the TemporaryAccess table) and implement the creation/validation of guest tokens.

- **TASK-014-NEW:** Email and notification templates. Implement actual template files and a mechanism to populate and send them (via SMTP). Also allow override per company (which might be as simple as storing a custom template path or using a templating engine with variables like company name).

- **TASK-015-NEW:** Biometric auth support. Likely use existing standards (if web, WebAuthn; if mobile, device-native biometric linked to our app). This will probably involve generating a challenge in backend and validating it. It's lower priority than the above, hence placed later.

This adjusted plan ensures we tackle the **most critical gaps first** (those that pose security risks or fundamental feature needs), while still progressing towards the full feature set.

## Implementation Strategy Recommendations

To successfully execute this plan, we recommend the following strategies:

### Leverage Existing Strengths

1. **Build on the current OTP and JWT foundations** – Rather than replacing what's there, we will extend it. For example, keep using Twilio for SMS (since it's proven) and add email on top. Extend `JwtTokenProvider` instead of writing from scratch, just altering the algorithm and claims.

2. **Reuse the Role hierarchy and entities** – All persona entities and roles exist; use those to implement RBAC checks. For instance, if BoardMember isn't an entity yet, consider if it can be just a role attached to a Tenant user to minimize new constructs.

3. **Follow established patterns** – The codebase uses a clean separation (modules → services → contributions). We will mirror this for auth: e.g., put new domain objects in auth-module, business logic in auth-service, controllers in core-webapp. This will make our additions blend in seamlessly.

4. **Gradual refactor vs big bang** – Where possible, introduce new functionality alongside old. For instance, add the unified login endpoint but keep the old OTP endpoints operational initially. This reduces risk during transition (we can switch the frontend to the new endpoint when stable).

### Key Integration Points

1. **Database Migrations Coordination** – Adding new tables and columns needs careful migration scripting. We must ensure the migrations run in the correct order and test them on a copy of production data if possible. For example, adding a non-null column (like password_hash) to an existing table needs a default or to be nullable until populated.

2. **Service Enhancement vs New Services** – We need to decide where to place new logic. E.g., we could integrate password handling into the existing identity service or create a new PasswordService. The strategy will be to minimize disruption: possibly extend UserServiceImpl to handle password setting and verification, since it already deals with user data.

3. **Frontend-backend contract** – When implementing new endpoints, simultaneously update the frontend service stubs and types. This avoids integration mismatches. Using OpenAPI, we can even auto-generate some API client code. It's vital that the frontend expects exactly what the backend sends (e.g., field names in JSON, error format).

4. **Backward Compatibility** – Ensure that existing mobile apps or integrations that might be using current auth endpoints are not broken. If mobile apps are using, say, the staff OTP login endpoint, that will continue to work. We might mark it deprecated but not remove it until we've confirmed all clients have migrated to the new flow.

### Risk Mitigation

1. **Feature Toggles for rollout** – Consider behind-the-scenes toggles (config flags) for new features like MFA or unified login. This way, we could deploy the new code but enable it gradually. For example, initially keep unifiedLogin disabled to not affect users, then enable and test.

2. **Extensive QA and UAT** – Because authentication is a gateway to everything, allocate extra time for QA. Also, involve a small group of end-users or client admins to beta test the new flows (especially on variety of devices for OTP deliverability and UX feedback).

3. **Monitoring after launch** – Set up extra monitoring specifically on auth endpoints after release. Track metrics like OTP request rate, success/failure counts, average login time, etc. This will help quickly spot any issues (like an OTP not arriving or a bug causing refresh tokens to fail) before they become widespread problems.

4. **Documentation and Training** – Mitigate business risk (RISK-011) by preparing good help articles or even tutorial videos on the new login process. If users are aware of how OTP-first works and why it's beneficial, they'll be more accepting. Similarly, train internal support to handle common issues (can't receive OTP, etc.) with defined steps.

By following these strategies, we aim for a smooth implementation that strengthens the platform's security and user experience without causing regressions or user frustration.

## 📊 Implementation Effort Estimation

We assess the effort for major components as follows (Existing vs New work):

### Overall Assessment

CloudSuites' backend provides a **strong foundation** in terms of architecture and core models. The major work lies in **bolstering the authentication layer** to be on par with modern security standards and the feature set expected (OTP everywhere, MFA, etc.). By addressing the identified gaps – particularly around OTP handling, token security, and user experience improvements – the platform will achieve the robust onboarding and authentication system envisioned in this plan.

---

**Document Version:** 1.0  
**Last Updated:** September 10, 2025  
**Status:** Ready for Implementation
