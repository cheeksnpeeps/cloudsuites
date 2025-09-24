# Redis Rate Limiting Service

## Overview

CloudSuites implements a comprehensive rate limiting system using Redis as the primary storage backend with intelligent in-memory fallback. This provides robust protection against abuse while maintaining high availability.

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │───▶│ RateLimitService│───▶│ Redis Cluster   │
│                 │    │                 │    │                 │
│ Authentication  │    │  - checkRecord  │    │ - Sliding Window│
│ API Endpoints   │    │  - lockoutUser  │    │ - Persistence   │
│ OTP Operations  │    │  - getRateStatus│    │ - HA Support    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                       │
                                │    Connection Failed  │
                                ▼                       ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │  Fallback Mode  │    │  Health Check   │
                       │                 │    │                 │
                       │ - In-Memory Map │    │ - Auto Recovery │
                       │ - Thread Safe   │    │ - Circuit Break │
                       │ - Cleanup Task  │    │ - Retry Logic   │
                       └─────────────────┘    └─────────────────┘
```

## Key Features

### 🔄 **Sliding Window Algorithm**
- **Time-based buckets**: Requests tracked within configurable time windows
- **Precise limiting**: Exact request counting with millisecond accuracy
- **Automatic cleanup**: Expired entries removed to prevent memory bloat

### 🔒 **Progressive Lockout System**
- **Exponential backoff**: Lockout duration increases with repeated violations
- **Configurable thresholds**: Different limits for different operations
- **Automatic unlock**: Time-based release with manual override capability

### 🏥 **High Availability**
- **Graceful degradation**: Seamless fallback to in-memory when Redis unavailable
- **Health monitoring**: Automatic Redis health checks and recovery
- **Zero downtime**: Service continues operating during Redis maintenance

### ⚡ **Performance Optimized**
- **Connection pooling**: Efficient Redis connection management
- **Async operations**: Non-blocking I/O for high throughput
- **Memory efficient**: Optimized data structures for minimal overhead

## Configuration

### Application Configuration (`application.yml`)

```yaml
cloudsuites:
  rate-limiting:
    enabled: true
    redis-enabled: true
    
    configurations:
      login:
        limit: 5                    # Max attempts
        window-minutes: 15          # Time window
        enable-lockout: true        # Enable progressive lockout
        lockout-threshold: 5        # Attempts before lockout
      
      otp_verify:
        limit: 5
        window-minutes: 5
        enable-lockout: true
        lockout-threshold: 3
      
      api_access:
        limit: 100
        window-minutes: 1
        enable-lockout: false       # High-frequency, no lockout
        lockout-threshold: 1000
    
    lockout:
      base-duration-minutes: 1      # Initial lockout period
      max-duration-minutes: 1440   # Max lockout (24 hours)
      multiplier: 5                 # Exponential multiplier
```

### Environment Variables (`.env`)

```bash
# Redis Connection
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_DATABASE=1
REDIS_PASSWORD=csRedisPass123
REDIS_TIMEOUT=2000ms

# Connection Pool
REDIS_POOL_MAX_ACTIVE=8
REDIS_POOL_MAX_IDLE=8
REDIS_POOL_MIN_IDLE=0
REDIS_POOL_MAX_WAIT=-1ms

# Rate Limiting
RATE_LIMITING_ENABLED=true
RATE_LIMITING_REDIS_ENABLED=true
RATE_LIMIT_LOGIN_ATTEMPTS=5
RATE_LIMIT_LOGIN_WINDOW_MINUTES=15
```

### Docker Configuration (`compose.yaml`)

```yaml
redis:
  image: redis:7-alpine
  container_name: cloudsuites-redis
  ports:
    - "6379:6379"
  command: redis-server --requirepass ${REDIS_PASSWORD:-csRedisPass123}
  volumes:
    - redis_data:/data
  networks:
    - cloudsuites-network
  healthcheck:
    test: ["CMD", "redis-cli", "--raw", "incr", "ping"]
    interval: 30s
    timeout: 10s
    retries: 5
```

## Usage Examples

### Controller Integration

```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    private final RateLimitService rateLimitService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String key = "auth:login:" + request.getEmail();
        
        // Check rate limit before processing
        RateLimitResult rateLimitResult = rateLimitService.checkAndRecord(
            key, 5, Duration.ofMinutes(15));
        
        if (!rateLimitResult.isAllowed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of(
                    "error", "Too many login attempts",
                    "retryAfter", rateLimitResult.getRetryAfter(),
                    "remaining", rateLimitResult.getRemaining()
                ));
        }
        
        // Process login...
        return processLogin(request);
    }
}
```

### Custom Rate Limiting

```java
@Service
public class CustomService {
    
    @Autowired
    private RateLimitService rateLimitService;
    
    public void processHighVolumeOperation(String userId) {
        // Custom rate limit for specific operation
        RateLimitResult result = rateLimitService.checkAndRecord(
            "custom:operation:" + userId,
            100,  // Allow 100 requests
            Duration.ofHours(1)  // Per hour
        );
        
        if (!result.isAllowed()) {
            throw new RateLimitExceededException(
                "Operation rate limit exceeded. Try again in " + 
                result.getRetryAfter() + " seconds");
        }
        
        // Process operation...
    }
}
```

## Operations & Monitoring

### Health Checks

```bash
# Check Redis connectivity
docker exec cloudsuites-redis redis-cli -a csRedisPass123 ping

