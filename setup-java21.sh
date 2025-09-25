#!/bin/bash
# CloudSuites Development Environment Setup
# Enforces Java 21 for development consistency

echo "🚀 CloudSuites Development Environment Setup"
echo "============================================="

# Check if Java 21 is installed
JAVA21_PATH="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"

if [ ! -d "$JAVA21_PATH" ]; then
    echo "❌ Java 21 (Temurin) not found at $JAVA21_PATH"
    echo "📦 Please install Java 21 Temurin:"
    echo "   brew install --cask temurin21"
    exit 1
fi

# Set environment variables
export JAVA_HOME="$JAVA21_PATH"
export PATH="$JAVA_HOME/bin:$PATH"

echo "✅ Java 21 detected and configured"
echo "📍 JAVA_HOME: $JAVA_HOME"

# Verify Java version
JAVA_VERSION=$($JAVA_HOME/bin/java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" != "21" ]; then
    echo "❌ Expected Java 21, but got version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version verified: $JAVA_VERSION"

# Verify Maven is using the correct Java version
echo "🔍 Checking Maven configuration..."
mvn --version | head -n 3

echo ""
echo "🎯 Environment Ready! You can now run:"
echo "   mvn clean install -s .mvn/settings.xml"
echo "   mvn test -pl modules/auth-module"
echo ""
echo "💡 To make this permanent, add to your ~/.zshrc:"
echo "   export JAVA_HOME=$JAVA21_PATH"
echo "   export PATH=\$JAVA_HOME/bin:\$PATH"
