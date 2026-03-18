# Secure Exam Browser Backend Integration

Spring Boot backend with MySQL persistence, REST + WebSocket event ingestion, and JWT-based authentication.

## Features

- Store exam browser events in MySQL (`exam_events` table via JPA)
- REST endpoint: `POST /api/events`
- STOMP endpoint: `/app/event` (via websocket `/ws`)
- Real-time admin alerts broadcast: `/topic/alerts`
- Role-based access with JWT:
  - `ROLE_STUDENT` and `ROLE_ADMIN` can send events
  - `ROLE_ADMIN` can view all events (`GET /api/events`)
- Layered architecture: controller -> service -> repository
- Validation and centralized error handling

## Stack

- Java 17
- Spring Boot 3.3.5
- Spring Web, Validation, Data JPA, Security, WebSocket
- MySQL
- JWT (jjwt)

## Configure Database

Edit `src/main/resources/application.properties`:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.datasource.driver-class-name`

Default DB in config: `exam_monitoring`

## Run

```bash
mvn spring-boot:run
```

On startup, default users are created:

- admin / admin123 (`ROLE_ADMIN`)
- student1 / student123 (`ROLE_STUDENT`)

## Auth Flow

1. Login:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "student1",
  "password": "student123"
}
```

Response:

```json
{
  "token": "<JWT_TOKEN>"
}
```

2. Use JWT in REST requests:

`Authorization: Bearer <JWT_TOKEN>`

## REST Event Ingestion

```http
POST /api/events
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "studentId": "S-1023",
  "eventType": "TAB_SWITCH",
  "timestamp": "2026-03-17T12:20:00Z",
  "details": "Window focus lost for 2 seconds"
}
```

## WebSocket Event Ingestion (STOMP)

- Connect to websocket endpoint: `/ws`
- Send STOMP `CONNECT` header:
  - `Authorization: Bearer <JWT_TOKEN>`
- Send event frame to destination: `/app/event`

Event payload example:

```json
{
  "studentId": "S-1023",
  "eventType": "FULLSCREEN_EXIT",
  "details": "Fullscreen exited during exam"
}
```

## Admin Live Monitoring

Admin dashboard subscribes to:

- `/topic/alerts`

Each new persisted event is broadcast to this topic.

## Package Structure

- `com.exam.backend.auth` -> JWT, login, user model
- `com.exam.backend.config` -> security, websocket, seed data
- `com.exam.backend.event` -> entity, repo, service, controllers
- `com.exam.backend.shared` -> exceptions and error response

## Notes

- Change JWT secret and DB credentials before production use.
- Replace seeded users with proper user provisioning.
- For production scale, replace in-memory websocket broker with external broker (RabbitMQ/Redis-backed architecture).
