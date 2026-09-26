# 🚀 SOPA Backend - VM Deployment Guide

## Quick Start (VM)

### Option 1: Git Pull + Docker Compose (Recommended)

```bash
# Step 1: Navigate to project
cd /path/to/sopa-backend

# Step 2: Pull latest code
git pull origin main

# Step 3: Build and run with Docker
docker-compose up --build

# Wait for startup (~60 seconds)
# Access at: http://localhost:8080/
```

### Option 2: Git Pull + Maven (If Docker not available)

```bash
# Step 1: Navigate to project
cd /path/to/sopa-backend

# Step 2: Pull latest code
git pull origin main

# Step 3: Clean build
mvn clean package

# Step 4: Run
mvn spring-boot:run

# Access at: http://localhost:8080/
```

### Option 3: Just Docker Compose (If files already there)

```bash
# If you already pulled the code before:
docker-compose up --build

# Or rebuild without cache:
docker-compose down -v
docker-compose up --build
```

---

## Complete Step-by-Step (VM Setup)

### 1. First Time Setup

```bash
# Clone or navigate to project
cd /home/user/sopa-backend  # or your path

# Pull latest
git pull origin main

# Install dependencies (if needed)
# Docker: Already included
# Maven: sudo apt-get install maven

# Build and run
docker-compose up --build
```

### 2. Check if Running

```bash
# In another terminal/tab
curl -u ufo:ufo_password_123 http://localhost:8080/api/users

# Should return: 200 OK with user list
```

### 3. Access Login Form

```
Open browser:
http://localhost:8080/

NOT: https://sopa-openeye-security.gr
```

---

## What's in Latest Pull

### New Files:
```
✅ index.html (professional login form with SOPA logo)
✅ dashboard.html (user dashboard)
✅ Updated application.properties (cache busting)
✅ Fixed URLs (http://localhost:8080 absolute URLs)
✅ application-docker.properties (docker config)
```

### Updated Files:
```
✅ Dockerfile (improved with health checks)
✅ docker-compose.yml (with proper wait conditions)
✅ All frontend URLs fixed to use localhost
```

---

## Docker Compose Explanation

### What `docker-compose up --build` does:

```
1. Builds Docker image from Dockerfile
2. Starts PostgreSQL container
3. Waits for PostgreSQL to be healthy
4. Starts Spring Boot container
5. Waits for app to be healthy
6. All containers running

Time: ~60 seconds
Result: http://localhost:8080/ ready
```

### Check Container Status:

```bash
docker-compose ps

# Should show:
# NAME          STATUS
# sopa-db       Up X seconds (healthy)
# sopa-backend  Up X seconds (healthy)
```

### View Logs:

```bash
# All logs
docker-compose logs -f

# Just app logs
docker-compose logs -f app

# Just database logs
docker-compose logs -f db
```

---

## Demo Credentials (After Startup)

```
UFO:      ufo / ufo_password_123
OpenEye:  openeye / openeye_password_123
```

### Quick Test:
```bash
# Terminal
curl -u ufo:ufo_password_123 http://localhost:8080/api/users

# Browser
http://localhost:8080/
Press Ctrl+1 (auto-fill UFO)
Click Login
See Dashboard ✅
```

---

## Troubleshooting

### Port Already in Use
```bash
# Find and kill process on 8080
lsof -i :8080
kill -9 <PID>

# Or use Docker to stop
docker-compose down
docker-compose up --build
```

### Database Connection Error
```bash
# Check database status
docker-compose logs db

# Restart everything
docker-compose down -v
docker-compose up --build
```

### Can't Access localhost:8080
```bash
# Check if containers running
docker-compose ps

# Check logs
docker-compose logs app

# Verify health check
docker exec sopa-backend curl http://localhost:8080/api/users
```

---

## For Your VM Specifically

### If Using Docker on VM:
```bash
git pull origin main
docker-compose up --build

# Access from another machine:
http://your-vm-ip:8080/
```

### If VM Firewall Enabled:
```bash
# Open port 8080
sudo ufw allow 8080

# Or for specific IPs
sudo ufw allow from 192.168.x.x to any port 8080
```

### SSH into VM and Access:
```bash
# From your machine
ssh user@vm-ip

# On VM terminal
cd /path/to/sopa-backend
git pull origin main
docker-compose up --build

# From your machine (new terminal)
curl -u ufo:ufo_password_123 http://vm-ip:8080/api/users
```

---

## Quick Decision Tree

### "What do I do?"

1. **If you have Docker installed:**
   ```bash
   git pull origin main
   docker-compose up --build
   ```

2. **If you only have Java/Maven:**
   ```bash
   git pull origin main
   mvn clean package
   java -jar target/*.jar
   ```

3. **If you're not sure what you have:**
   ```bash
   # Check Docker
   docker --version

   # Check Maven
   mvn --version

   # Check Java
   java -version
   ```

---

## Common Commands

```bash
# Pull latest code
git pull origin main

# View status
git status

# Check Docker containers
docker ps -a

# Stop containers
docker-compose down

# Remove volumes and restart
docker-compose down -v
docker-compose up --build

# View application logs
docker-compose logs -f app

# Access database
docker exec -it sopa-db psql -U parkinguser -d parkingdb

# Access app container shell
docker exec -it sopa-backend bash
```

---

## Final Checklist

After running the command:

- [ ] Git pulled successfully (no errors)
- [ ] Docker building (or Maven building)
- [ ] Containers started (or JAR running)
- [ ] "Started Parkings" message in logs
- [ ] Can access http://localhost:8080/
- [ ] See professional SOPA login form (NOT auth popup)
- [ ] Ctrl+1 works (auto-fills UFO)
- [ ] Login successful
- [ ] Dashboard appears

All checked? = **DEPLOYMENT SUCCESSFUL!** ✅

---

## 📞 Still Having Issues?

### Database Issues:
```bash
docker-compose down -v  # Remove volumes
docker-compose up --build
```

### Port Issues:
```bash
docker-compose down
# Kill any port 8080 processes
docker-compose up --build
```

### URL Issues:
```
Remember: http://localhost:8080/
NOT: https://sopa-openeye-security.gr
```

---

**Status: ✅ Ready for VM Deployment**

Use the appropriate command for your setup!
