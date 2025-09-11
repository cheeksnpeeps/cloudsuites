# CloudSuites Authentication Implementation Guide for Copilot Agents

## 📚 Document Reference System

### Primary Documents Overview

| Document | Purpose | When to Use | Key Information |
|----------|---------|-------------|-----------------|
| `backend-authentication-onboarding-platform-1.md` | **Master Requirements & Design** | Before starting any auth-related task | Requirements, constraints, architecture decisions, gap analysis |
| `auth-implementation-roadmap.md` | **Complete Implementation Plan** | Planning sprints and understanding dependencies | 6-week detailed roadmap, 50+ PRs, acceptance criteria |
| `auth-implementation-tracker.md` | **Daily Task Management** | Active development and progress tracking | Day-by-day tasks, status tracking, commands |

### Document Usage Decision Tree

```text
🤔 What am I working on?

├── 🎯 Planning/Architecture Decisions
│   └── Use: backend-authentication-onboarding-platform-1.md
│       - Requirements (REQ-001 to REQ-015)
│       - Security requirements (SEC-001 to SEC-007)
│       - Technical constraints (CON-001 to CON-014)
│       - Gap analysis findings
│
├── 📋 Understanding Implementation Scope
│   └── Use: auth-implementation-roadmap.md
│       - Sprint breakdown (6 weeks)
│       - PR dependencies
│       - Detailed acceptance criteria
│       - File structure and deliverables
│
└── 🔨 Active Development Work
    └── Use: auth-implementation-tracker.md
        - Today's specific tasks
        - Commands to run
        - Status updates
        - Quality checklists
```

## 🎯 Agent Usage Scenarios

### Scenario 1: Starting a New Authentication Task

**Trigger:** User assigns auth-related work or mentions authentication features

**Process:**

1. **First:** Check `auth-implementation-tracker.md` for current sprint and today's tasks
2. **Then:** Reference `backend-authentication-onboarding-platform-1.md` for specific requirements
3. **Finally:** Use `auth-implementation-roadmap.md` for detailed implementation guidance

**Example:**

```text
User: "Implement JWT token enhancement"

Agent should:
1. Check tracker → Find "PR #3: JWT Enhancement" in Week 1, Day 2
2. Check requirements → Find REQ-003, SEC-003, CON-001 for context
3. Check roadmap → Get detailed acceptance criteria and file changes
```

### Scenario 2: Understanding Requirements

**Trigger:** Questions about business rules, security, or compliance

**Process:**

1. **Primary:** Use `backend-authentication-onboarding-platform-1.md`
   - Section 1: Requirements & Constraints
   - Section 7: Risks & Assumptions
   - Gap Analysis section

**Example:**

```text
User: "What are the password requirements?"

Agent should reference:
- REQ-004: Password fallback option
- SEC-006: Secure password reset
- Password Management gap analysis
```

### Scenario 3: Technical Implementation Questions

**Trigger:** How to implement specific features, file structures, or dependencies

**Process:**

1. **Primary:** Use `auth-implementation-roadmap.md`
   - Detailed PR descriptions
   - File structures
   - Acceptance criteria
2. **Secondary:** Reference `backend-authentication-onboarding-platform-1.md` for context

**Example:**

```text
User: "How should we structure the OTP service?"

Agent should reference:
- Roadmap PR #7: Multi-Channel OTP Service
- Requirements REQ-002: SMS/email OTP support
- Gap analysis: Email OTP missing
```

### Scenario 4: Progress Tracking and Status Updates

**Trigger:** Daily standups, progress questions, or status updates

**Process:**

1. **Primary:** Use `auth-implementation-tracker.md`
   - Progress overview table
   - Daily task status
   - Sprint completion metrics

**Example:**

```text
User: "What's our authentication implementation status?"

Agent should reference:
- Tracker progress table
- Current sprint status
- Today's completed/pending PRs
```

## 🔗 Cross-Reference Patterns

### Requirements to Implementation Mapping

