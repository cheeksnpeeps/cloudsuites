# Multi-stage build for CloudSuites with Java 21 + Spring Boot 3.3 + React
FROM maven:3.9.11-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy all source code (modules need to exist for Maven multi-module resolution)
COPY . .

# Download dependencies and build (with cache mount for faster rebuilds)
RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests -B -s .mvn/settings.xml

# Production stage with Java 21
FROM eclipse-temurin:21-jre-alpine

# Add metadata
LABEL maintainer="cloudsuites@example.com"
LABEL version="1.0"
LABEL description="CloudSuites Property Management Platform"

# Create app user for security
RUN addgroup -g 1000 cloudsuites && \
    adduser -D -s /bin/sh -u 1000 -G cloudsuites cloudsuites

# Set working directory
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy the built JAR file
COPY --from=build /app/contributions/core-webapp/target/*.jar app.jar

# Change ownership to cloudsuites user
RUN chown cloudsuites:cloudsuites app.jar

# Switch to non-root user
USER cloudsuites

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Environment variables for CloudSuites
# Non-sensitive defaults only - sensitive values should be provided via .env file or docker-compose
ENV SPRING_PROFILES_ACTIVE=dev

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
