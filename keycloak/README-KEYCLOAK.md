# Keycloak Local Development Setup

This project includes a Docker Compose configuration to run Keycloak locally for development purposes.

## Prerequisites

- Docker and Docker Compose installed on your system
- Port 8180 available (or modify the port mapping in docker-compose.yml)

## Starting Keycloak

To start the Keycloak server, run:

```bash
docker-compose up -d keycloak
```

This will:
- Pull the Keycloak image (if not already present)
- Start Keycloak in development mode
- Expose Keycloak on http://localhost:8180 (standard Keycloak port)

## Accessing Keycloak

- **Admin Console**: http://localhost:8180/admin
- **Admin Username**: `admin`
- **Admin Password**: `admin`

## Initial Setup

- In the admin console, create the `frontend-app` realm using `frontend-app_realm.json`. This will create the users and roles needed for this challenge
- For each user, create a password using the admin console

## Stopping Keycloak

To stop the Keycloak server:

```bash
docker-compose down
```

To stop and remove all data (including realms and users):

```bash
docker-compose down -v
```
