# SOPA Backend - Architecture & Database Design

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENT APPLICATIONS                           │
│  ┌──────────────────────┐          ┌──────────────────────────┐     │
│  │   UFO Mobile App     │          │   OpenEye Web Dashboard  │     │
│  │   (Username: ufo)    │          │ (Username: openeye)      │     │
│  └──────────────────────┘          └──────────────────────────┘     │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                    HTTP Basic Auth │ (JSON/REST)
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    SPRING BOOT BACKEND (Port 8080)                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │              Spring Security Layer                          │   │
│  │  ┌─────────────────────────────────────────────────────┐   │   │
│  │  │  BCryptPasswordEncoder                              │   │   │
│  │  │  UserDetailsService (ufo, openeye)                 │   │   │
│  │  │  SecurityFilterChain (endpoint authorization)       │   │   │
│  │  └─────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                    │                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │              REST Controllers Layer                         │   │
│  │                                                              │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │   │
│  │  │   Users API  │  │  Clients API │  │   Spots API  │      │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘      │   │
│  │                                                              │   │
│  │  ┌──────────────────┐  ┌──────────────────────────────┐    │   │
│  │  │ Timestamps API   │  │ Patrol Spot Timestamps API   │    │   │
│  │  └──────────────────┘  └──────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                    │                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │          JPA Repository Layer                              │   │
│  │                                                              │   │
│  │  AppUserRepository    PatrolClientRepository   SpotRepository  │
│  │  TimestampRepository  PatrolSpotTimestampRepository        │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                    │                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │          Hibernate ORM Layer                               │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                        JDBC Protocol
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│              PostgreSQL Database (Port 5432)                        │
├─────────────────────────────────────────────────────────────────────┤
│  parkingdb                                                           │
│  ┌───────────────────────────────────────────────────────────────┐ │
│  │ Tables:                                                       │ │
│  │                                                                │ │
│  │  1. app_users          ─── 2 rows (ufo, openeye)             │ │
│  │  2. patrol_clients     ─── N patrol clients                  │ │
│  │  3. qr_spots           ─── N spots with GPS coords           │ │
│  │  4. timestamps         ─── N audit log events                │ │
│  │  5. patrol_spot_       ─── N patrol activities               │ │
│  │     timestamps                                                │ │
│  │                                                                │ │
│  └───────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Database Entity Relationship Diagram (ERD)

```
┌─────────────────────────┐
│      app_users          │
├─────────────────────────┤
│ id (PK)                 │
│ username (UNIQUE)       │
│ password                │
│ app_type                │
│ is_active               │
└─────────────────────────┘
           │
           │ (authenticated user)
           │
           ▼
┌─────────────────────────┐          ┌─────────────────────────┐
│   patrol_clients        │◄─ 1──M ─►│     qr_spots            │
├─────────────────────────┤          ├─────────────────────────┤
│ id (PK)                 │          │ id (PK)                 │
│ client_name             │          │ spot_name               │
│ client_code             │          │ qr_code (UNIQUE)        │
│ description             │          │ longitude (GPS)         │
│ is_active               │          │ latitude (GPS)          │
└─────────────────────────┘          │ patrol_client_id (FK)   │
           │                          │ description             │
           │                          │ is_active               │
           │                          └─────────────────────────┘
           │                                      │
           │                                      │
           │                         ┌────────────▼─────────────┐
           │                         │ patrol_spot_timestamps   │
           │                         ├──────────────────────────┤
           │                         │ id (PK)                  │
           │                         │ spot_id (FK)             │
           │                         │ patrol_client_id (FK) ───┼──┐
           │                         │ check_in_timestamp       │  │
           │                         │ check_out_timestamp      │  │
           │                         │ notes                    │  │
           │                         │ status                   │  │
           │                         └──────────────────────────┘  │
           │                                                       │
           └───────────────────────────────────────────────────────┘

┌─────────────────────────────────────┐
│         timestamps                  │
│  (Independent Audit Log)            │
├─────────────────────────────────────┤
│ id (PK)                             │
│ event_type                          │
│ description                         │
│ event_timestamp                     │
│ user_id (references app_users)      │
│ entity_type                         │
│ entity_id                           │
└─────────────────────────────────────┘
```

---

## Data Flow Diagrams

### Flow 1: User Authentication & Login

