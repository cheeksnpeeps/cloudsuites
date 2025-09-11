# CloudSuites Copilot Instructi### Authentication-Specific Development Rules

### Before Starting Any Authentication Work

1. **ALWAYS** check `plan/auth-implementation-tracker.md` first to understand:
   - Current sprint and week
   - Today's assigned tasks
   - PR dependencies and status
   - Quality requirements

2. **Reference requirements** from `plan/backend-authentication-onboarding-platform-1.md` for:
   - Business rules (REQ-XXX references)
   - Security constraints (SEC-XXX references) 
   - Technical constraints (CON-XXX references)
   - Gap analysis findings

3. **Follow implementation details** from `plan/auth-implementation-roadmap.md` for:
   - Specific PR acceptance criteria
   - File structures and naming conventions
   - Integration patterns with existing code
   - Testing requirements

### 📝 MANDATORY: Documentation Update Requirements

**CRITICAL**: Every authentication PR MUST update documentation. This is not optional.

#### After Completing ANY Authentication Task:

1. **ALWAYS update `plan/auth-implementation-tracker.md`**:
   - ✅ Mark completed tasks as done
   - Update PR status: 🔄 IN PROGRESS → ✅ COMPLETED
   - Add completion date and commit hash
   - Update overall progress percentage
   - Update files created/modified section
   - Add implementation highlights

2. **Update progress tracking**:
   - Change PR status from "Not Started" to "Completed"
   - Update sprint progress counters
   - Verify all task checkboxes are marked
   - Commit the documentation updates

3. **Required information in tracker updates**:
   ```markdown
   **Status:** ✅ **COMPLETED**
   **Completed:** [Current Date]
   **Commit:** `[commit-hash]`
   **Implementation Highlights:**
   - [Key achievements]
   - [Lines of code added]
   - [Security features implemented]
   ```

#### Documentation Update Checklist (Per PR):
- [ ] Tracker status updated to COMPLETED
- [ ] All task checkboxes marked as done
- [ ] Files created/modified section updated
- [ ] Commit hash added to tracker
- [ ] Progress percentage recalculated
- [ ] Implementation highlights added
- [ ] Documentation changes committed to git

#### Enforcement:
**No PR is considered complete without tracker updates.** Agents must include documentation updates in their final commit for each authentication task.

#### Example Commands:
```bash
# After completing authentication work
git add plan/auth-implementation-tracker.md
git commit -m "docs: Update tracker - PR #X [Feature] completed

- [Task completion summary]
- [Key implementation details]
- Progress: X/50 PRs completed"
```n Implementation Addendum

## Authentication Implementation Context

When working on CloudSuites authentication features, you have access to a comprehensive implementation plan with three key documents:

### 📚 Authentication Documentation Structure

1. **Master Requirements & Design**: `plan/backend-authentication-onboarding-platform-1.md`
   - Complete requirements (REQ-001 to REQ-015)
   - Security constraints (SEC-001 to SEC-007)
   - Gap analysis of existing vs. needed implementation
   - Business rules and compliance requirements

2. **Implementation Roadmap**: `plan/auth-implementation-roadmap.md`
   - 6-week sprint structure with 50+ PRs
   - Detailed acceptance criteria for each task
   - File structure and deliverables
   - Dependencies and integration points

3. **Daily Task Tracker**: `plan/auth-implementation-tracker.md`
   - Current sprint status and daily tasks
   - Progress tracking with status indicators
   - Development commands and quality checklists
   - Agent-specific task assignments

4. **Agent Usage Guide**: `plan/auth-agent-guide.md`
   - When and how to reference each document
   - Decision trees for document selection
   - Response templates and patterns

## Authentication-Specific Development Rules

### Before Starting Any Authentication Work

1. **ALWAYS** check `plan/auth-implementation-tracker.md` first to understand:
   - Current sprint and week
   - Today's assigned tasks
   - PR dependencies and status
   - Quality requirements

2. **Reference requirements** from `plan/backend-authentication-onboarding-platform-1.md` for:
   - Business rules (REQ-XXX references)
   - Security constraints (SEC-XXX references) 
   - Technical constraints (CON-XXX references)
   - Gap analysis findings

3. **Follow implementation details** from `plan/auth-implementation-roadmap.md` for:
   - Specific PR acceptance criteria
   - File structures and naming conventions
   - Integration patterns with existing code
   - Testing requirements

### Authentication Module Architecture

**CRITICAL**: CloudSuites uses a multi-module Maven architecture. For authentication:

