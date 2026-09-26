# SOPA Backend - OpenEye Extension

## Overview
This is a Spring Boot backend for the SOPA (Surveillance and Patrol Operations Application) with OpenEye integration. The system tracks patrol clients, their assigned QR code spots with location data, and timestamps for all activities.

## Database Schema

### Tables

#### 1. **app_users**
Stores application users with different roles (UFO and OPENEYE)
```sql
- id (Long, PK)
- username (String, unique)
- password (String, encrypted)
- app_type (String): "UFO" or "OPENEYE"
- is_active (Boolean)
```

#### 2. **patrol_clients**
Stores patrol client entities (each can have multiple spots)
```sql
- id (Long, PK)
- client_name (String)
- client_code (String)
- description (String)
- is_active (Boolean)
```

#### 3. **qr_spots**
Stores patrol spots with location data and QR codes
```sql
- id (Long, PK)
- spot_name (String)
- qr_code (String, unique)
- longitude (Double)
- latitude (Double)
- patrol_client_id (Long, FK → patrol_clients)
- description (String)
- is_active (Boolean)
```

#### 4. **timestamps**
General event/audit log for all activities
```sql
- id (Long, PK)
- event_type (String): "LOGIN", "SPOT_CHECK", "UPDATE", etc.
- description (String)
- event_timestamp (LocalDateTime)
- user_id (Long)
- entity_type (String): "SPOT", "PATROL_CLIENT", etc.
- entity_id (Long)
```

#### 5. **patrol_spot_timestamps**
Specific timestamps for patrol client spot check-ins/check-outs
```sql
- id (Long, PK)
- spot_id (Long, FK → qr_spots)
- patrol_client_id (Long, FK → patrol_clients)
- check_in_timestamp (LocalDateTime)
- check_out_timestamp (LocalDateTime)
- notes (String)
- status (String): "CHECKED_IN", "CHECKED_OUT", "COMPLETED"
```

## Security

### Users (In-Memory Authentication)
Two default users are configured:

**UFO User:**
- Username: `ufo`
- Password: `ufo_password_123`
- Role: `UFO_USER`

**OpenEye User:**
- Username: `openeye`
- Password: `openeye_password_123`
- Role: `OPENEYE_USER`

### Authentication
- Uses HTTP Basic Auth with BCrypt password encoding
- All endpoints except `/api/auth/**` require authentication

## API Endpoints

### Authentication
```
POST /api/auth/login - Login endpoint (permit all)
```

### Users Management
```
GET    /api/users                      - Get all users
GET    /api/users/{id}                 - Get user by ID
GET    /api/users/username/{username}  - Get user by username
POST   /api/users                      - Create new user
PUT    /api/users/{id}                 - Update user
DELETE /api/users/{id}                 - Delete user
```

### Patrol Clients
```
GET    /api/patrol-clients             - Get all patrol clients
GET    /api/patrol-clients/{id}        - Get patrol client by ID
POST   /api/patrol-clients             - Create patrol client
PUT    /api/patrol-clients/{id}        - Update patrol client
DELETE /api/patrol-clients/{id}        - Delete patrol client
```

### Spots (QR Spots)
```
GET    /api/spots                      - Get all spots
GET    /api/spots/{id}                 - Get spot by ID
GET    /api/spots/qr/{qrCode}         - Get spot by QR code
GET    /api/spots/client/{patrolClientId} - Get spots by patrol client
POST   /api/spots                      - Create spot
PUT    /api/spots/{id}                 - Update spot
DELETE /api/spots/{id}                 - Delete spot
```

### Timestamps (General Events)
```
GET    /api/timestamps                 - Get all timestamps
GET    /api/timestamps/{id}            - Get timestamp by ID
GET    /api/timestamps/event/{eventType} - Get timestamps by event type
GET    /api/timestamps/user/{userId}   - Get timestamps by user ID
GET    /api/timestamps/range?startTime=&endTime= - Get timestamps in date range
POST   /api/timestamps                 - Create timestamp
DELETE /api/timestamps/{id}            - Delete timestamp
```

**Date Format:** ISO 8601 (e.g., `2026-09-26T10:30:00`)

