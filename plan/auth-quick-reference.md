# 🚀 CloudSuites Authentication Implementation - Quick Reference

## 📋 Current Status
- **Sprint**: Week 1 of 6-week implementation
- **Active PRs**: 10 PRs planned for Week 1
- **Focus**: Foundation (Database, JWT, Auth Module, Services)

## 📚 Document Quick Access

| What I Need | Document | Key Sections |
|-------------|----------|--------------|
| **Current Task** | `auth-implementation-tracker.md` | Progress Table, Week 1 Tasks |
| **Requirements** | `backend-authentication-onboarding-platform-1.md` | Section 1 (REQ-XXX), Section 7 (Risks) |
| **Implementation** | `auth-implementation-roadmap.md` | Sprint 1, PR Details |
| **Usage Guide** | `auth-agent-guide.md` | Decision Trees, Response Templates |

## 🎯 Key Requirements (Must Remember)
- **REQ-001**: OTP-first authentication (passwordless primary)
- **REQ-003**: JWT - 15min access, 30-day refresh, rotation required
- **SEC-001**: Rate limiting (3 OTP attempts/5min)
- **SEC-002**: Audit logging for all auth events
- **SEC-003**: Refresh token rotation on each use

## 🔧 Current Implementation Gaps (Priority Order)

1. **Database**: OTP, session, audit tables missing (V2-V5 migrations)
2. **JWT**: Needs RSA256 upgrade + token rotation
3. **OTP**: Email channel missing (SMS-only via Twilio)
4. **Password**: No password management system exists
5. **Audit**: No auth event logging implemented
6. **Rate Limiting**: Only Twilio defaults, no app-level limits

## 📁 File Structure (Where to Add Code)
```
modules/auth-module/          # 🆕 New authentication entities
services/auth-service/        # 🆕 New business logic
contributions/core-webapp/    # 🔄 Extend existing controllers
  src/main/java/...webapp/auth/
src/main/resources/db/migration/ # 🆕 V007+ migrations
```

## 🚨 Critical Development Rules
1. **Check tracker first** - Always verify current sprint/task
2. **Follow dependency flow** - modules ← services ← webapp
3. **Use Maven settings** - `mvn -s .mvn/settings.xml [command]`
4. **Security first** - Implement SEC-XXX requirements from start
5. **Update tracker** - Mark status after completing tasks

## 💻 Quick Commands
```bash
# Check current auth implementation status
grep "PR #" plan/auth-implementation-tracker.md

# Find specific requirement details  
grep "REQ-003" plan/backend-authentication-onboarding-platform-1.md

# Run auth-specific tests
mvn -s .mvn/settings.xml test -Dtest="*Auth*"

# Start development environment
docker-compose up --build
```

## 🔍 When to Reference Each Document
- **Starting work**: Check tracker → Check requirements → Check roadmap
- **Need requirements**: backend-authentication-onboarding-platform-1.md
- **Need implementation details**: auth-implementation-roadmap.md  
- **Daily progress**: auth-implementation-tracker.md
- **Confused about docs**: auth-agent-guide.md

---
**Last Updated**: September 10, 2025 | **Version**: 1.0
**Current Sprint**: 1 of 6 | **Week**: 1 | **Status**: Ready for Implementation
