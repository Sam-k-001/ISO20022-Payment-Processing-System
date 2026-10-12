# ISO 20022 Payment Processing System

<div align="center">

**A Java-based payment processing backend built with Spring Boot, featuring secure REST APIs, transaction management, and ISO 20022 financial messaging.**

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven)
![Docker](https://img.shields.io/badge/Docker-Supported-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

</div>

## Project Overview

The ISO 20022 Payment Processing System is a backend application developed using Java 21 and Spring Boot 3 to demonstrate payment processing workflows and standardized financial messaging. It provides RESTful APIs for handling payment requests, validating transaction data, tracking payment records, and generating ISO 20022-style XML messages.

The application follows a layered architecture that separates controllers, services, repositories, DTOs, and domain entities. It also incorporates Spring Security, JWT-related authentication components, centralized exception handling, API documentation, automated tests, and Docker configuration. This project serves as a practical exploration of backend engineering and financial technology concepts.

## Key Features

- Payment processing through RESTful APIs.
- ISO 20022 message generation for `pain.001`, `pacs.008`, and `camt.053`.
- Spring Security and JWT-related security components.
- Payment request validation and transaction management.
- Spring Data JPA and Hibernate for database access.
- Centralized exception handling and structured error responses.
- Swagger/OpenAPI API documentation.
- Spring Boot Actuator health and monitoring endpoints.
- Unit and controller testing.
- Docker and Docker Compose configuration.

## Technology Stack

| Category | Technologies |
|---|---|
| Programming Language | Java 21 |
| Backend Framework | Spring Boot 3 |
| API Development | Spring Web, REST APIs |
| Security | Spring Security, JWT |
| Persistence | Spring Data JPA, Hibernate |
| Database | H2 for development; MySQL configured for the production profile |
| Financial Messaging | ISO 20022 |
| API Documentation | Swagger/OpenAPI |
| Monitoring | Spring Boot Actuator |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |
| Testing | Spring Boot Test, JUnit |

## System Architecture

The application uses a layered backend architecture to separate responsibilities and make the code easier to maintain and extend.

```text
Client / API Consumer
        |
        v
REST Controllers
        |
        v
Service Layer
        |
        +----------------------+
        |                      |
        v                      v
Payment Processing      ISO 20022 Message
        |                 Generation
        v
Repository Layer
        |
        v
Configured Database
```

## ISO 20022 Financial Messaging

The project explores the following ISO 20022 message types:

- **pain.001:** Customer credit transfer initiation.
- **pacs.008:** Financial institution-to-financial institution customer credit transfer.
- **camt.053:** Bank-to-customer statement.

These message generators demonstrate the structure and purpose of standardized financial messages. Additional schema validation and integration testing would be needed before using generated messages with real financial institutions.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 21
- Maven
- Git
- Docker Desktop (optional)

### Clone the Repository

```bash
git clone https://github.com/Sam-k-001/ISO20022-Payment-Processing-System.git
cd ISO20022-Payment-Processing-System
```

### Build the Project

```bash
mvn clean verify
```

### Run the Application

```bash
mvn spring-boot:run
```

The application is configured to run on port `8080` by default. Check the application configuration for the active profile and database settings.

## API Documentation

After starting the application, try these URLs if enabled by the current configuration:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api-docs`
- **Application Health:** `http://localhost:8080/actuator/health`

Authentication requirements depend on the configured Spring Security rules.

## Testing

Run the automated test suite with:

```bash
mvn test
```

Run the complete Maven verification lifecycle with:

```bash
mvn clean verify
```

## Docker

The repository contains Docker-related configuration under the `docker/` directory. Review the Dockerfile, Compose build context, database credentials, and environment variables before starting the containerized application.

Never commit real passwords, JWT secrets, or production credentials to the repository.

## Project Roadmap

The following improvements are planned and should be marked complete only after implementation and testing:

- [ ] PostgreSQL and Flyway database migrations.
- [ ] Kafka-based asynchronous payment processing.
- [ ] Idempotent payment processing and reliable event handling.
- [ ] Double-entry ledger service.
- [ ] Fraud detection service.
- [ ] Settlement and reconciliation workflows.
- [ ] Additional ISO 20022 messages: `pacs.002`, `pain.002`, `camt.056`, and `pacs.004`.
- [ ] Prometheus and Grafana monitoring.
- [ ] GitHub Actions CI/CD pipeline.
- [ ] Kubernetes deployment.
- [ ] AWS deployment.

## Project Scope

This is an educational and portfolio project intended to demonstrate backend development, payment workflow concepts, and financial messaging. It is not a production banking platform and should not be used to process real payments or sensitive customer data without substantial additional engineering, security review, compliance validation, and testing.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
