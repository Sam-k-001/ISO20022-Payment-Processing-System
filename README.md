::: {align="center"}
 ISO 20022 Payment Processing System ## A Java and Spring Boot
backend for exploring modern financial payment workflows
[Java(https://img.shields.io/badge/Java-21-orange?logo=openjdk)\](https://openjdk.org/)
[Spring
Boot(https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F?logo=springboot)\](https://spring.io/projects/spring-boot)
[Maven(https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven)\](https://maven.apache.org/)
[Docker(https://img.shields.io/badge/Container-Docker-2496ED?logo=docker)\](https://www.docker.com/)
[License:
MIT(https://img.shields.io/badge/License-MIT-yellow.svg)\](LICENSE)
A backend project demonstrating payment APIs, transaction
management, security configuration, and ISO 20022-style XML message
generation.
:::

- # Overview The ISO 20022 Payment Processing System is a
Java 21 and Spring Boot application built to explore how a payment
service can expose REST APIs, validate payment requests, track
transactions, and generate structured financial messages. The codebase
follows a layered structure with controllers, services, repositories,
DTOs, domain entities, and centralized exception handling. It also
includes OpenAPI/Swagger documentation, application health and metrics
endpoints through Spring Boot Actuator, test classes, and Docker-related
configuration.  Project status: This repository is a learning
and portfolio project, not a production banking system. ISO 20022
message generation is implemented for the message types listed below;
full standards certification, real bank connectivity, and
production-grade payment controls are not implied. # Features -
Payment REST APIs for initiating transfers, checking transaction
status, and viewing paginated transaction history. - ISO 20022
message generation for pain.001, pacs.008, and
camt.053. - Request validation using Jakarta Bean
Validation. - Spring Security configuration and JWT-related
dependencies. - Transaction persistence layer using Spring Data
JPA and Hibernate. - Centralized error handling with custom
exceptions and structured error responses. - API documentation
using OpenAPI/Swagger UI. - Health and application metrics
through Spring Boot Actuator. - Automated tests for payment
service and controller behavior. - Docker configuration and a
Docker Compose file for a local service environment. # Technology
Stack  Area  Technologies  --  Language  Java 21
  Backend framework  Spring Boot 3.2.3, Spring Web   API style
 REST, JSON   Persistence  Spring Data JPA, Hibernate  
Databases configured  H2 for development; MySQL for the production
profile / Compose configuration   Security  Spring Security, JWT
libraries, BCrypt password encoder   Financial messaging  ISO
20022-style XML generation: pain.001, pacs.008, camt.053 
 Validation  Jakarta Bean Validation   API documentation 
Springdoc OpenAPI / Swagger UI   Monitoring  Spring Boot Actuator
  Build  Maven   Container tooling  Docker, Docker Compose
  Testing  Spring Boot Test, JUnit-based tests, Spring Security
Test  Frontend: The repository currently presents a Spring
Boot backend; a React frontend is not included in this repository
structure. # Architecture text Client / API Consumer  v REST
Controllers  v Service Layer   v v Payment ISO 20022 Processing
Message Generation  v Repository Layer (Spring Data JPA)  v
Configured Database (H2 development / MySQL production profile) 
The main code is organized by responsibility rather than placing all
logic in controllers. # ISO 20022 Message Types  Message  Purpose
 Current project role  ---  pain.001 
Customer credit transfer initiation  Generated for payment initiation
  pacs.008  Financial institution-to-financial institution
customer credit transfer  Generated as part of the message bundle 
 camt.053  Bank-to-customer statement  Generated as part of
the message bundle  The application exposes an endpoint to retrieve
the generated message bundle for a transaction. Generated XML should be
validated against the appropriate ISO 20022 schema before being treated
as interoperable with a real financial institution. # API Endpoints
The controller is rooted at /api/v1/payments.  Method  Endpoint
 Purpose  ---  POST 
/api/v1/payments/transfer  Initiate a payment transfer  
GET  /api/v1/payments/{transactionId}  Retrieve a payment by
transaction ID   GET  /api/v1/payments  Retrieve
paginated transaction history   GET 
/api/v1/payments/{transactionId}/messages  Retrieve generated ISO
20022 XML messages   GET  /api/v1/payments/health 
Payment service health information   GET  /actuator/health
 Spring Boot health endpoint  Payment endpoints are subject to the
applications Spring Security configuration. See the configuration and
API documentation for the current authentication behavior. # Getting
Started ## Prerequisites - JDK 21 - Maven 3.8+ (or use the Maven
Wrapper if you add one) - Git - Docker Desktop only if you want to use
the container configuration ## 1. Clone the repository bash git
clone
https://github.com/Sam-k-001/ISO20022-Payment-Processing-System.git cd
ISO20022-Payment-Processing-System  If your local folder contains
the Maven project in a subdirectory, change into the directory that
contains pom.xml before running Maven commands. ## 2. Build and
test bash mvn clean verify  ## 3. Run the application
bash mvn spring-boot:run  The default application port is
8080. The default configuration activates the dev profile
and uses an in-memory H2 database. ## 4. Open API documentation Once
the application starts, visit: - Swagger UI:
http://localhost:8080/swagger-ui.html\` - OpenAPI JSON:
http://localhost:8080/api-docs\` - Health:
http://localhost:8080/actuator/health\` Some payment endpoints require
authentication according to the configured security rules. # Database
and Configuration The default application.properties uses an
in-memory H2 database for development. The prod profile is
configured for MySQL. Before running the production profile, configure
database credentials through environment variables such as
DB_USERNAME and DB_PASSWORD. Do not commit real credentials, JWT
secrets, or other secrets to GitHub. Replace development secrets with
environment-provided values before any public deployment. The Docker
Compose file is located under docker/. Review its build context and
environment settings before running it from that directory, and use
non-default credentials for any environment beyond local testing. #
Tests Run the test suite with: bash mvn test  Test sources
are located under src/test/java. Test results depend on the current
code and local environment; run the commands above to confirm the latest
state. # Project Structure text iso20022-payment-system/ ├──
docker/ │ ├── Dockerfile │ └── docker-compose.yml ├── src/ │ ├── main/ │
│ ├── java/com/fintech/payment/ │ │ │ ├── config/ │ │ │ ├── controller/
│ │ │ ├── domain/ │ │ │ │ ├── dto/ │ │ │ │ ├── entity/ │ │ │ │ └──
enums/ │ │ │ ├── exception/ │ │ │ ├── iso20022/ │ │ │ │ ├── factory/ │ │
│ │ ├── generator/ │ │ │ │ └── model/ │ │ │ ├── repository/ │ │ │ └──
service/ │ │ └── resources/ │ └── test/ ├── pom.xml ├── README.md └──
LICENSE  # Roadmap The following are planned
improvements, not claims about features already implemented: -  
PostgreSQL and Flyway migrations -   Reliable asynchronous Kafka
payment workflow, including retries and dead-letter handling -  
Idempotency and stronger payment lifecycle controls -   Double-entry
ledger with balanced debit and credit entries -   Fraud rules and
persisted risk decisions -   Settlement and reconciliation
workflows -   Additional ISO 20022 messages: pacs.002,
pain.002, camt.056, and pacs.004 -   Prometheus metrics
and Grafana dashboards -   GitHub Actions CI pipeline -  
Kubernetes deployment manifests -   AWS deployment documentation and
infrastructure # Security and Scope This repository is intended for
educational and portfolio use. Do not use it to process real money or
sensitive customer data. A production payment platform would require,
among other things, verified authentication and authorization, secret
management, schema-validated ISO messages, idempotent processing, ledger
integrity, audit trails, operational monitoring, threat modeling, and
independent security testing. # Contributing Suggestions and
improvements are welcome. For a change, create a branch, describe the
problem being solved, add or update tests, and open a pull request. #
License This project is licensed under the MIT License. See
LICENSE(LICENSE) for details.