### Patrol Spot Timestamps
```
GET    /api/patrol-spot-timestamps              - Get all patrol spot timestamps
GET    /api/patrol-spot-timestamps/{id}        - Get by ID
GET    /api/patrol-spot-timestamps/spot/{spotId} - Get by spot ID
GET    /api/patrol-spot-timestamps/client/{patrolClientId} - Get by client ID
GET    /api/patrol-spot-timestamps/range?startTime=&endTime= - Get by date range
POST   /api/patrol-spot-timestamps              - Create check-in/out record
PUT    /api/patrol-spot-timestamps/{id}        - Update (check-out, add notes)
DELETE /api/patrol-spot-timestamps/{id}        - Delete record
```

## Example Requests

### Create a Patrol Client
```bash
curl -X POST http://localhost:8080/api/patrol-clients \
  -H "Content-Type: application/json" \
  -u ufo:ufo_password_123 \
  -d '{
    "clientName": "Downtown Patrol",
    "clientCode": "DT-001",
    "description": "Downtown area patrol",
    "isActive": true
  }'
```

### Create a Spot with Location
```bash
curl -X POST http://localhost:8080/api/spots \
  -H "Content-Type: application/json" \
  -u openeye:openeye_password_123 \
  -d '{
    "spotName": "Main Street Corner",
    "qrCode": "QR-DT-001",
    "longitude": -73.935242,
    "latitude": 40.730610,
    "patrolClientId": 1,
    "description": "Main street intersection",
    "isActive": true
  }'
```

### Check-in at a Spot
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

### Record General Event
```bash
curl -X POST http://localhost:8080/api/timestamps \
  -H "Content-Type: application/json" \
  -u openeye:openeye_password_123 \
  -d '{
    "eventType": "SPOT_CHECK",
    "description": "Officer checked spot DT-001",
    "userId": 1,
    "entityType": "SPOT",
    "entityId": 1
  }'
```

## Configuration

### application.properties
```properties
spring.application.name=parking-app
spring.datasource.url=jdbc:postgresql://localhost:5432/parkingdb
spring.datasource.username=parkinguser
spring.datasource.password=parkingpass
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
server.port=8080
```

## Running the Application

### Prerequisites
- Java 17+
- PostgreSQL database
- Maven

### Steps
1. **Setup Database:**
   ```bash
   createdb parkingdb
   psql parkingdb -U parkinguser < scripts/init_sopa_data.sql
   ```

2. **Build:**
   ```bash
   mvn clean package
   ```

3. **Run:**
   ```bash
   mvn spring-boot:run
   ```
   or
   ```bash
   java -jar target/parking-app-0.0.1-SNAPSHOT.jar
   ```

4. **Verify:**
   ```bash
   curl -u ufo:ufo_password_123 http://localhost:8080/api/users
   ```

## Docker Support

### Docker Compose
```bash
docker compose up --build
```

This will start:
- PostgreSQL database (port 5432)
- Spring Boot application (port 8080)

## Entities Architecture

```
AppUser (authenticated user)
  ├── UFO_USER
  └── OPENEYE_USER

PatrolClient
  └── Spots (1-to-many)
      ├── Location (latitude, longitude)
      ├── QR Code (unique identifier)
      └── PatrolSpotTimestamp (check-ins/outs)

Timestamp (General audit log for all events)

PatrolSpotTimestamp (Specific patrol activity log)
```

## Features Implemented

✅ User authentication with two app types (UFO, OpenEye)
✅ Spring Security with HTTP Basic Auth
✅ Patrol client management
✅ QR spot management with GPS location
✅ General timestamps for all events
✅ Patrol spot specific timestamps (check-in/check-out)
✅ RESTful APIs with proper error handling
✅ JPA/Hibernate ORM with PostgreSQL
✅ Validation annotations on entities
✅ Lombok for reducing boilerplate
✅ Docker support

## Future Enhancements

- JWT token-based authentication
- Role-based access control (RBAC)
- Spot boundary alerts
- Real-time notifications
- Mobile app integration
- Advanced analytics dashboard
- Photo/evidence capture
- Offline mode support

---

**Last Updated:** September 26, 2026
**Version:** 1.0.0
