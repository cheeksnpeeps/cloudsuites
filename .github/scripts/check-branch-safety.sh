#!/bin/bash
# Git Branch Safety Checker for CloudSuites
# Run this script before making commits to ensure proper workflow

echo "🔍 CloudSuites Git Branch Safety Check"
echo "======================================"

# Get current branch
current_branch=$(git rev-parse --abbrev-ref HEAD)
echo "📍 Current branch: $current_branch"

# Check if on main
if [ "$current_branch" = "main" ] || [ "$current_branch" = "master" ]; then
    echo "❌ WARNING: You are on the main branch!"
    echo ""
    echo "🚨 CRITICAL: All development must be done on feature branches"
    echo ""
    echo "To fix this:"
    echo "1. Create a feature branch: git checkout -b feat/your-feature-name"
    echo "2. Or switch to existing branch: git checkout feat/existing-branch"
    echo ""
    echo "❌ DO NOT COMMIT TO MAIN BRANCH"
    exit 1
fi

# Check if branch follows naming convention
if [[ $current_branch =~ ^(feat|fix|refactor|docs|test)/.+ ]]; then
    echo "✅ Branch naming follows convention"
else
    echo "⚠️  Branch name should follow convention: feat/fix/refactor/docs/test/feature-name"
    echo "   Current: $current_branch"
    echo "   Consider: git branch -m feat/$(echo $current_branch | sed 's/[^a-zA-Z0-9]/-/g')"
fi

# Check for uncommitted changes
if ! git diff-index --quiet HEAD --; then
    echo "📝 Uncommitted changes detected"
    echo "   Files changed: $(git diff --name-only | wc -l)"
    
    # Show status
    echo ""
    echo "📋 Git Status:"
    git status --porcelain
else
    echo "✅ Working directory clean"
fi

# Check if branch is up to date with main
git fetch origin main --quiet 2>/dev/null
commits_behind=$(git rev-list --count HEAD..origin/main 2>/dev/null || echo "0")
commits_ahead=$(git rev-list --count origin/main..HEAD 2>/dev/null || echo "0")

if [ "$commits_behind" -gt 0 ]; then
    echo "⚠️  Branch is $commits_behind commits behind main"
    echo "   Consider: git rebase origin/main"
else
    echo "✅ Branch is up to date with main"
fi

if [ "$commits_ahead" -gt 0 ]; then
    echo "📈 Branch is $commits_ahead commits ahead of main"
else
    echo "📊 Branch has no new commits"
fi

# Check for merge conflicts
if git ls-files -u | grep -q .; then
    echo "❌ Merge conflicts detected"
    echo "   Resolve conflicts before committing"
    exit 1
else
    echo "✅ No merge conflicts"
fi

echo ""
echo "🎯 Safety Check Summary:"
echo "- Branch: $current_branch"
echo "- Status: $([ "$current_branch" != "main" ] && echo "✅ Safe to commit" || echo "❌ Cannot commit")"
echo "- Commits ahead: $commits_ahead"
echo "- Commits behind: $commits_behind"

if [ "$current_branch" != "main" ] && [ "$current_branch" != "master" ]; then
    echo ""
    echo "✅ All checks passed! Safe to commit to feature branch."
    echo ""
    echo "Next steps:"
    echo "1. git add . (stage changes)"
    echo "2. git commit -m 'type: description' (commit changes)"
    echo "3. git push origin $current_branch (push to remote)"
    exit 0
else
    echo ""
    echo "❌ Cannot proceed. Create a feature branch first."
    exit 1
fi
