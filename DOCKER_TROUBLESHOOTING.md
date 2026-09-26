# Docker Deployment - Troubleshooting & Configuration Guide

## Issue: `NullPointerException: Cannot invoke "org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert()"`

### Root Cause
This error typically occurs when:
1. The database container hasn't finished initializing when the app tries to connect
2. Connection pool issues or database connection timeouts
3. JDBC driver not properly initialized
4. Hostname resolution issues in Docker network

### Solution Implemented

#### 1. **Updated docker-compose.yml**
- Added `depends_on` with `service_healthy` condition
- Added PostgreSQL health check with `pg_isready`
- Added connection timeout parameters to JDBC URL
- Configured proper Docker networking
- Added app health check

#### 2. **Enhanced application.properties**
- Added HikariCP connection pool settings
- Set connection timeout: 30 seconds
- Added socket timeout configuration
- Improved Hibernate configuration
- Added connection validation query

#### 3. **Created application-docker.properties**
- Docker-specific database URL pointing to `db` hostname
- Optimized connection pooling
- Reduced logging verbosity for production
- Added TCP keep-alives

#### 4. **Improved Dockerfile**
- Added health check
- Optimized JVM options for Docker (G1GC, reduced heap)
- Added curl for health checks
- Used Alpine Linux for smaller image size

---

## How to Deploy Correctly

### Option 1: Build and Run (Recommended)
```bash
cd C:\Users\UfOo\IdeaProjects\sopa-backend

# Clean build
mvn clean package

# Start with Docker Compose
docker compose up --build
```

### Option 2: Without Rebuild
```bash
# Just start containers (if already built)
docker compose up
```

### Option 3: Rebuild Specific Service
```bash
# Rebuild only the app container
docker compose build app --no-cache
docker compose up
```

---

## Verifying Deployment

### Check Container Status
```bash
docker compose ps
```

Expected output:
```
NAME                  STATUS
sopa-db              Up X seconds (healthy)
sopa-backend         Up X seconds (healthy)
```

### Check Logs
```bash
# View all logs
docker compose logs -f

# View only app logs
docker compose logs -f app

# View only database logs
docker compose logs -f db
```

### Test API
```bash
# Wait 40+ seconds after startup, then test
curl -u ufo:ufo_password_123 http://localhost:8080/api/users

# Expected response: 200 OK with user list
```

---

## Connection String Analysis

### Local Development
```
jdbc:postgresql://localhost:5432/parkingdb
```

### Docker Environment
```
jdbc:postgresql://db:5432/parkingdb?connectTimeout=30&socketTimeout=30
```

**Key differences:**
- `localhost` → `db` (Docker service name)
- Added `connectTimeout=30` - Wait up to 30 seconds
- Added `socketTimeout=30` - Socket operations timeout

---

## Common Issues & Solutions

### Issue 1: "Cannot connect to PostgreSQL"
```
ERROR: NullPointerException in Hibernate
```

**Solutions:**
1. Verify PostgreSQL container is running: `docker compose ps`
2. Check PostgreSQL health: `docker compose logs db`
3. Increase `connectTimeout` in JDBC URL
4. Add waiting mechanism before app startup

**Fix applied:** Added health check to docker-compose.yml

---

### Issue 2: "Connection timeout after 30 seconds"
```
ERROR: org.postgresql.util.PSQLException: Connection timeout
```

**Solutions:**
1. Check PostgreSQL is fully initialized: `docker exec sopa-db psql -U parkinguser -d parkingdb -c "SELECT 1;"`
2. Increase HikariCP `connection-timeout` setting
3. Check Docker network connectivity
4. Verify database credentials

**Fix applied:** Health checks ensure startup order

---

### Issue 3: "Too many connections"
```
ERROR: too many connections for role "parkinguser"
```

**Solutions:**
1. Reduce HikariCP `maximum-pool-size` (default now 20)
2. Close idle connections faster
3. Check for connection leaks
4. Increase PostgreSQL `max_connections`

**Fix applied:** Added proper connection pool settings + PostgreSQL max_connections=200

---

### Issue 4: "Database lock timeout"
```
ERROR: Timeout: bitwise operation exceeded
```

**Solutions:**
1. Increase connection timeout
2. Reduce batch size temporarily
3. Check for long-running queries
4. Verify Hibernate transaction settings

**Fix applied:** Added `spring.jpa.properties.hibernate.order_inserts=true`

---

## Configuration Files Reference

### application.properties (Local Development)
- Located: `src/main/resources/application.properties`
- Uses: `localhost` for database
- For: Running locally with `mvn spring-boot:run`
- Connection pool: 20 max, 5 min idle

