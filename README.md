# chit-chat

A Java servlet-based real-time web chat application. Users can register, log in, select a contact, and exchange messages in the browser. The backend is pluggable — switch between **PostgreSQL** and **Elasticsearch** with a single config change.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Database Setup](#database-setup)
- [Build & Deploy](#build--deploy)
- [URL Reference](#url-reference)
- [Flow Summary](#flow-summary)

---

## Architecture Overview

```
Browser (JSP)
    │
    ▼
Controllers  (@WebServlet — Jakarta Servlet 6.1)
    │  LoginController · AddUserController · ContactsController
    │  ChatController  · LogoutController
    │
    ▼
Repo Interfaces  (ChatRepo · UserRepo)
    │
    ├── FactoryChat / FactoryUser  ← reads db.type from application.properties
    │
    ├── SQL path   →  SQLChatRepo / SQLUserRepo  →  ConnectSQL  →  PostgreSQL
    └── ES  path   →  ESChatRepo  / ESUserRepo   →  ConnectES   →  Elasticsearch
```

Diagrams:
- [`docs/class-diagram.puml`](docs/class-diagram.puml) — full class & dependency diagram
- [`docs/sequence-diagram.puml`](docs/sequence-diagram.puml) — register / login / chat / logout flows

---

## Project Structure

```
src/main/
├── java/com/chitchat/
│   ├── model/
│   │   └── Chat.java                  # Immutable chat message (sender, receiver, message, Instant)
│   ├── config/
│   │   ├── AppConfig.java             # Loads application.properties once at startup
│   │   ├── db/
│   │   │   ├── ConnectSQL.java        # PostgreSQL singleton connection
│   │   │   └── ConnectES.java         # Elasticsearch singleton transport client
│   │   └── security/
│   │       ├── DataCrypt.java         # BCrypt password hash / verify (cost=10)
│   │       └── SessionKey.java        # UUID session token generate / validate
│   ├── repo/
│   │   ├── chat/
│   │   │   ├── ChatRepo.java          # Interface
│   │   │   ├── FactoryChat.java       # Returns SQLChatRepo or ESChatRepo
│   │   │   ├── SQLChatRepo.java
│   │   │   └── ESChatRepo.java
│   │   └── user/
│   │       ├── UserRepo.java          # Interface
│   │       ├── FactoryUser.java       # Returns SQLUserRepo or ESUserRepo
│   │       ├── SQLUserRepo.java
│   │       └── ESUserRepo.java
│   └── controller/
│       ├── LoginController.java       # POST /LoginController
│       ├── AddUserController.java     # POST /AddUserController
│       ├── ContactsController.java    # POST /ContactsController
│       ├── ChatController.java        # POST /ChatController
│       └── LogoutController.java      # POST /LogoutController
├── webapp/
│   ├── Login.jsp
│   ├── AddUser.jsp
│   ├── Contacts.jsp
│   ├── Chat.jsp                       # Polling UI — sets lasttime = Instant.EPOCH
│   ├── FetchChat.jsp                  # AJAX endpoint — returns <p> fragments
│   └── WEB-INF/
│       └── web.xml                    # Jakarta EE 6.1, metadata-complete=false
└── resources/
    ├── application.properties         # DB type, credentials, ES host
    ├── schema.sql                      # PostgreSQL DDL + indexes
    └── es-mapping.json                 # Elasticsearch index mapping
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Servlet API | Jakarta Servlet 6.1 |
| App Server | Apache Tomcat 11 |
| Build | Gradle 9 (Kotlin DSL) |
| SQL Database | PostgreSQL (via JDBC) |
| Search Database | Elasticsearch 5.6 (transport client) |
| Password Hashing | jBCrypt 0.4 (cost factor 10) |
| Logging | java.util.logging (JUL) |

---

## Prerequisites

- Java 25 JDK
- Apache Tomcat 11
- PostgreSQL 14+ **or** Elasticsearch 5.6 (depending on `db.type`)
- Gradle 9 (or use the included wrapper `./gradlew`)

---

## Configuration

All runtime config lives in `src/main/resources/application.properties`:

```properties
# Database selection: SQL or ES
db.type=SQL

# PostgreSQL
db.url=jdbc:postgresql://localhost:5432/chitchat
db.username=your_user
db.password=your_password

# Elasticsearch
es.host=localhost
es.port=9300
```

Switch the entire backend by changing `db.type=SQL` → `db.type=ES`. No code changes required.

---

## Database Setup

### PostgreSQL

Run `src/main/resources/schema.sql` against your database:

```bash
psql -U your_user -d chitchat -f src/main/resources/schema.sql
```

This creates:
- `users` — username (PK), BCrypt password hash, session token
- `chats` — id, sender, receiver, message, delivertime (TIMESTAMP, default now())
- Two composite indexes on `(sender, receiver, delivertime)` and `(receiver, sender, delivertime)` for fast conversation queries

### Elasticsearch

Create index `chatapp` using `src/main/resources/es-mapping.json`:

```bash
curl -X PUT "localhost:9200/chatapp" \
     -H "Content-Type: application/json" \
     -d @src/main/resources/es-mapping.json
```

The `timestamp` field uses `epoch_millis` format — stored as a `long` (milliseconds since Unix epoch).

---

## Build & Deploy

### Build WAR

```bash
./gradlew war
# Output: build/libs/chit-chat-1.0-SNAPSHOT.war
```

### Deploy to Tomcat (root context)

```bash
cp build/libs/chit-chat-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/ROOT.war
$CATALINA_HOME/bin/startup.sh
```

Deploying as `ROOT.war` mounts the app at `/`. Deploying under any other name (e.g. `chit-chat.war`) mounts it at `/chit-chat`.

### IntelliJ Tomcat Run Config

1. **Run → Edit Configurations → + → Tomcat Server → Local**
2. **Deployment tab** → add the Gradle WAR artifact
3. Set **Application context** to `/`
4. Click Run

---

## URL Reference

| URL | Method | Description |
|---|---|---|
| `/Login.jsp` | GET | Login page |
| `/AddUser.jsp` | GET | Registration page |
| `/LoginController` | POST | Authenticate user, create session |
| `/AddUserController` | POST | Register new user |
| `/Contacts.jsp` | GET | Contact list (requires session) |
| `/ContactsController` | POST | Select a contact, open chat |
| `/Chat.jsp` | GET | Chat UI |
| `/ChatController` | POST | Send a message (AJAX) |
| `/FetchChat.jsp` | GET | Poll for new messages (AJAX, every 1 s) |
| `/LogoutController` | POST | Invalidate session, clear DB token |

---

## Flow Summary

### Register
`AddUser.jsp` → `AddUserController` → BCrypt hash password → `INSERT INTO users`

### Login
`Login.jsp` → `LoginController` → fetch stored hash → BCrypt verify → generate UUID session token → store in DB + HTTP session

### Chat
1. `Contacts.jsp` → `ContactsController` → set `session.receiver` → `Chat.jsp`
2. `Chat.jsp` sets `session.lasttime = Instant.EPOCH`, starts two AJAX loops:
   - **Send** — `POST /ChatController` on button click → `INSERT INTO chats`
   - **Receive** — `GET /FetchChat.jsp` every 1 s → `SELECT * FROM chats WHERE delivertime > lasttime` → returns `<p>` fragments → appended to chat div; `lasttime` updated to last message's `Instant` (full nanosecond precision to avoid duplicate fetch)

### Logout
`LogoutController` → `setKey(uname, null)` in DB → `session.invalidate()`
