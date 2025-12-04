# Workshop Manager - Project Instructions

## Overview
A car workshop management system (monorepo) with a Spring Boot backend and Vue.js frontend.

**Language**: Polish (all UI labels, error messages, validation messages)

---

## Project Structure

```
workshop-manager/
├── backend/                 # Spring Boot REST API (Java 17+)
│   ├── src/main/java/com/fzemi/workshopmanager/
│   ├── src/main/resources/
│   ├── pom.xml
│   └── docker-compose.yml   # PostgreSQL database
├── frontend/                # Vue 3 + PrimeVue + Tailwind CSS v4
│   ├── src/
│   ├── package.json
│   ├── vite.config.js
│   └── AGENTS.md            # Detailed frontend instructions
└── upload-dir/              # File uploads storage
```

---

## Backend (Spring Boot)

### API Base URL
```
http://localhost:8080/api/v1
```

### Key Endpoints
- `/api/v1/auth/login` - Authentication
- `/api/v1/clients` - Client management
- `/api/v1/vehicles` - Vehicle management
- `/api/v1/repairs` - Repair management

### Package Structure
```
com.fzemi.workshopmanager/
├── config/          # Security, CORS, Web config
├── client/          # Client entity, controller, service, repository
├── vehicle/         # Vehicle entity, controller, service, repository
├── repair/          # Repair entity, controller, service, repository
└── auth/            # Authentication
```

---

## Frontend (Vue 3)

### Dev Server
```
http://localhost:5173
```

> **See `frontend/AGENTS.md`** for detailed frontend patterns, components, and code examples.

---

## Conventions

1. **Polish Language** - All user-facing text in Polish
2. **REST API** - Standard REST conventions with `/api/v1` prefix
3. **Error Codes** - Numeric codes (10xx security, 11xx repairs, 12xx vehicles, 13xx clients)
4. **Date Format** - ISO format (YYYY-MM-DD) in API, formatted for display
5. **Authentication** - JWT Bearer token in Authorization header
