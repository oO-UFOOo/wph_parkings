# SOPA Backend - Visual Quick Reference

## 📋 Database Schema at a Glance

```
┌─────────────────────────────────────────────────────────────────┐
│                     SOPA BACKEND DATABASE                       │
│                        (parkingdb)                              │
└─────────────────────────────────────────────────────────────────┘

TABLE: app_users
┌──────────────────────────────────┐
│ id (PK)                          │
│ username: "ufo" / "openeye"      │
│ password: (hashed)               │
│ app_type: "UFO" / "OPENEYE"      │
│ is_active: true/false            │
└──────────────────────────────────┘

TABLE: patrol_clients
┌──────────────────────────────────┐
│ id (PK)                          │
│ client_name: "Downtown Patrol"   │
│ client_code: "DT-001"            │
│ description: Text                │
│ is_active: true/false            │
└────────────┬──────────────────────┘
             │
             │ (1-to-Many)
             │
             ▼
TABLE: qr_spots
┌──────────────────────────────────┐
│ id (PK)                          │
│ spot_name: "Main Street Corner"  │
│ qr_code: "QR-DT-001" (unique)    │
│ longitude: -73.935242            │
│ latitude: 40.730610              │
│ patrol_client_id (FK)            │
│ description: Text                │
│ is_active: true/false            │
└────────────┬──────────────────────┘
             │
             │ (1-to-Many)
             │
             ▼
TABLE: patrol_spot_timestamps
┌──────────────────────────────────┐
│ id (PK)                          │
│ spot_id (FK)                     │
│ patrol_client_id (FK)            │
│ check_in_timestamp: DateTime     │
│ check_out_timestamp: DateTime    │
│ notes: Text                      │
│ status: "CHECKED_IN" / "CHECKED_│
│         OUT" / "COMPLETED"       │
└──────────────────────────────────┘

TABLE: timestamps (Independent Audit Log)
┌──────────────────────────────────┐
│ id (PK)                          │
│ event_type: "LOGIN"              │
│            "SPOT_CHECK"          │
│            "UPDATE" etc.         │
│ description: Text                │
│ event_timestamp: DateTime        │
│ user_id: Long                    │
│ entity_type: "SPOT"              │
│            "PATROL_CLIENT" etc.  │
│ entity_id: Long                  │
└──────────────────────────────────┘
```

---

## 🔐 Authentication Flow

```
CLIENT REQUEST
    │
    │ HTTP Basic Auth Header
    │ (username:password in Base64)
    │
    ▼
SPRING SECURITY FILTER CHAIN
    │
    ├─► Decode credentials
    │   (from Authorization header)
    │
    ├─► Load user from UserDetailsService
    │   (ufo or openeye)
    │
    ├─► Verify password
    │   (BCryptPasswordEncoder.matches())
    │
    ├─ Valid ──► Grant Authentication
    │            (set GrantedAuthority)
    │            │
    │            ▼
    │        ALLOW REQUEST TO PROCEED
    │        (to controller)
    │
    └─ Invalid ──► Return 401 Unauthorized
```

---

## 📱 API Endpoints Quick Map