### application-docker.properties (Docker Container)
- Located: `src/main/resources/application-docker.properties`
- Uses: `db` hostname (Docker internal)
- For: Running in Docker container
- Activated by: `spring.profiles.active=docker` in docker-compose.yml

---

## Port Mappings

| Service | Container Port | Host Port | Purpose |
|---------|---|---|---|
| PostgreSQL | 5432 | 5432 | Database access |
| Spring Boot App | 8080 | 8080 | REST API |

Access from host machine:
- Database: `localhost:5432`
- API: `http://localhost:8080`

---

## Environment Variables in Docker

Set in `docker-compose.yml`:
```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/parkingdb
  SPRING_DATASOURCE_USERNAME: parkinguser
  SPRING_DATASOURCE_PASSWORD: parkingpass
  SPRING_JPA_HIBERNATE_DDL_AUTO: update
```

These override values in `application.properties`

---

## Health Checks Explained

### PostgreSQL Health Check
```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U parkinguser -d parkingdb"]
  interval: 5s        # Check every 5 seconds
  timeout: 5s         # Wait max 5 seconds for response
  retries: 5          # Fail after 5 failed checks
```

### Spring Boot Health Check
```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/api/users"]
  start_period: 40s   # Wait 40 seconds before first check
  interval: 30s       # Check every 30 seconds
  retries: 3          # Fail after 3 failed checks
```

---

## Startup Sequence

```
1. Docker Compose starts
2. PostgreSQL container starts
3. PostgreSQL initializes (creates database, users)
4. PostgreSQL health check passes
5. Spring Boot app container starts (depends_on: service_healthy)
6. Spring Boot waits for PostgreSQL to be ready
7. Hibernate creates/updates tables
8. Application starts listening on port 8080
9. App health check begins (after 40s startup period)
10. Application is ready to receive requests
```

**Total startup time:** 30-60 seconds depending on machine

---

## Database Initialization

### Automatic (via Hibernate)
- `spring.jpa.hibernate.ddl-auto=update` creates/updates schema
- Tables are created from entity classes automatically
- No manual SQL needed for schema

### Optional Manual Data Loading
```bash
# Load sample data after startup
docker exec sopa-db psql -U parkinguser -d parkingdb < scripts/init_sopa_data.sql
```

---

## Performance Tuning

### For Development
- Keep defaults in application.properties
- Good balance of speed and resource usage

### For Production
```properties
# Increase pool size if handling high load
spring.datasource.hikari.maximum-pool-size=50

# Reduce verbosity
logging.level.root=ERROR
logging.level.com.sopa=WARN

# Use read replicas if available
# spring.datasource.hikari.read-only=false
```

---

## Monitoring in Docker

### View Real-time Logs
```bash
docker compose logs -f --timestamps
```

### Container Stats
```bash
docker stats
```

### Database Status
```bash
docker exec sopa-db psql -U parkinguser -d parkingdb -c "\conninfo"
```

### Connection Pool Status
Look for log lines with `HikariPool` information

---

## Cleanup & Reset

### Stop Everything
```bash
docker compose down
```

### Remove All Data
```bash
docker compose down -v
```

### Rebuild Everything
```bash
docker compose down -v
docker system prune
mvn clean package
docker compose up --build
```

---

## Quick Reference Commands

```bash
# Start
docker compose up --build

# Stop
docker compose down

# View logs
docker compose logs -f app

# Check status
docker compose ps

# Database shell
docker exec -it sopa-db psql -U parkinguser -d parkingdb

# App shell
docker exec -it sopa-backend bash

# Test API
curl -u ufo:ufo_password_123 http://localhost:8080/api/users
```

---

## Verification Checklist

- [ ] Docker and Docker Compose installed
- [ ] Project built with `mvn clean package`
- [ ] `docker compose up --build` starts without errors
- [ ] Both containers show as "Up" and "healthy" in `docker compose ps`
- [ ] App logs show "Started Parkings" message
- [ ] Can curl the API: `curl -u ufo:ufo_password_123 http://localhost:8080/api/users`
- [ ] Response is 200 OK with user data
- [ ] Can import Postman collection and test endpoints

---

## Need Help?

1. Check logs: `docker compose logs -f`
2. Verify containers: `docker compose ps`
3. Check connectivity: `docker exec sopa-db pg_isready -U parkinguser`
4. Check Java startup: `docker logs sopa-backend | head -50`

---

**Last Updated:** September 26, 2026
**Version:** 1.0.0
**Status:** Ready for Docker deployment
