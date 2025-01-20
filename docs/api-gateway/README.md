# API Gateway

## Introduction
The API Gateway serves as the central entry point for client requests in the microservices architecture. It acts as an intelligent router that handles authentication, request routing, and efficient communication between clients and various microservices. By centralizing these concerns, the API Gateway simplifies client interactions while ensuring secure and optimized service delivery.

## Core Features

### OpenAPI Documentation
- Comprehensive API documentation using OpenAPI 3.0 annotations
- Clear visibility into available endpoints, request/response schemas, and authentication requirements
- Auto-generated API documentation that stays synchronized with the codebase

### Feign Client Integration
- Declarative REST client implementation using OpenFeign
- Simplified service-to-service communication
- Type-safe interface for interacting with other microservices

### JWT Authentication
- Secure endpoint protection using JSON Web Tokens (JWT)
- Token validation and user authentication for protected routes
- Seamless integration with the Auth Service for token management

### Swagger UI
- Interactive API testing interface available at `http://localhost:8080/swagger-ui.html`
- Real-time endpoint testing capabilities
- Request/response visualization and documentation

## API Endpoints

Access the complete API documentation and testing interface:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Services Integration

The API Gateway communicates with the following microservices:

### Auth Service
- Manages user authentication and authorization
- Handles JWT token generation and validation
- Base URL: `http://localhost:8081`

### User Service
- Manages user profiles and user-related operations
- Handles user data validation and verification
- Base URL: `http://localhost:8081`

### Task Service
- Manages task creation, updates, and deletion
- Handles task assignments and status tracking
- Base URL: `http://localhost:8082`

### Label Service
- Manages task categorization and tagging
- Handles label creation and assignment
- Base URL: `http://localhost:8083`

## Execution Flow

Below is the request flow diagram showing how the API Gateway handles incoming requests:

```mermaid
sequenceDiagram
    participant Client
    participant API Gateway
    participant Auth Service
    participant User Service
    participant Task Service
    participant Label Service

    Client->>API Gateway: HTTP Request with JWT
    API Gateway->>Auth Service: Validate JWT Token
    Auth Service-->>API Gateway: Token Valid/Invalid

    alt Token Valid
        API Gateway->>User Service: Forward User Request
        User Service-->>API Gateway: User Response
        
        opt Task Operations
            API Gateway->>Task Service: Forward Task Request
            Task Service-->>API Gateway: Task Response
        end

        opt Label Operations
            API Gateway->>Label Service: Forward Label Request
            Label Service-->>API Gateway: Label Response
        end

        API Gateway-->>Client: Success Response
    else Token Invalid
        API Gateway-->>Client: 401 Unauthorized
    end
```

## Security

### JWT Authentication Flow
1. Clients include JWT tokens in the Authorization header
2. API Gateway validates tokens with the Auth Service
3. Valid tokens allow access to protected resources
4. Invalid tokens result in 401 Unauthorized responses

### Token Format
- Bearer token in Authorization header
- Example: `Authorization: Bearer eyJhbGciOiJIUzI1NiIs...`

## How to Run

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- Spring Boot 3.x

### Setup and Running
1. Clone the repository:
```bash
git clone https://github.com/Nadira3/afrikai_apis.git
cd api-gateway
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The API Gateway will be available at `http://localhost:8080`

### Configuration
- Update `application.properties` or `application.yml` to configure:
  - Service URLs
  - Security settings
  - Port configuration
  - Logging levels

## License

This project is licensed under the ALX Backend Curriculum License.

Copyright (c) 2024 ALX Backend Curriculum. All rights reserved.