```
/api/users
├── GET /api/users                    → Get all users
├── GET /api/users/{id}               → Get user by ID
├── GET /api/users/username/{username}→ Get user by username
├── POST /api/users                   → Create user
├── PUT /api/users/{id}               → Update user
└── DELETE /api/users/{id}            → Delete user

/api/patrol-clients
├── GET /api/patrol-clients           → Get all clients
├── GET /api/patrol-clients/{id}      → Get client by ID
├── POST /api/patrol-clients          → Create client
├── PUT /api/patrol-clients/{id}      → Update client
└── DELETE /api/patrol-clients/{id}   → Delete client

/api/spots
├── GET /api/spots                    → Get all spots
├── GET /api/spots/{id}               → Get spot by ID
├── GET /api/spots/qr/{qrCode}       → Get spot by QR code
├── GET /api/spots/client/{clientId}  → Get spots by client
├── POST /api/spots                   → Create spot (with GPS)
├── PUT /api/spots/{id}               → Update spot
└── DELETE /api/spots/{id}            → Delete spot

/api/timestamps
├── GET /api/timestamps               → Get all events
├── GET /api/timestamps/{id}          → Get event by ID
├── GET /api/timestamps/event/{type}  → Get by event type
├── GET /api/timestamps/user/{userId} → Get by user
├── GET /api/timestamps/range         → Get by date range
├── POST /api/timestamps              → Log new event
└── DELETE /api/timestamps/{id}       → Delete event

/api/patrol-spot-timestamps
├── GET /api/patrol-spot-timestamps   → Get all activities
├── GET /api/patrol-spot-timestamps/{id} → Get by ID
├── GET /api/patrol-spot-timestamps/spot/{spotId} → Get spot activities
├── GET /api/patrol-spot-timestamps/client/{clientId} → Get client activities
├── GET /api/patrol-spot-timestamps/range → Get by date range
├── POST /api/patrol-spot-timestamps  → Check-in to spot
├── PUT /api/patrol-spot-timestamps/{id} → Update/check-out
└── DELETE /api/patrol-spot-timestamps/{id} → Delete
```

---

## 💬 Example: Complete Check-in Workflow

### Step 1: Authenticate as UFO User
```bash
curl -u ufo:ufo_password_123 \
  http://localhost:8080/api/users
```

### Step 2: Get Available Spots
```bash
curl -u ufo:ufo_password_123 \
  http://localhost:8080/api/spots
```

### Step 3: Check-in to a Spot
```bash
curl -X POST http://localhost:8080/api/patrol-spot-timestamps \
  -H "Content-Type: application/json" \
  -u ufo:ufo_password_123 \
  -d '{
    "spotId": 1,
    "patrolClientId": 1,
    "status": "CHECKED_IN"
  }'
```

### Step 4: Log the Event
```bash
curl -X POST http://localhost:8080/api/timestamps \
  -H "Content-Type: application/json" \
  -u ufo:ufo_password_123 \
  -d '{
    "eventType": "SPOT_CHECK",
    "description": "Check-in at Main Street Corner",
    "userId": 1,
    "entityType": "SPOT",
    "entityId": 1
  }'
```

### Step 5: Get Check-in History
```bash
curl -u ufo:ufo_password_123 \
  http://localhost:8080/api/patrol-spot-timestamps/spot/1
```

---

## 🗺️ Create Spot with GPS Example

### Create a Spot at Times Square
```bash
curl -X POST http://localhost:8080/api/spots \
  -H "Content-Type: application/json" \
  -u openeye:openeye_password_123 \
  -d '{
    "spotName": "Times Square",
    "qrCode": "QR-TS-001",
    "longitude": -73.985130,
    "latitude": 40.758896,
    "patrolClientId": 1,
    "description": "Times Square patrol zone",
    "isActive": true
  }'
```

### Response (201 Created)
```json
{
  "id": 3,
  "spotName": "Times Square",
  "qrCode": "QR-TS-001",
  "longitude": -73.985130,
  "latitude": 40.758896,
  "patrolClientId": 1,
  "description": "Times Square patrol zone",
  "isActive": true
}
```

### Now retrieve by QR code
```bash
curl -u openeye:openeye_password_123 \
  http://localhost:8080/api/spots/qr/QR-TS-001
```

---

## 📅 Date Range Query Example

### Query Timestamps Between Dates
```bash
curl -u openeye:openeye_password_123 \
  "http://localhost:8080/api/timestamps/range?startTime=2026-09-26T00:00:00&endTime=2026-09-26T23:59:59"
```

### Query Patrol Activities Between Dates
```bash
curl -u ufo:ufo_password_123 \
  "http://localhost:8080/api/patrol-spot-timestamps/range?startTime=2026-09-26T08:00:00&endTime=2026-09-26T17:00:00"
```