```
┌─────────────┐
│   Client    │
└─────────────┘
       │
       │ HTTP Basic Auth Header
       │ (Authorization: Basic base64(username:password))
       │
       ▼
┌─────────────────────────────────────┐
│   Security Filter Chain             │
│   (Validates Credentials)           │
└─────────────────────────────────────┘
       │
       ├─ Valid ──► UserDetailsService
       │            (Load ufo/openeye)
       │                  │
       │                  ▼
       │            BCryptPasswordEncoder
       │            (Verify Password)
       │                  │
       ├─ Success ──► Create Authentication Token
       │                  │
       │                  ▼
       │            Allow Request Processing
       │
       └─ Invalid ──► Return 401 Unauthorized
```

### Flow 2: Patrol Check-in Process

```
Client (UFO/OpenEye)
       │
       ▼
POST /api/patrol-spot-timestamps
(spotId, patrolClientId, status="CHECKED_IN")
       │
       ▼
┌──────────────────────────┐
│ PatrolSpotTimestamp      │
│ Controller               │
└──────────────────────────┘
       │
       ▼
┌──────────────────────────┐
│ Validate Spot & Client   │
│ (Check if exists)        │
└──────────────────────────┘
       │
       ▼
┌──────────────────────────┐
│ Create PatrolSpotTime    │
│ timestamp Entity         │
│ (Auto-set: check_in_ts) │
└──────────────────────────┘
       │
       ▼
┌──────────────────────────┐
│ Save to Database         │
│ (patrol_spot_timestamps) │
└──────────────────────────┘
       │
       ▼
┌──────────────────────────┐
│ Log to Audit Trail       │
│ (timestamps table)       │
│ Event: "SPOT_CHECK_IN"   │
└──────────────────────────┘
       │
       ▼
Return 201 Created
(PatrolSpotTimestamp object)
```

### Flow 3: General Event Logging

```
Client (Any authenticated user)
       │
       ▼
POST /api/timestamps
(eventType, description, userId, entityType, entityId)
       │
       ▼
┌──────────────────────────┐
│ Timestamp Controller     │
└──────────────────────────┘
       │
       ▼
┌──────────────────────────┐
│ Create Timestamp Entity  │
│ (Auto-set: timestamp)    │
└──────────────────────────┘
       │
       ▼
┌──────────────────────────┐
│ Save to timestamps       │
│ table                    │
└──────────────────────────┘
       │
       ▼
Return 201 Created
(Timestamp object)
```

---

## Request/Response Examples

### Example 1: Get All Patrol Clients

**Request:**
```
GET /api/patrol-clients HTTP/1.1
Host: localhost:8080
Authorization: Basic dWZvOnVmb19wYXNzd29yZF8xMjM=
```

**Response:**
```json
[
  {
    "id": 1,
    "clientName": "Downtown Patrol",
    "clientCode": "DT-001",
    "description": "Downtown area patrol client",
    "isActive": true
  },
  {
    "id": 2,
    "clientName": "North Zone Patrol",
    "clientCode": "NZ-002",
    "description": "North zone area patrol client",
    "isActive": true
  }
]
```

### Example 2: Create Spot with Location

**Request:**
```json
POST /api/spots HTTP/1.1
Host: localhost:8080
Authorization: Basic b3BlbmV5ZTpvcGVuZXllX3Bhc3N3b3JkXzEyMw==
Content-Type: application/json

{
  "spotName": "Times Square Area",
  "qrCode": "QR-TS-001",
  "longitude": -73.985130,
  "latitude": 40.758896,
  "patrolClientId": 1,
  "description": "Times Square patrol zone",
  "isActive": true
}
```

**Response (201 Created):**
```json
{
  "id": 3,
  "spotName": "Times Square Area",
  "qrCode": "QR-TS-001",
  "longitude": -73.985130,
  "latitude": 40.758896,
  "patrolClientId": 1,
  "description": "Times Square patrol zone",
  "isActive": true
}
```

### Example 3: Check-in to Spot

**Request:**
```json
POST /api/patrol-spot-timestamps HTTP/1.1
Host: localhost:8080
Authorization: Basic dWZvOnVmb19wYXNzd29yZF8xMjM=
Content-Type: application/json

{
  "spotId": 3,
  "patrolClientId": 1,
  "status": "CHECKED_IN"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "spotId": 3,
  "patrolClientId": 1,
  "checkInTimestamp": "2026-09-26T14:30:45.123456",
  "checkOutTimestamp": null,
  "notes": null,
  "status": "CHECKED_IN"
}
```

### Example 4: Query Timestamps by Date Range

**Request:**
```
GET /api/timestamps/range?startTime=2026-09-26T00:00:00&endTime=2026-09-26T23:59:59 HTTP/1.1
Host: localhost:8080
Authorization: Basic b3BlbmV5ZTpvcGVuZXllX3Bhc3N3b3JkXzEyMw==
```

