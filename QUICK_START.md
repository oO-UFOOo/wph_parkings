# SOPA Backend - Quick Start Guide

## What's Been Implemented

### ✅ Database Tables

1. **app_users** - Two types of users (UFO & OpenEye)
2. **patrol_clients** - Patrol client entities
3. **qr_spots** - QR-coded patrol spots with GPS coordinates (latitude/longitude)
4. **timestamps** - General event audit log for all system activities
5. **patrol_spot_timestamps** - Specific check-in/check-out records for patrol spots

### ✅ Security

- Spring Security configured with HTTP Basic Auth
- **UFO User**: `ufo` / `ufo_password_123`
- **OpenEye User**: `openeye` / `openeye_password_123`
- All API endpoints require authentication except `/api/auth/**`

### ✅ Entities Created

- `AppUser.java` - Application user entity
- `PatrolClient.java` - Patrol client with 1-to-many relationship to Spot
- `Spot.java` - QR spot with location (longitude, latitude)
- `Timestamp.java` - General timestamp event log
- `PatrolSpotTimestamp.java` - Patrol activity log (check-in/out)

### ✅ Repositories

- `AppUserRepository` - CRUD + find by username
- `PatrolClientRepository` - CRUD operations
- `SpotRepository` - CRUD + find by QR code, find by client
- `TimestampRepository` - CRUD + find by event type, user, date range
- `PatrolSpotTimestampRepository` - CRUD + find by spot, client, date range

### ✅ Controllers (REST APIs)

- `AppUserController` - User management endpoints
- `PatrolClientController` - Patrol client CRUD
- `SpotController` - Spot CRUD with location and QR code lookups
- `TimestampController` - General timestamp logging
- `PatrolSpotTimestampController` - Patrol activity check-in/out

## Database Setup

### PostgreSQL Connection
```
Host: localhost
Port: 5432
Database: parkingdb
User: parkinguser
Password: parkingpass
```

### Initialize Sample Data
```bash
psql parkingdb -U parkinguser < scripts/init_sopa_data.sql
```

This will create sample data:
- 2 Patrol Clients (Downtown, North Zone)
- 5 QR Spots with GPS coordinates
- 2 App Users (ufo, openeye)

## API Testing

### Option 1: Using Postman
1. Import `postman_collection.json` into Postman
2. Set credentials for UFO or OpenEye user
3. Test endpoints

### Option 2: Using cURL
```bash
# Get all patrol clients (as UFO user)
curl -u ufo:ufo_password_123 http://localhost:8080/api/patrol-clients

# Create a new spot with location
curl -X POST http://localhost:8080/api/spots \
  -H "Content-Type: application/json" \
  -u openeye:openeye_password_123 \
  -d '{
    "spotName": "Park Entrance",
    "qrCode": "QR-NEW-001",
    "longitude": -73.950,
    "latitude": 40.765,
    "patrolClientId": 1,
    "description": "Main park entrance",
    "isActive": true
  }'

# Check in to a spot
curl -X POST http://localhost:8080/api/patrol-spot-timestamps \
  -H "Content-Type: application/json" \
  -u ufo:ufo_password_123 \
  -d '{
    "spotId": 1,
    "patrolClientId": 1,
    "status": "CHECKED_IN"
  }'

# Log a general event
curl -X POST http://localhost:8080/api/timestamps \
  -H "Content-Type: application/json" \
  -u openeye:openeye_password_123 \
  -d '{
    "eventType": "SPOT_CHECK",
    "description": "Completed spot inspection",
    "userId": 1,
    "entityType": "SPOT",
    "entityId": 1
  }'
```

### Option 3: Using IntelliJ HTTP Client
Create `.http` file in project root:
```http
### Get all patrol clients
GET http://localhost:8080/api/patrol-clients
Authorization: Basic dWZvOnVmb19wYXNzd29yZF8xMjM=

### Create patrol client
POST http://localhost:8080/api/patrol-clients
Authorization: Basic dWZvOnVmb19wYXNzd29yZF8xMjM=
Content-Type: application/json

{
  "clientName": "Test Client",
  "clientCode": "TC-001",
  "description": "Test",
  "isActive": true
}
```

## Key Features

### 1. User Authentication
- Two built-in users with different roles
- BCrypt password encoding
- HTTP Basic Authentication

### 2. Patrol Client Management
- Create patrol clients
- Each client can have multiple spots
- Active/inactive status

### 3. QR Spot Management
- Unique QR code identification
- GPS location (latitude/longitude)
- Associated with a patrol client
- Lookup by QR code

### 4. General Timestamps
- Log any type of event (LOGIN, SPOT_CHECK, UPDATE, etc.)
- Track entity type and ID that triggered the event
- Search by event type, user, or date range

### 5. Patrol Spot Timestamps
- Track check-ins and check-outs
- Record notes and status
- Query by spot, client, or date range
- Automatic timestamp on creation

## Common API Response Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Request succeeded |
| 201 | Created - Resource created successfully |
| 204 | No Content - Delete successful |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Permission denied |
| 404 | Not Found - Resource not found |
| 500 | Internal Server Error |

## Relationship Diagram

```
AppUser
├── username (UFO or OPENEYE)
└── appType

PatrolClient
├── clientName
└── Spots (ONE-TO-MANY)
    ├── spotName
    ├── qrCode (unique)
    ├── longitude
    ├── latitude
    └── PatrolSpotTimestamps
        ├── checkInTimestamp
        ├── checkOutTimestamp
        └── status

Timestamp (Independent)
├── eventType
├── eventTimestamp
└── entityId (points to any entity)
```

## Next Steps

1. **Test the API** using Postman or cURL
2. **Verify database** tables were created
3. **Review logs** for any Spring Security warnings
4. **Add custom business logic** as needed
5. **Deploy to Docker** when ready

## Troubleshooting

### Issue: "401 Unauthorized"
- Check username/password
- Ensure user has required role
- Verify Basic Auth header is correct

### Issue: "404 Not Found"
- Verify resource exists
- Check endpoint URL spelling
- Ensure ID exists in database

### Issue: Database connection error
- Verify PostgreSQL is running
- Check connection string in `application.properties`
- Verify credentials are correct

### Issue: Port 8080 already in use
- Change `server.port` in `application.properties`
- Or kill process using port 8080

---

**Ready to run!** Start your application and test the endpoints.
