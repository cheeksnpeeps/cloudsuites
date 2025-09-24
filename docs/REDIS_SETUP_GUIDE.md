# Redis Setup Guide for CloudSuites Agents

## 🚀 Quick Start

### 1. Start All Services
```bash
# Start PostgreSQL + Redis + Application
docker-compose up --build -d

# Check all services are healthy
docker-compose ps
```

### 2. Verify Redis Connection
```bash
# Test Redis connectivity
docker exec cloudsuites-redis redis-cli -a csRedisPass123 ping
# Expected output: PONG

# Check Redis info
docker exec cloudsuites-redis redis-cli -a csRedisPass123 INFO server
```

### 3. Test Rate Limiting
```bash
# Run rate limiting tests
mvn test -s .mvn/settings.xml -pl modules/auth-module -Dtest="RedisRateLimitService*"
# Expected: All tests pass (10/10 unit tests, 8/8 integration tests)
```

## ⚙️ Configuration Quick Reference

### Environment Variables (`.env`)
```bash
# Redis - Core settings agents need to know
REDIS_HOST=redis                    # Docker service name
REDIS_PASSWORD=csRedisPass123       # Authentication
RATE_LIMITING_ENABLED=true          # Enable rate limiting
RATE_LIMIT_LOGIN_ATTEMPTS=5         # Login attempts before block
```

### Key Rate Limiting Operations
| Operation | Default Limit | Window | Lockout |
|-----------|--------------|--------|---------|
| `login` | 5 attempts | 15 min | Yes |
| `otp_send` | 3 attempts | 5 min | No |
| `otp_verify` | 5 attempts | 5 min | Yes |
| `api_access` | 100 attempts | 1 min | No |

## 🛠️ Agent Commands

### Check Rate Limiting Status
```bash
# View rate limit keys in Redis
docker exec cloudsuites-redis redis-cli -a csRedisPass123 KEYS "rate:*"

# Check specific user's login attempts
docker exec cloudsuites-redis redis-cli -a csRedisPass123 GET "rate:login:user@example.com"

# Clear rate limits for a user (emergency)
docker exec cloudsuites-redis redis-cli -a csRedisPass123 DEL "rate:login:user@example.com"
```

### Monitor Performance
```bash
# Check Redis memory usage
docker exec cloudsuites-redis redis-cli -a csRedisPass123 INFO memory | grep used_memory_human

# Monitor connection pool
docker-compose logs cloudsuites-app | grep -i "redis pool"

# Check if fallback mode is active
docker-compose logs cloudsuites-app | grep -i "fallback"
```

### Debugging Common Issues
```bash
# Redis not starting
docker-compose logs redis
docker-compose restart redis

# Rate limiting not working  
docker-compose logs cloudsuites-app | grep -i "rate limit"

# High memory usage
docker exec cloudsuites-redis redis-cli -a csRedisPass123 FLUSHDB  # ⚠️ CAUTION: Clears all data
```

## 🧪 Testing Scenarios

### Test Rate Limiting Manually
```bash
# Use the test API script
./test-all-apis.sh

# Or test specific endpoint multiple times
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/v1/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com","password":"wrong"}' \
    -w "\nStatus: %{http_code}\n"
done
# Should see HTTP 429 (Too Many Requests) after 5 attempts
```

### Validate Fallback Mode
```bash
# Stop Redis to test fallback
docker-compose stop redis

# Make requests - should still work with in-memory fallback
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"wrong"}'

# Check logs for fallback activation
docker-compose logs cloudsuites-app | tail -20

# Restart Redis
docker-compose start redis
```

## 🔧 Agent Troubleshooting

### Issue: "Redis connection failed"
```bash
# 1. Check Redis container status
docker-compose ps redis

# 2. Verify Redis logs
docker-compose logs redis | tail -10

# 3. Test connection manually  
docker exec cloudsuites-redis redis-cli -a csRedisPass123 ping

# 4. Restart if needed
docker-compose restart redis
```

### Issue: "Rate limiting not working"
```bash
# 1. Check configuration
docker-compose logs cloudsuites-app | grep -i "rate limit config"

# 2. Verify Redis keys are being created
docker exec cloudsuites-redis redis-cli -a csRedisPass123 MONITOR
# Make a request and watch for SET commands

# 3. Check environment variables
docker exec cloudsuites-app env | grep RATE_LIMIT
```

### Issue: "High memory usage"
```bash
# 1. Check Redis memory
docker exec cloudsuites-redis redis-cli -a csRedisPass123 INFO memory

# 2. See key distribution
docker exec cloudsuites-redis redis-cli -a csRedisPass123 --scan --pattern "rate:*" | head -10

# 3. Clean up if needed (⚠️ Will clear rate limit data)
docker exec cloudsuites-redis redis-cli -a csRedisPass123 FLUSHDB
```

## 🚨 Emergency Procedures

### Disable Rate Limiting Quickly
```bash
# 1. Set environment variable
echo "RATE_LIMITING_ENABLED=false" >> .env

# 2. Restart application
docker-compose restart cloudsuites-app

# 3. Verify disabled
docker-compose logs cloudsuites-app | grep -i "rate limiting disabled"
```

### Reset All Rate Limits
```bash
# ⚠️ CAUTION: This clears ALL rate limiting data
docker exec cloudsuites-redis redis-cli -a csRedisPass123 EVAL "
return redis.call('del', unpack(redis.call('keys', 'rate:*')))
" 0
```

### Full Redis Reset
```bash
# ⚠️ CAUTION: Complete data loss
docker-compose down
docker volume rm cloudsuites_redis_data  
docker-compose up --build -d
```

## 📋 Agent Checklist

When working with rate limiting features:

- [ ] ✅ Redis container is running and healthy
- [ ] ✅ Application can connect to Redis (check logs)
- [ ] ✅ Rate limiting is enabled in configuration  
- [ ] ✅ Environment variables are properly set
- [ ] ✅ Tests are passing (unit + integration)
- [ ] ✅ Fallback mode works when Redis unavailable
- [ ] ✅ Rate limits are being enforced correctly
- [ ] ✅ Performance is acceptable under load

## 🔗 Quick Links

- **Full Documentation**: [docs/REDIS_RATE_LIMITING.md](./REDIS_RATE_LIMITING.md)
- **Configuration**: [application.yml](../contributions/core-webapp/src/main/resources/application.yml)
- **Environment**: [.env](../.env)  
- **Docker Setup**: [compose.yaml](../compose.yaml)
- **Implementation**: `modules/auth-module/src/main/java/.../RedisRateLimitServiceImpl.java`
- **Tests**: `modules/auth-module/src/test/java/.../RedisRateLimitService*Test.java`

---
**Need help?** Check the full documentation or search application logs for specific error messages.