| Requirement | Implementation Documents |
|-------------|-------------------------|
| **REQ-001**: OTP-first auth | Tracker: PR #7 (Multi-channel OTP), Roadmap: Sprint 1-2 |
| **REQ-003**: JWT tokens | Tracker: PR #3 (JWT Enhancement), Roadmap: Day 2 tasks |
| **SEC-001**: Rate limiting | Tracker: PR #9 (Redis Rate Limiting), Roadmap: Day 5 |
| **SEC-002**: Audit logging | Tracker: PR #8 (Audit Service), Roadmap: Day 4 |

### Gap Analysis to Implementation Mapping

| Gap Identified | Implementation Solution |
|-----------------|------------------------|
| **No OTP storage** | Tracker: PR #1 (Database Schema), PR #7 (OTP Service) |
| **No refresh token rotation** | Tracker: PR #4 (Token Rotation), Roadmap: Sprint 1 |
| **No unified login endpoint** | Tracker: PR #21 (Unified Controller), Roadmap: Sprint 3 |
| **No password management** | Tracker: PR #6 (Password Service), Roadmap: Sprint 1 |

## 📋 Reference Commands for Agents

### Quick Reference Lookups

```bash
# Find requirement details
grep "REQ-001" plan/backend-authentication-onboarding-platform-1.md

# Find implementation for specific feature
grep "OTP" plan/auth-implementation-roadmap.md

# Check current task status
grep "PR #" plan/auth-implementation-tracker.md

# Find security requirements
grep "SEC-" plan/backend-authentication-onboarding-platform-1.md
```

### Context-Specific Searches

```bash
# For database work
grep -A5 -B5 "database\|schema\|migration" plan/

# For API endpoints
grep -A5 -B5 "endpoint\|controller\|API" plan/

# For security implementation
grep -A5 -B5 "security\|JWT\|auth" plan/

# For frontend work
grep -A5 -B5 "React\|frontend\|UI" plan/
```

## 🎯 Agent Decision Framework

### When Working on Authentication Tasks

```text
1. ALWAYS check auth-implementation-tracker.md FIRST
   └── Confirms current sprint and task priority

2. IF requirements clarification needed:
   └── Reference backend-authentication-onboarding-platform-1.md
       ├── Section 1: Requirements & Constraints
       ├── Section 7: Risks & Assumptions
       └── Gap Analysis section

3. IF implementation details needed:
   └── Reference auth-implementation-roadmap.md
       ├── Specific PR descriptions
       ├── File structures
       ├── Acceptance criteria
       └── Dependencies

4. IF working on existing code:
   └── Reference gap analysis for current state
   └── Check what needs enhancement vs. new implementation

5. ALWAYS update tracker status after completing work
```

### Priority Order for Document Reference

1. **🔥 URGENT/CURRENT**: `auth-implementation-tracker.md`
2. **📋 REQUIREMENTS**: `backend-authentication-onboarding-platform-1.md`
3. **🔧 IMPLEMENTATION**: `auth-implementation-roadmap.md`

## 📝 Agent Response Templates

### When Starting Auth Work

```markdown
I'm working on authentication. Let me check the current status and requirements:

✅ Current Sprint: [from tracker]
✅ Today's Task: [from tracker] 
✅ Requirements: [from master doc]
✅ Implementation Details: [from roadmap]

Proceeding with: [specific task with acceptance criteria]
```

### When Questions About Requirements

```markdown
Based on the authentication requirements documentation:

**Requirement**: [REQ-XXX from master doc]
**Context**: [from gap analysis or constraints]
**Implementation**: [from roadmap/tracker]
**Status**: [current progress]
```

### When Providing Implementation Guidance

```markdown
For this authentication feature:

**Sprint**: [from roadmap]
**PR**: [from tracker]
**Files to Create/Modify**: [from roadmap]
**Acceptance Criteria**: [from roadmap]
**Dependencies**: [from roadmap]
**Current Status**: [from tracker]
```

---

This guide ensures agents always know which document to reference for specific authentication implementation needs, maintaining consistency and avoiding confusion during the 6-week implementation process.
