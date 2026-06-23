# Frontend App with Keycloak

A Spring Boot application demonstrating Keycloak integration for authentication and authorization.

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java 17 or higher** - Required to run the Spring Boot application
- **Docker and Docker Compose** - Required to run Keycloak locally
- **Port 8080** - Must be available for the application
- **Port 8180** - Must be available for Keycloak

## Setup Instructions

### 1. Setup Keycloak

Follow the instructions in [keycloak/README-KEYCLOAK.md](keycloak/README-KEYCLOAK.md) to:
- Start Keycloak using Docker Compose
- Access the Keycloak Admin Console
- Configure the `frontend-app` realm
- Create user passwords

### 2. Run the Application

Use the Gradle wrapper to start the application:

**On Linux/macOS:**
```bash
./gradlew bootRun
```

**On Windows:**
```bash
gradlew.bat bootRun
```

Alternatively, you can build and run the JAR:
```bash
./gradlew build
java -jar build/libs/frontendapp-0.0.1-SNAPSHOT.jar
```

### 3. Access the Application

Once the application is running, navigate to:

**http://localhost:8080**

You will be redirected to Keycloak for authentication. Use the credentials you configured during the Keycloak setup.

## Users and Access Levels

The application includes three test users with different permission levels:

### user1
- **Role**: Does not have `my-role` role
- **Access**: Cannot access calendar APIs
- **Use Case**: Demonstrates role-based access control

### user2
- **Role**: Has `my-role` role but no 2FA configured
- **Access**: Can only access the `/calendar/today` API
- **Use Case**: Demonstrates basic role-based access

### user3
- **Role**: Has `my-role` role and 2FA configured
- **Access**: Can access all calendar APIs (`/calendar/today` and `/calendar/thisMonth`)
- **Use Case**: Demonstrates full access with 2FA

**Important Note for user3**: When you set up OTP (One-Time Password) for the first time, the `amr` (Authentication Methods Reference) claim will not be populated immediately. You will need to **logout and login again** after configuring OTP to access the `/calendar/thisMonth` API.

## Additional Information

- The application uses OAuth 2.0 / OpenID Connect for authentication
- Keycloak handles user management and authentication
- The application includes role-based access control and 2FA enforcement features