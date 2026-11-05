# Airport Management System

A web application for managing an airport's passenger services: flight schedules, passengers and
bookings, simulated payment, tickets, seats, check-in, baggage tracking, security checks, boarding,
assistance requests, complaints, lost and found, and passenger reviews.

It is a two-student university project. This repository contains **Student B's part** (passenger
services, Module 1 complete) and **temporary, minimal versions of Student A's tables** (users, roles,
departments, airlines, flights), so it runs on its own.

| | |
|---|---|
| Frontend | Angular 21 (standalone components, signals), TypeScript 5.9, plain CSS |
| Backend | Spring Boot 4.1, Java 21 (runs on Java 21 to 25), Spring Data JPA / Hibernate, Bean Validation |
| Database | PostgreSQL (tested with 16 and 18), schema managed by Flyway |
| Communication | REST API, JSON over HTTP |
| Tests | Vitest (frontend), JUnit 5 + Mockito + AssertJ (backend) |

---

## Table of contents

1. [Overview and architecture](#1-overview-and-architecture)
2. [Prerequisites](#2-prerequisites)
3. [Installation](#3-installation)
4. [Configuration and environment variables](#4-configuration-and-environment-variables)
5. [Run the backend](#5-run-the-backend)
6. [Run the frontend](#6-run-the-frontend)
7. [How frontend and backend work together](#7-how-frontend-and-backend-work-together)
8. [Scripts and commands](#8-scripts-and-commands)
9. [API endpoints](#9-api-endpoints)
10. [Database: setup, migrations, demo data](#10-database-setup-migrations-demo-data)
11. [Testing](#11-testing)
12. [Build and deployment](#12-build-and-deployment)
13. [Troubleshooting](#13-troubleshooting)
14. [Project structure](#14-project-structure)
15. [Assumptions and missing information](#15-assumptions-and-missing-information)

---

## 1. Overview and architecture

```
Browser ── Angular app (http://localhost:4200)
              │  HttpClient + 2 interceptors (auth header, error toasts)
              │  JSON over HTTP, base URL http://localhost:8080/api
              ▼
         Spring Boot REST API (http://localhost:8080)
              │  Controller → Service (business rules) → Repository (JPA)
              │  GlobalExceptionHandler → uniform JSON errors (400 / 404 / 409 / 500)
              ▼
         PostgreSQL database "airport_db"
              tables created and versioned by Flyway (V1, V2, V100–V103)
```

Main design choices:

- **The database schema is defined only by SQL migrations** (Flyway). Hibernate runs with
  `ddl-auto=validate`: it never creates or changes tables, it only checks at startup that every
  Java entity matches a table.
- **Business rules are enforced twice**: in the Java services (clear error messages) and, for the
  critical ones, by PostgreSQL constraints (CHECK, UNIQUE, partial unique indexes) as a safety net.
- **Entities are never sent to the browser**: each endpoint returns a DTO (Java `record`), mirrored by
  a TypeScript interface in the frontend.
- **Payments are simulated**: no card number or bank data exists anywhere in the code or the schema.
- **Flights belong to Student A**: Student B's tables only reference a flight by `vol_id`.

## 2. Prerequisites

| Tool | Version used | Check with |
|---|---|---|
| Node.js | 24.x (22.x also works) | `node -v` |
| npm | 11.x | `npm -v` |
| Angular CLI | 21.2.x | `ng version` |
| Java JDK | 21 or newer (developed with Temurin 25) | `java -version` |
| Maven | 3.9 or newer | `mvn -v` |
| PostgreSQL | 16 or 18 | `psql --version` |
| Git | any recent version | `git --version` |

Install the Angular CLI globally if `ng` is not found:

```
npm install -g @angular/cli@21
```

> **Maven on Windows:** the project has no Maven wrapper (`mvnw`). If `mvn` is not on your `PATH`,
> call it with its full path, for example `& "C:\Tools\apache-maven-3.10.0\bin\mvn.cmd" -v`,
> or open `backend/pom.xml` in IntelliJ IDEA, which includes Maven.

## 3. Installation

The commands below are for **Windows PowerShell**. The repository folder is called `airport`.

```
git clone <REPOSITORY_URL> airport
cd airport
```

**1. Create the database** (once). Use the password of your `postgres` user:

```
psql -U postgres -c "CREATE DATABASE airport_db;"
```

**2. Install the frontend dependencies:**

```
cd frontend
npm install
cd ..
```

**3. Download the backend dependencies and compile:**

```
cd backend
mvn -DskipTests package
cd ..
```

The tables are created automatically the first time the backend starts (see section 10).

## 4. Configuration and environment variables

This project does **not** use `.env` files. Spring Boot does not read `.env` files by default, and
Angular builds its configuration into the code. Configuration works like this:

### Backend

`backend/src/main/resources/application.properties` reads two **operating-system environment
variables**, with a default value after the colon:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/airport_db
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
app.cors.allowed-origins=http://localhost:4200
```

| Variable | Default | Purpose |
|---|---|---|
| `DB_USERNAME` | `postgres` | PostgreSQL user |
| `DB_PASSWORD` | `postgres` | PostgreSQL password. **Set it**: your password is probably not `postgres`. |

Set it for the current PowerShell window only:

```
$env:DB_PASSWORD = Read-Host "Mot de passe PostgreSQL"
```

Or save it once for your Windows user (then open a new PowerShell window):

```
[Environment]::SetEnvironmentVariable("DB_PASSWORD", (Read-Host "Mot de passe PostgreSQL"), "User")
```

Any other property can also be overridden by an environment variable, using Spring Boot's naming
rule (uppercase, dots become underscores, dashes removed), for example:

| Environment variable | Overrides |
|---|---|
| `SPRING_DATASOURCE_URL` | `spring.datasource.url` |
| `SERVER_PORT` | `server.port` |
| `APP_CORS_ALLOWEDORIGINS` | `app.cors.allowed-origins` (comma-separated list) |

Never put a real password in `application.properties`: that file is committed to Git.

### Frontend

The API address is a constant in `frontend/src/app/core/config/api.config.ts`:

```ts
export const API_BASE_URL = 'http://localhost:8080/api';
```

Change this line if the backend runs on another host or port, then rebuild.
There is no frontend environment variable.

## 5. Run the backend

PostgreSQL must be running and `airport_db` must exist.

```
cd backend
$env:DB_PASSWORD = Read-Host "Mot de passe PostgreSQL"   # skip if saved as a user variable
mvn spring-boot:run
```

Success looks like this in the log:

```
Successfully applied 6 migrations to schema "public"      (first start only)
Tomcat started on port 8080 (http)
Started AirportApplication in ... seconds
```

Check in a browser or with PowerShell:

```
Invoke-RestMethod http://localhost:8080/api/health
```

Expected: `status UP` and `database UP`.

Stop the backend with **Ctrl + C**.

## 6. Run the frontend

In a **second** PowerShell window (keep the backend running):

```
cd frontend
ng serve
```

Open **http://localhost:4200**. The top bar shows "API et base connectées" when the backend and the
database are reachable. `ng serve` reloads the page automatically when you save a file.

## 7. How frontend and backend work together

| Item | Value | Where it is set |
|---|---|---|
| Frontend URL | `http://localhost:4200` | Angular CLI default port |
| Backend URL | `http://localhost:8080` | `server.port` in `application.properties` |
| API base URL | `http://localhost:8080/api` | `frontend/src/app/core/config/api.config.ts` |
| Allowed CORS origin | `http://localhost:4200` | `app.cors.allowed-origins`, applied by `CorsConfig.java` to `/api/**` |

**CORS.** The two apps run on different ports, so the browser blocks calls unless the backend allows
the frontend's origin. `CorsConfig.java` allows `GET, POST, PUT, PATCH, DELETE, OPTIONS` from the
configured origin. If you serve the frontend from another address, add it to `app.cors.allowed-origins`.

**Errors.** Every backend error has the same JSON shape (`ApiError.java`):

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Ce passager a déjà une réservation active sur ce vol",
  "path": "/api/reservations",
  "timestamp": "2026-10-29T10:15:00",
  "fieldErrors": null
}
```

| Status | Meaning |
|---|---|
| 400 | Invalid request body (validation, `fieldErrors` lists the fields) or unreadable JSON |
| 404 | Record not found |
| 409 | Business rule broken, or a database constraint refused the change |
| 500 | Unexpected error (details only in the server log) |

The frontend's `errorInterceptor` turns every error into a readable toast. Status `0` means the
request never reached the server ("Impossible de joindre le serveur").

**Authentication.** There is **no login yet**: authentication with Spring Security is Student A's
responsibility. The frontend is already prepared: `authInterceptor` adds
`Authorization: Bearer <token>` to every `/api` request when a token is stored in `localStorage`
under the key `ams_token`. Until the login exists, no token is stored and requests are sent unchanged.

The **passenger space** (`/passengers/espace`) works without an account, like an airline
"manage my booking" page: the passenger enters the reservation reference and the email, sent in the
body of a POST so they do not appear in URLs or server logs.

## 8. Scripts and commands

### Frontend (`frontend/`)

| Command | What it does |
|---|---|
| `npm install` | Install dependencies |
| `ng serve` (or `npm start`) | Development server on http://localhost:4200 |
| `ng build` (or `npm run build`) | Production build into `frontend/dist/frontend/browser/` |
| `ng build --configuration development` | Faster, non-minified build |
| `npm run watch` | Development build, rebuilt on every change |
| `ng test --watch=false` | Run the unit tests once (Vitest) |
| `ng test` (or `npm test`) | Run the tests in watch mode |

### Backend (`backend/`)

| Command | What it does |
|---|---|
| `mvn spring-boot:run` | Start the API on http://localhost:8080 |
| `mvn test` | Run the unit tests |
| `mvn package` | Compile, test and build `target/airport-backend-0.0.1-SNAPSHOT.jar` |
| `mvn -DskipTests package` | Same, without running the tests |
| `java -jar target/airport-backend-0.0.1-SNAPSHOT.jar` | Run the built jar |

## 9. API endpoints

All URLs start with `http://localhost:8080`. Request and response bodies are JSON.

### General and flights

| Method | URL | Purpose |
|---|---|---|
| GET | `/api/health` | Backend and database status |
| GET | `/api/vols` | Upcoming flights *(temporary Student A API)* |
| GET | `/api/vols/{id}` | One flight *(temporary Student A API)* |
| GET | `/api/vols/{volId}/sieges` | Seat map of a flight (seats generated on first call) |

### Passengers and bookings

| Method | URL | Purpose |
|---|---|---|
| GET | `/api/passagers?recherche=` | List or search passengers |
| GET | `/api/passagers/{id}` | One passenger |
| POST | `/api/passagers` | Create a passenger |
| PUT | `/api/passagers/{id}` | Update a passenger |
| DELETE | `/api/passagers/{id}` | Delete (refused if the passenger has history) |
| GET | `/api/reservations?statut=&volId=&passagerId=` | List bookings with optional filters |
| GET | `/api/reservations/{id}` | Booking detail: payments, ticket, bags, assistance, review |
| POST | `/api/reservations` | Book `{passagerId, volId, classe}` |
| POST | `/api/reservations/{id}/paiements` | Simulated payment `{mode, simulerRefus}` |
| POST | `/api/reservations/{id}/annulation` | Cancel a booking |
| POST | `/api/espace-passager/recherche` | Passenger space `{reference, email}` |

### Tickets, check-in and baggage

| Method | URL | Purpose |
|---|---|---|
| GET | `/api/billets/{id}` | One ticket |
| PUT | `/api/billets/{id}/siege` | Choose a seat `{siegeId}` |
| POST | `/api/billets/{id}/enregistrement` | Check in `{canal}` |
| POST | `/api/enregistrements/{id}/bagages` | Add a bag `{type, poidsKg}` |
| GET | `/api/bagages?statut=&etiquette=` | List bags |
| GET | `/api/bagages/{id}` | Bag with status history and handling events |
| POST | `/api/bagages/{id}/evenements` | Record a scan `{typeEvenement, lieu, commentaire}` |

### Security and boarding

| Method | URL | Purpose |
|---|---|---|
| GET | `/api/points-controle` | List security checkpoints |
| POST | `/api/points-controle` | Create a checkpoint |
| PUT | `/api/points-controle/{id}` | Update (also activate / deactivate) |
| DELETE | `/api/points-controle/{id}` | Delete (refused if already used) |
| GET | `/api/vols/{volId}/embarquement` | Gate screen: checked-in passengers and their state |
| POST | `/api/billets/{id}/controles-securite` | Security check `{pointControleId, resultat, commentaire}` |
| POST | `/api/billets/{id}/embarquement` | Gate event `{typeEvenement, porte, commentaire}` |

### Customer relations

| Method | URL | Purpose |
|---|---|---|
| GET | `/api/assistances?statut=&volId=` | List assistance requests |
| POST | `/api/assistances` | Create `{reservationId, typeAssistance, description}` |
| PUT | `/api/assistances/{id}/statut` | Change status `{statut}` |
| GET | `/api/avis?note=` | List reviews |
| POST | `/api/avis` | Give a review `{reservationId, note, commentaire}` (after boarding) |
| GET | `/api/reclamations?statut=&categorie=` | List complaints |
| GET | `/api/reclamations/{id}` | One complaint |
| POST | `/api/reclamations` | Create a complaint |
| PUT | `/api/reclamations/{id}/statut` | Process `{statut, reponse}` |
| GET | `/api/objets-trouves?statut=&categorie=&recherche=` | List found objects |
| GET | `/api/objets-trouves/{id}` | Object with its claims |
| POST | `/api/objets-trouves` | Register a found object |
| POST | `/api/objets-trouves/{id}/demandes` | Claim an object |
| POST | `/api/objets-trouves/{id}/elimination` | Eliminate (only after 90 days in stock) |
| POST | `/api/demandes-restitution/{id}/decision` | Accept or refuse a claim `{acceptee, commentaire}` |

Example:

```
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/reservations `
  -ContentType "application/json" `
  -Body '{"passagerId": 1, "volId": 1, "classe": "ECONOMIQUE"}'
```

## 10. Database: setup, migrations, demo data

Flyway runs every script in `backend/src/main/resources/db/migration/` that has not been applied yet,
in version order, when the backend starts. Applied scripts are recorded in the table
`flyway_schema_history`.

| Script | Content |
|---|---|
| `V1__student_a_temporary_tables.sql` | Minimal Student A tables: `role`, `utilisateur`, `utilisateur_role`, `departement`, `compagnie_aerienne`, `vol` |
| `V2__student_a_demo_data.sql` | Demo roles, users, departments, 3 airlines, 6 flights dated from the creation day |
| `V100__passengers_travel_flow.sql` | Module 1 travel flow: 10 tables (passengers, bookings, payments, tickets, seats, check-in, bags…) |
| `V101__passengers_demo_data.sql` | 4 demo passengers |
| `V102__passengers_services.sql` | Module 1 services: 8 tables (security, boarding, assistance, reviews, complaints, lost and found) |
| `V103__passengers_services_demo_data.sql` | 3 security checkpoints, 4 found objects |

**Rules for migrations**

- Never edit a script that has already run: Flyway compares checksums and refuses to start.
  Add a new script instead (for example `V104__...sql`).
- Version numbers: **V3–V99 are reserved for Student A**, **V100 and above for Student B**.
- The demo data is the seed: there is no separate seed command.

**Reset the database** (deletes all data; useful before a demo, because demo flights are dated
relative to the day the database was created):

```
psql -U postgres -c "DROP DATABASE airport_db;"
psql -U postgres -c "CREATE DATABASE airport_db;"
```

Then restart the backend: Flyway recreates everything.

## 11. Testing

**Frontend** (Vitest, 9 test files): components, HTTP calls with a fake backend
(`HttpTestingController`), the confirmation dialog, the breadcrumb, the stars pipe.

```
cd frontend
ng test --watch=false
```

**Backend** (JUnit 5, Mockito, AssertJ, 7 test classes): business rules tested without a database
(repositories are replaced by Mockito mocks, the time by a fixed `Clock`).

```
cd backend
mvn test
```

There are no end-to-end or database integration tests yet: `<ADD_INTEGRATION_TESTS_IF_NEEDED>`.

## 12. Build and deployment

**Build the frontend:**

```
cd frontend
ng build
```

Output: static files in `frontend/dist/frontend/browser/`, to be served by any web server.
The production build downloads the Inter font from Google Fonts to inline it, so it needs internet access.

**Build the backend:**

```
cd backend
mvn package
java -jar target/airport-backend-0.0.1-SNAPSHOT.jar
```

**Before deploying anywhere other than your own computer:**

1. Set `API_BASE_URL` in `api.config.ts` to the public API address, then rebuild the frontend.
2. Set `APP_CORS_ALLOWEDORIGINS` to the public frontend address.
3. Set `SPRING_DATASOURCE_URL`, `DB_USERNAME`, `DB_PASSWORD` for the target database.
4. Add authentication (Student A's Spring Security module): today every endpoint is public.

Deployment target and procedure: `<ADD_DEPLOYMENT_TARGET_AND_STEPS>`. The project has no Docker
file or CI configuration.

## 13. Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| Backend log: `authentification par mot de passe échouée` / `password authentication failed`, SQL state `28P01` | `DB_PASSWORD` not set in this PowerShell window (default `postgres` used) | Set `DB_PASSWORD` (section 4), or save it as a user variable |
| Backend log: `database "airport_db" does not exist` | Database not created | `psql -U postgres -c "CREATE DATABASE airport_db;"` |
| Backend log: `Connection refused` on port 5432 | PostgreSQL service stopped | Start it: Windows *Services* → `postgresql-x64-<version>` → Start |
| Backend log: `Validate failed: Migration checksum mismatch` | An applied migration file was edited | Undo the edit and add a new `V…` script, or reset the database (section 10) |
| Backend log: `Schema-validation: missing column` or `wrong column type` | A Java entity and its table do not match | Fix the entity or add a migration; `ddl-auto=validate` will not change tables |
| Backend log: `Port 8080 was already in use` | Another backend still running | Close the other window, or start with `$env:SERVER_PORT=8081` and update `api.config.ts` |
| `mvn` is not recognized | Maven not on `PATH` | Use its full path (section 2) or IntelliJ IDEA |
| Toast: "Impossible de joindre le serveur" | Backend stopped, or a CORS refusal | Start the backend; check the browser console (F12) for a CORS message |
| Browser console: `blocked by CORS policy` | Frontend served from an origin other than `http://localhost:4200` | Add the origin to `app.cors.allowed-origins` |
| No flight in the lists, or booking refused "plus ouvert à la réservation" | Demo flights are in the past | Reset the database (section 10) |
| Check-in refused "ouvre 48 h avant le départ" | Normal rule: the flight departs more than 48 h later | Use a flight departing within 48 h, or reset the database |
| `ng build` stops on fonts (no internet) | Production build inlines Google Fonts | Connect to the internet, or build with `ng build --configuration development` |
| `ng` is not recognized | Angular CLI not installed globally | `npm install -g @angular/cli@21`, or use `npx ng ...` |

## 14. Project structure

```
airport/
├── README.md
├── backend/                              Spring Boot API
│   ├── pom.xml                           Maven build (Spring Boot 4.1, Java 21)
│   └── src/
│       ├── main/
│       │   ├── java/com/airport/
│       │   │   ├── AirportApplication.java   entry point
│       │   │   ├── common/                   shared code
│       │   │   │   ├── config/               CorsConfig, ClockConfig
│       │   │   │   ├── controller/           HealthController
│       │   │   │   ├── exception/            ApiError, GlobalExceptionHandler, business exceptions
│       │   │   │   └── util/                 CodeGenerator (references, ticket numbers, tags)
│       │   │   ├── auth/  rh/  operations/   TEMPORARY Student A code (users, departments, flights)
│       │   │   └── passengers/               Student B, Module 1
│       │   │       ├── entity/               JPA entities and enums (statuses with their transitions)
│       │   │       ├── repository/           Spring Data repositories (JPQL with JOIN FETCH)
│       │   │       ├── dto/                  request / response records with validation
│       │   │       ├── service/              business rules
│       │   │       └── controller/           REST endpoints
│       │   └── resources/
│       │       ├── application.properties
│       │       └── db/migration/             Flyway SQL scripts
│       └── test/java/com/airport/passengers/ unit tests
└── frontend/                             Angular app
    ├── package.json
    ├── angular.json
    └── src/
        ├── index.html  main.ts  styles.css   styles.css = design system (tokens and shared classes)
        └── app/
            ├── app.ts  app.config.ts  app.routes.ts
            ├── core/                         app-wide services, loaded once
            │   ├── config/                   api.config.ts (API base URL)
            │   ├── interceptors/             auth header, error toasts
            │   ├── layout/                   sidebar, top bar, breadcrumb, navigation data
            │   ├── models/                   flight, health, API error types
            │   └── services/                 toasts, confirmation, health, flights
            ├── shared/                       reusable UI
            │   ├── components/               icon, page-header, state-message, toast-container,
            │   │                             confirm-dialog, departures-board
            │   └── pipes/                    stars
            └── features/
                ├── home/                     dashboard
                └── passengers/               Module 1 (lazy-loaded under /passengers)
                    ├── passengers.routes.ts
                    ├── models/               TypeScript mirrors of the Java DTOs + labels
                    ├── services/             one HTTP service per backend controller
                    ├── components/           seat-map
                    └── pages/                one folder per screen
```

Naming conventions: database tables and columns in French `snake_case` (`demande_assistance_passager`),
Java and TypeScript fields in French `camelCase` (`dateHeureDepart`), folders in English
(`passengers/`, `features/`), URLs in French (`/api/reclamations`).

## 15. Assumptions and missing information

Facts above were read from the project files. These points are not defined in the project:

- **Repository URL**: `<REPOSITORY_URL>`. The repository should be one Git repository at the `airport/`
  level containing `frontend/` and `backend/` (the Angular CLI may have created a separate Git
  repository inside `frontend/` when the project was generated).
- **Deployment**: no target, Docker file or CI pipeline exists: `<ADD_DEPLOYMENT_TARGET_AND_STEPS>`.
- **Authentication**: not implemented yet (Student A). Section 7 describes what the frontend already expects.
- **Student A's tables** in V1 are minimal placeholders; Student A extends them with migrations V3–V99.
- **Modules 2–5** of Student B (parking and transport, billing, purchases and stock, cargo and
  notifications) are planned and not in this repository yet.
- Commands are written for **Windows PowerShell**, the development environment of the project.
  On macOS or Linux, replace `$env:DB_PASSWORD = ...` with `export DB_PASSWORD=...`.# Airport-Management-System
