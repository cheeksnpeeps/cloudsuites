#!/bin/bash
# Install git workflow safety tools for CloudSuites

echo "🔧 Installing CloudSuites Git Workflow Safety Tools"
echo "=================================================="

# Install pre-commit hook
if [ -f ".git/hooks/pre-commit" ]; then
    echo "⚠️  Pre-commit hook already exists. Creating backup..."
    cp .git/hooks/pre-commit .git/hooks/pre-commit.backup
fi

echo "📦 Installing pre-commit hook..."
cp .github/hooks/pre-commit .git/hooks/
chmod +x .git/hooks/pre-commit
echo "✅ Pre-commit hook installed"

# Make safety checker executable
echo "📦 Setting up branch safety checker..."
chmod +x .github/scripts/check-branch-safety.sh
echo "✅ Branch safety checker ready"

# Test current branch status
echo ""
echo "🔍 Running initial safety check..."
./.github/scripts/check-branch-safety.sh

echo ""
echo "🎉 Git workflow safety tools installed successfully!"
echo ""
echo "Available commands:"
echo "  ./.github/scripts/check-branch-safety.sh  - Check current branch status"
echo "  git commit                                 - Will automatically check branch safety"
echo ""
echo "📚 For complete guidelines, see:"
echo "  .github/git-workflow-standards.md"