---

## 🚀 Startup Commands

### Initialize Database
```bash
psql -U parkinguser -d parkingdb < scripts/init_sopa_data.sql
```

### Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### Docker Compose
```bash
docker compose up --build
```

---

## 🔍 Testing Checklist

### Test as UFO User
```bash
curl -u ufo:ufo_password_123 http://localhost:8080/api/users
curl -u ufo:ufo_password_123 http://localhost:8080/api/patrol-clients
curl -u ufo:ufo_password_123 http://localhost:8080/api/spots
curl -u ufo:ufo_password_123 http://localhost:8080/api/patrol-spot-timestamps
curl -u ufo:ufo_password_123 http://localhost:8080/api/timestamps
```

### Test as OpenEye User
```bash
curl -u openeye:openeye_password_123 http://localhost:8080/api/users
curl -u openeye:openeye_password_123 http://localhost:8080/api/patrol-clients
curl -u openeye:openeye_password_123 http://localhost:8080/api/spots
curl -u openeye:openeye_password_123 http://localhost:8080/api/patrol-spot-timestamps
curl -u openeye:openeye_password_123 http://localhost:8080/api/timestamps
```

### Test Create Operations
```bash
# Create Patrol Client
curl -X POST http://localhost:8080/api/patrol-clients \
  -H "Content-Type: application/json" \
  -u openeye:openeye_password_123 \
  -d '{"clientName":"Test","clientCode":"TEST","isActive":true}'

# Create Spot with GPS
curl -X POST http://localhost:8080/api/spots \
  -H "Content-Type: application/json" \
  -u openeye:openeye_password_123 \
  -d '{"spotName":"Test","qrCode":"QR-TEST","longitude":-73.935,"latitude":40.730,"patrolClientId":1,"isActive":true}'

# Check-in
curl -X POST http://localhost:8080/api/patrol-spot-timestamps \
  -H "Content-Type: application/json" \
  -u ufo:ufo_password_123 \
  -d '{"spotId":1,"patrolClientId":1,"status":"CHECKED_IN"}'

# Log Event
curl -X POST http://localhost:8080/api/timestamps \
  -H "Content-Type: application/json" \
  -u openeye:openeye_password_123 \
  -d '{"eventType":"SPOT_CHECK","description":"Test","userId":1,"entityType":"SPOT","entityId":1}'
```

---

## 🎯 User Credentials Reference

| User | Username | Password | Role |
|------|----------|----------|------|
| UFO | `ufo` | `ufo_password_123` | UFO_USER |
| OpenEye | `openeye` | `openeye_password_123` | OPENEYE_USER |

---

## 📍 GPS Coordinate Examples

| Location | Latitude | Longitude |
|----------|----------|-----------|
| Times Square, NYC | 40.758896 | -73.985130 |
| Central Park, NYC | 40.781776 | -73.965244 |
| Main Street, NYC | 40.730610 | -73.935242 |
| Equator | 0.000000 | 0.000000 |
| North Pole | 90.000000 | 0.000000 |
| South Pole | -90.000000 | 0.000000 |

---

## 📁 Key Files Location

| Purpose | File Path |
|---------|-----------|
| API Docs | `/SOPA_IMPLEMENTATION.md` |
| Quick Start | `/QUICK_START.md` |
| Architecture | `/ARCHITECTURE.md` |
| Database Init | `/scripts/init_sopa_data.sql` |
| Postman Collection | `/postman_collection.json` |
| Security Config | `src/main/java/com/sopa/controller/SecurityConfig.java` |

---

## ✨ That's It!

Your SOPA backend is ready to use. Choose your starting point:
- 🚀 **Quick Start:** See `QUICK_START.md`
- 📚 **Full Docs:** See `SOPA_IMPLEMENTATION.md`
- 🏗️ **Architecture:** See `ARCHITECTURE.md`
- 🧪 **Testing:** Import `postman_collection.json` into Postman

---

**Happy patrolling! 🚁**