# Monitor memory usage
docker exec cloudsuites-redis redis-cli -a csRedisPass123 INFO memory

# Check active connections
docker exec cloudsuites-redis redis-cli -a csRedisPass123 CLIENT LIST

# View rate limiting keys
docker exec cloudsuites-redis redis-cli -a csRedisPass123 KEYS "rate:*"
```

### Performance Monitoring

```bash
# Monitor Redis performance
docker exec cloudsuites-redis redis-cli -a csRedisPass123 --latency

# Check slow queries
docker exec cloudsuites-redis redis-cli -a csRedisPass123 SLOWLOG GET 10

# Monitor memory and CPU
docker stats cloudsuites-redis
```

### Debugging

```bash
# Application logs
docker-compose logs cloudsuites-app | grep -i "rate limit"

# Redis logs
docker-compose logs redis

# Check fallback mode activation
docker-compose logs cloudsuites-app | grep -i "fallback"
```

## Best Practices

### 🎯 **Rate Limit Design**
- **Operation-specific limits**: Different limits for login vs API calls
- **User context**: Include user ID in rate limit keys
- **Geographic considerations**: Consider different limits per region
- **Burst allowance**: Allow short bursts within longer windows

### 🔧 **Configuration Guidelines**
- **Start conservative**: Begin with lower limits, increase based on usage
- **Monitor metrics**: Track hit rates and adjust accordingly
- **Test edge cases**: Validate behavior at limit boundaries
- **Document changes**: Keep configuration changes well documented

### 🚀 **Performance Optimization**
- **Key naming**: Use consistent, hierarchical key naming patterns
- **Memory management**: Set appropriate Redis memory limits
- **Connection tuning**: Adjust pool sizes based on load testing
- **Monitoring**: Set up alerts for Redis health and performance

### 🔒 **Security Considerations**
- **Key isolation**: Use database separation for different services
- **Access control**: Implement Redis AUTH with strong passwords
- **Network security**: Use TLS in production environments
- **Audit logging**: Log rate limit violations for security analysis

## Troubleshooting

### Common Issues

| Issue | Symptoms | Solution |
|-------|----------|----------|
| **Redis Connection Failed** | "Unable to connect to Redis" errors | Check Redis container status, verify credentials |
| **High Memory Usage** | Redis OOM errors | Implement TTL, increase memory limits, or add cleanup |
| **Slow Performance** | High response times | Optimize connection pool, check Redis latency |
| **Fallback Mode Active** | "Using in-memory fallback" logs | Diagnose Redis connectivity, check network |
| **Rate Limits Not Working** | Users not being blocked | Verify configuration, check key patterns |

### Recovery Procedures

```bash
# Reset Redis data (⚠️ CAUTION: Clears all data)
docker-compose down
docker volume rm cloudsuites_redis_data
docker-compose up -d redis

# Restart rate limiting service
docker-compose restart cloudsuites-app

# Clear specific user rate limits
docker exec cloudsuites-redis redis-cli -a csRedisPass123 DEL "rate:login:user@example.com"

# Emergency disable rate limiting
# Set RATE_LIMITING_ENABLED=false in .env and restart
```

## Testing

### Unit Tests
```bash
# Run rate limiting unit tests (10/10 passing)
mvn test -s .mvn/settings.xml -pl modules/auth-module -Dtest="RedisRateLimitServiceImplTest"
```

### Integration Tests
```bash
# Run with Redis container
docker-compose up -d redis
mvn test -s .mvn/settings.xml -pl modules/auth-module -Dtest="RedisRateLimitServiceIntegrationTest"

# Tests gracefully skip when Redis unavailable
```

### Load Testing
```bash
# Test rate limiting under concurrent load
# Use the built-in concurrent test in RedisRateLimitServiceImplTest
# Validates thread safety and limit enforcement
```

## Migration & Deployment

### Production Deployment
1. **Redis Setup**: Deploy Redis cluster for high availability
2. **Configuration**: Update environment variables for production
3. **Monitoring**: Set up Redis monitoring and alerting
4. **Backup**: Configure Redis persistence and backup
5. **Testing**: Validate rate limiting behavior in staging

### Scaling Considerations
- **Redis Cluster**: Use Redis Cluster for horizontal scaling
- **Consistent Hashing**: Implement for distributed rate limiting
- **Regional Deployment**: Consider geo-distributed Redis instances
- **Performance Testing**: Load test at expected traffic levels

---

## 📚 Additional Resources

- **Redis Documentation**: [redis.io/documentation](https://redis.io/documentation)
- **Spring Data Redis**: [docs.spring.io/spring-data/redis](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)
- **Rate Limiting Patterns**: [cloud.google.com/architecture/rate-limiting-strategies](https://cloud.google.com/architecture/rate-limiting-strategies-techniques)
- **Performance Tuning**: [redis.io/docs/manual/performance](https://redis.io/docs/manual/performance/)

For questions or issues, please refer to the project documentation or create an issue in the repository.