**Response:**
```json
[
  {
    "id": 1,
    "eventType": "SPOT_CHECK",
    "description": "Officer checked spot TS-001",
    "eventTimestamp": "2026-09-26T14:35:22.567890",
    "userId": 1,
    "entityType": "SPOT",
    "entityId": 3
  },
  {
    "id": 2,
    "eventType": "LOGIN",
    "description": "UFO user logged in",
    "eventTimestamp": "2026-09-26T08:00:00.000000",
    "userId": 1,
    "entityType": "USER",
    "entityId": 1
  }
]
```

---

## HTTP Status Codes Used

| Code | Meaning | Example Scenario |
|------|---------|------------------|
| 200 | OK | GET requests successful |
| 201 | Created | POST request successful |
| 204 | No Content | DELETE successful |
| 400 | Bad Request | Invalid JSON body |
| 401 | Unauthorized | Missing/invalid credentials |
| 403 | Forbidden | User lacks permission |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | QR code already exists |
| 500 | Server Error | Database error |

---

## Security Features

### Authentication Flow

```
┌──────────────────────────────────────────┐
│ Client sends HTTP Basic Auth request      │
│ (base64 encoded: username:password)       │
└──────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────┐
│ Spring Security Filter intercepts         │
│ (SecurityFilterChain)                     │
└──────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────┐
│ UserDetailsService loads user from       │
│ in-memory provider (ufo/openeye)         │
└──────────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────┐
│ BCryptPasswordEncoder verifies password   │
│ (Compares hashed values)                  │
└──────────────────────────────────────────┘
                    │
                    ├─ MATCH ─────► Grant Access
                    │                 with GrantedAuthority
                    │
                    └─ NO MATCH ──► Return 401 Unauthorized
```

### Authorization Matrix

| Endpoint | Auth Required | Roles | UFO | OpenEye |
|----------|---------------|-------|-----|---------|
| POST /api/auth/** | No | - | ✅ | ✅ |
| GET /api/users | Yes | Any | ✅ | ✅ |
| POST /api/users | Yes | Any | ✅ | ✅ |
| GET /api/patrol-clients | Yes | Any | ✅ | ✅ |
| POST /api/patrol-clients | Yes | Any | ✅ | ✅ |
| GET /api/spots | Yes | Any | ✅ | ✅ |
| POST /api/spots | Yes | Any | ✅ | ✅ |
| POST /api/timestamps | Yes | Any | ✅ | ✅ |
| POST /api/patrol-spot-timestamps | Yes | Any | ✅ | ✅ |

---

## Technology Stack

```
┌──────────────────────────────────┐
│     Spring Boot 3.3.2            │
│  ├─ Spring Web (REST APIs)       │
│  ├─ Spring Data JPA (ORM)        │
│  ├─ Spring Security (Auth/Sec)   │
│  └─ Spring Validation (Input)    │
├──────────────────────────────────┤
│     Persistence Layer            │
│  ├─ Hibernate ORM                │
│  ├─ PostgreSQL JDBC Driver       │
│  └─ Jakarta Persistence API      │
├──────────────────────────────────┤
│     Utilities                    │
│  ├─ Lombok (Reduce boilerplate)  │
│  ├─ BCrypt (Password encoding)   │
│  └─ Validation (JSR-380)         │
├──────────────────────────────────┤
│     Database                     │
│  └─ PostgreSQL 12+               │
└──────────────────────────────────┘
```

---

## Deployment Architecture

```
┌─────────────────────────────────────────────┐
│           Docker Compose                    │
├─────────────────────────────────────────────┤
│                                              │
│ ┌─────────────────────────────────────┐    │
│ │   Container: sopa-backend           │    │
│ │   Image: Dockerfile (Spring Boot)   │    │
│ │   Port: 8080                        │    │
│ │   ┌─────────────────────────────┐   │    │
│ │   │ Spring Boot Application    │   │    │
│ │   │ + All Controllers/Services  │   │    │
│ │   └─────────────────────────────┘   │    │
│ └─────────────────────────────────────┘    │
│                     │                       │
│                     │ (JDBC)                │
│                     │                       │
│ ┌─────────────────────────────────────┐    │
│ │   Container: postgres               │    │
│ │   Image: postgres:15                │    │
│ │   Port: 5432                        │    │
│ │   Volume: /var/lib/postgresql/data  │    │
│ │   Database: parkingdb               │    │
│ └─────────────────────────────────────┘    │
│                                              │
└─────────────────────────────────────────────┘
```

**Run with:**
```bash
docker compose up --build
```

---

**Last Updated:** September 26, 2026
**Architecture Version:** 1.0
