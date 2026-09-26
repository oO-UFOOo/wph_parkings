# Fix Login - Database Seeding

## Problem
Login was failing because the `app_users` table was empty. The credentials `ufo/ufo_password_123` and `openeye/openeye_password_123` didn't exist in the database.

## Solution
I added a `DatabaseInitializer` that automatically creates these users when the app starts.

## What to do on your VM

### Step 1: Pull the latest code
```bash
cd /path/to/sopa-backend
git pull origin main
```

### Step 2: Rebuild and restart
```bash
docker compose up --build -d
```

Or with Maven:
```bash
mvn clean package
mvn spring-boot:run
```

### Step 3: Check logs for initialization
```bash
docker compose logs app | grep "Database initialization"
```

You should see:
```
Initializing database with demo users...
✓ Created UFO user
✓ Created OpenEye user
✓ Database initialization complete!
```

### Step 4: Try login again
Go to: `https://sopa-openeye-security.gr/`

Use credentials:
- **UFO**: `ufo` / `ufo_password_123`
- **OpenEye**: `openeye` / `openeye_password_123`

---

## What was added

### New file: `DatabaseInitializer.java`
- Location: `src/main/java/com/sopa/config/DatabaseInitializer.java`
- This runs automatically on app startup
- Creates demo users if they don't exist
- Uses the same password encoder as the security config

### How it works
1. App starts → Spring runs all `CommandLineRunner` beans
2. DatabaseInitializer checks if users already exist
3. If empty, creates UFO and OpenEye users with hashed passwords
4. If users exist, skips (safe for re-deployment)

---

## If you want to manually add users via API

Once the app is running, you can also POST new users:

```bash
curl -X POST https://sopa-openeye-security.gr/api/users \
  -u ufo:ufo_password_123 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "newpass123",
    "appType": "UFO",
    "isActive": true
  }'
```

---

## Troubleshooting

### Login still fails after restart
1. Check logs:
   ```bash
   docker compose logs app | grep -i "error\|exception"
   ```

2. Check if users were created:
   ```bash
   docker compose exec db psql -U parkinguser -d parkingdb -c "SELECT * FROM app_users;"
   ```

3. If table is empty, database init didn't run — check:
   ```bash
   docker compose logs app | head -50
   ```

### Reset users (delete and recreate)
```bash
docker compose exec db psql -U parkinguser -d parkingdb -c "DELETE FROM app_users;"
docker compose restart app
```

Then check logs again to see initialization run.

---

## Next step
After users are seeded, you should be able to login and see the dashboard! 🎉