- **New auth entities**: `modules/auth-module/` (to be created)
- **Business logic**: `services/auth-service/` (to be created)
- **REST APIs**: `contributions/core-webapp/src/main/java/com/cloudsuites/framework/webapp/auth/`
- **Database migrations**: `src/main/resources/db/migration/V00X__auth_*.sql`

**Never violate the dependency flow**: modules ← services ← webapp

### Key Authentication Requirements to Remember

- **REQ-001**: OTP-first authentication (passwordless primary flow)
- **REQ-003**: JWT with 15-min access tokens, 30-day refresh tokens, rotation required
- **SEC-001**: Rate limiting (3 OTP attempts per 5 minutes)
- **SEC-002**: Comprehensive audit logging for all auth events
- **SEC-003**: Refresh token rotation on each use
- **CON-001**: Spring Boot 3.3.2 with Spring Security JWT
- **CON-007**: Twilio SMS integration (extend for email)

### Current Implementation Gaps (Key Items to Implement)

Based on gap analysis, priority missing features:
1. **OTP storage and email channel** (currently SMS-only via Twilio)
2. **Refresh token rotation and session management**
3. **Audit logging for authentication events**
4. **Rate limiting beyond Twilio's defaults**
5. **Password management system** (currently no password support)
6. **Unified login endpoint** (currently persona-specific endpoints)

### Development Workflow for Authentication

#### Branch Naming Convention
```bash
feat/auth-[feature-name]     # New features
fix/auth-[issue]            # Bug fixes
refactor/auth-[component]   # Code improvements
```

#### Commit Message Template
```
feat(auth): [PR number] - [Brief description]

- Specific change 1
- Specific change 2

Implements: [REQ-XXX, SEC-XXX references]
Files: [key files modified]
```

#### Testing Commands
```bash
# Run auth-specific tests
mvn test -Dtest="*Auth*"

# Integration tests
mvn test -Dtest="*AuthIntegration*"

# Verify JWT functionality
mvn test -Dtest="*JWT*,*Token*"
```

### Authentication-Specific Response Patterns

When user requests authentication work, structure your response:

```markdown
## Authentication Task Analysis

**Current Status**: [Check tracker for current sprint/week]
**Requirements**: [REQ-XXX references from master doc]
**Implementation**: [PR number and scope from roadmap]
**Dependencies**: [Check roadmap for prerequisites]

## Implementation Plan
[Specific steps with file paths and acceptance criteria]

## Quality Checklist
- [ ] Security requirements met (SEC-XXX)
- [ ] Audit logging implemented
- [ ] Rate limiting considered
- [ ] JWT token handling secure
- [ ] Tests written and passing
- [ ] **MANDATORY: Tracker documentation updated**
- [ ] **MANDATORY: PR status marked as COMPLETED**
- [ ] **MANDATORY: Progress percentage recalculated**
```

### Integration Points to Remember

1. **Existing JWT Provider**: `modules/identity-module/.../JwtTokenProvider.java`
   - Currently uses HMAC-SHA256 (needs upgrade to RSA256)
   - Token expiry currently 1 day (needs to be 15 minutes)

2. **Existing OTP Service**: `modules/identity-module/.../TwilioOtpService.java`
   - SMS-only implementation
   - Needs extension for email channel

3. **Existing User Entities**: Tenant, Owner, Staff, Admin entities exist
   - Need to extend for password storage
   - Need session/device trust relationships

4. **Security Configuration**: `contributions/core-webapp/.../SecurityConfiguration.java`
   - Currently configured for basic JWT validation
   - Needs enhancement for new auth flows

### Common Authentication Tasks Decision Flow

```
User Request → Check Tracker → Identify Requirements → Review Gaps → Implement → Test → Update Status
      ↓              ↓              ↓                    ↓             ↓         ↓         ↓
   Task Type    Current Sprint   REQ-XXX from     Gap Analysis   Follow PR   Quality   Update
   Category     and Week        Master Doc       in Master Doc  Criteria    Gates     Tracker
```

## Emergency Authentication References

**Security Issues**: Always check SEC-001 through SEC-007 in master doc
**JWT Problems**: Reference gap analysis "JWT Token Provider" section
**OTP Issues**: Reference gap analysis "OTP Service" section  
**Database**: Reference gap analysis "Database Schema" section
**API Design**: Reference roadmap Sprint 3 "API Layer Implementation"

---

This addendum ensures you have clear guidance on authentication implementation while maintaining CloudSuites' architectural principles and security standards.
