# Git Workflow Standards for CloudSuites

## 🚨 CRITICAL: Never Commit Directly to Main Branch

**ALL development work MUST be done on feature branches. Direct commits to main are prohibited.**

## Required Git Workflow

### 1. Branch Creation

```bash
# Always create feature branches from main
git checkout main
git pull origin main
git checkout -b feat/your-feature-name

# Branch naming conventions:
# feat/feature-name     - New features
# fix/bug-description   - Bug fixes
# refactor/scope        - Code refactoring
# docs/topic           - Documentation updates
# test/scope           - Test improvements
```

### 2. Development Process

```bash
# Make your changes and commit to the feature branch
git add .
git commit -m "type: descriptive commit message"

# Push feature branch to remote
git push origin feat/your-feature-name
```

### 3. Integration Process

```bash
# Create Pull Request via GitHub UI
# Never merge directly to main locally

# After PR approval and merge, clean up
git checkout main
git pull origin main
git branch -d feat/your-feature-name
```

## Current Repository Status

- **Current Branch**: `feat/refresh-token-rotation` ✅
- **Default Branch**: `main`
- **Status**: All recent commits are properly on feature branch

## Agent Guidelines

### ✅ DO

- Always check current branch before making commits
- Create new feature branches for new work
- Use descriptive branch names following conventions
- Make logical, atomic commits with clear messages
- Push feature branches to remote for backup

### ❌ NEVER

- Commit directly to main branch
- Force push to main branch
- Merge branches locally without PR review
- Delete main branch or modify its history
- Work on multiple unrelated features in same branch

## Git Commands for Agents

### Check Current Branch

```bash
git branch --show-current
git status
```

### Verify You're Not on Main

```bash
# If you're on main, immediately create feature branch
if [ "$(git branch --show-current)" = "main" ]; then
    echo "⚠️  WARNING: You are on main branch!"
    echo "Creating feature branch..."
    git checkout -b feat/$(date +%Y%m%d)-development
fi
```

### Safe Commit Pattern

```bash
# 1. Check branch
current_branch=$(git branch --show-current)
if [ "$current_branch" = "main" ]; then
    echo "❌ ERROR: Cannot commit to main branch"
    exit 1
fi

# 2. Stage and commit
git add .
git commit -m "type: description"

# 3. Push to remote
git push origin $current_branch
```

## Branch Protection Rules

### Main Branch Protection

- Require pull request reviews
- Require status checks to pass
- Require branches to be up to date
- Restrict pushes to main branch
- Require linear history

### Feature Branch Guidelines

- Use descriptive names
- Keep branches focused on single features
- Regularly rebase on main to stay current
- Delete after successful merge

## Emergency Procedures

### If Accidentally Committed to Main

```bash
# 1. Don't panic - we can fix this
git log --oneline -n 5

# 2. Create feature branch from current state
git checkout -b feat/emergency-fix

# 3. Reset main to previous state (if not pushed)
git checkout main
git reset --hard HEAD~1

# 4. Continue work on feature branch
git checkout feat/emergency-fix
```

### If Pushed to Main by Mistake

```bash
# 1. Immediately contact team
# 2. Create feature branch from the commits
# 3. Revert the commits on main
# 4. Create proper PR from feature branch
```

## Code Review Process

### Before Creating PR
- [ ] All commits are on feature branch
- [ ] Branch is up to date with main
- [ ] All tests pass locally
- [ ] Code follows project standards
- [ ] No sensitive data in commits

### PR Requirements
- [ ] Descriptive title and description
- [ ] Link to related issues
- [ ] Reviewers assigned
- [ ] CI/CD checks passing
- [ ] No conflicts with main

## Automation Support

### Pre-commit Hook (Recommended)
```bash
#!/bin/sh
# .git/hooks/pre-commit
current_branch=$(git branch --show-current)
if [ "$current_branch" = "main" ]; then
    echo "❌ ERROR: Direct commits to main branch are not allowed!"
    echo "Please create a feature branch:"
    echo "  git checkout -b feat/your-feature-name"
    exit 1
fi
```

### Git Aliases (Add to .gitconfig)
```bash
[alias]
    # Safe commit that checks branch
    scommit = "!f() { if [ \"$(git branch --show-current)\" = \"main\" ]; then echo \"❌ Cannot commit to main\"; exit 1; fi; git commit \"$@\"; }; f"
    
    # Create and switch to feature branch
    feature = "!f() { git checkout main && git pull origin main && git checkout -b feat/$1; }; f"
    
    # Show current branch prominently
    branch-current = branch --show-current
```

## Training Checklist for Agents

- [ ] Understand branch protection rules
- [ ] Know how to check current branch
- [ ] Can create feature branches properly
- [ ] Understand commit message conventions
- [ ] Know emergency procedures
- [ ] Familiar with PR process

## Monitoring and Compliance

### Daily Checks
- Monitor main branch for unauthorized commits
- Review feature branch naming compliance
- Verify PR process adherence
- Check for abandoned branches

### Weekly Reviews
- Clean up merged feature branches
- Review git workflow compliance
- Update documentation as needed
- Train team on any issues found

---

**Remember: The main branch represents production-ready code. All experimental, development, and feature work MUST happen on dedicated feature branches with proper code review through Pull Requests.**
