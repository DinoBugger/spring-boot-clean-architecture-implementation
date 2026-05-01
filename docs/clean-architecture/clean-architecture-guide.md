# Clean Architecture Guide

This document explains how to apply **Clean Architecture** in this Spring Boot project, how to build it step by step, what each layer is responsible for, and what the main strengths and weaknesses are.

The goal is to help contributors keep the core business logic independent from frameworks, databases, and delivery mechanisms such as REST APIs.

---

## 1. What Clean Architecture is

Clean Architecture is a way of organizing code so that the **business rules stay at the center** of the system and the outer details stay at the edges.

The main idea is simple:    

- the **domain** should not depend on Spring Boot, JPA, Liquibase, or any delivery framework
- the **application/use case layer** should coordinate business actions without knowing the infrastructure details
- the **interfaces/adapters layer** should translate between external systems and the core business logic

A common mental model is a set of concentric circles:

```text
[ Frameworks / Drivers ]
[ Adapters / Infrastructure ]
[ Application / Use Cases ]
[ Domain / Entities ]
```

Dependencies always point **inward**.

That means the domain and use cases should never import outer framework code.

---

## 2. Why this fits this Spring Boot project

This repository is a Spring Boot application, so it naturally has:

- web delivery through Spring MVC
- database access through JPA / JDBC
- schema migration through Liquibase
- runtime logging through Log4j2
- environment-specific configuration through YAML files

Those tools are useful, but they are **implementation details**.

Clean Architecture helps keep those details outside of the business core so that:

- the business rules stay testable without a database
- the app can change persistence or web frameworks later with less impact
- the codebase becomes easier to reason about as it grows

This is especially useful because the current project already has:

- `src/main/java/com/dinobugger/cleanproject/CleanprojectApplication.java`
- `src/main/resources/application-dev.yaml`
- `src/main/resources/application-test.yaml`
- Liquibase changelog files under `src/main/resources/db/changelog/`
- Log4j2 configuration under `src/main/resources/log4j2-spring.xml`

Those files belong to the outer layers, while the domain and use cases should remain framework-agnostic.

---

## 3. Recommended layer structure for this repository

A practical package layout for this project could be:

```text
com.dinobugger.cleanproject
├── domain
│   ├── model
│   ├── service
│   └── event
├── application
│   ├── port
│   │   ├── in
│   │   └── out
│   └── usecase
├── adapter
│   ├── in
│   │   └── web
│   └── out
│       ├── persistence
│       └── external
└── config
```

This is only a suggested layout, but it follows the clean dependency direction very well.

---

## 4. What each component does

### 4.1 Domain layer

The domain layer contains the most important business concepts.

Typical contents:

- entities
- value objects
- domain services
- domain events
- business rules

Responsibilities:

- represent the core business language
- enforce business invariants
- stay free of Spring annotations if possible

Example responsibilities:

- `User` entity
- `EmailAddress` value object
- business rule such as “a user email must be unique”

Domain code should not know:

- how data is stored
- how HTTP requests arrive
- how Liquibase manages schema
- how logs are written

---

### 4.2 Application layer

The application layer coordinates business workflows.

Typical contents:

- use cases
- application services
- ports (interfaces)

Responsibilities:

- execute one business action at a time
- orchestrate domain objects and repositories
- define boundaries for input and output

Example use cases:

- create user
- update user profile
- list users
- delete user

This layer usually defines interfaces such as:

- input ports: what the application can do
- output ports: what the application needs from the outside world

A use case should not directly depend on JPA repositories or controllers.
It should depend on interfaces instead.

---

### 4.3 Adapter / interface layer

This layer converts external input into the format expected by the application layer.

Typical contents:

- REST controllers
- request/response DTOs
- persistence adapters
- message consumers/producers
- external API clients

Responsibilities:

- parse HTTP requests
- validate transport-level data
- map DTOs to application commands
- map application results back to API responses
- talk to the database or external services through adapters

In a Spring Boot project, this is where most framework annotations usually live:

- `@RestController`
- `@Component`
- `@Repository`
- `@Configuration`

The key rule is that these classes should depend on the application layer, not the other way around.

---

### 4.4 Infrastructure layer

Infrastructure is the outermost layer that holds technical implementations.

Typical contents:

- JPA entities
- Spring Data repositories
- Liquibase migrations
- database configuration
- filesystem adapters
- third-party integrations

Responsibilities:

- implement the ports defined by the application layer
- manage technical persistence details
- connect the application to PostgreSQL, H2, or any other external system

In this repository, the following items already belong to infrastructure concerns:

- `build.gradle`
- `src/main/resources/db/changelog/`
- `src/main/resources/application-dev.yaml`
- `src/main/resources/application-test.yaml`
- database-related configuration in Spring

---

## 5. How to build Clean Architecture in this project

Use this sequence when creating a new business feature.

### Step 1: Start from the use case

Define the business action first.

Ask:

- What should the system do?
- What input does it need?
- What output should it return?

Example:

- `CreateUserUseCase`
- `FindUserByIdUseCase`

At this stage, do not think about JPA or controllers yet.

---

### Step 2: Define the ports

Create the interfaces the use case needs.

Example:

- `UserRepositoryPort`
- `PasswordEncoderPort`
- `EventPublisherPort`

These interfaces belong to the application layer because they describe what the use case needs from the outside.

---

### Step 3: Implement the domain model

Add the business entities and rules.

Example:

- `User`
- `UserId`
- `EmailAddress`

Keep the domain model simple and focused on business meaning.

---

### Step 4: Add adapters

Implement the input and output adapters.

Example adapters:

- REST controller for HTTP input
- JPA adapter for persistence output
- Liquibase migration for schema creation

These adapters should convert external data into the shape used by the application layer.

---

### Step 5: Wire everything in configuration

Use Spring configuration classes only at the edges.

Example:

- define beans
- connect ports to adapter implementations
- configure serialization, database, and infrastructure details

The core layers should remain independent from Spring configuration.

---

### Step 6: Test the core first

Write tests around the domain and use cases before testing the database or web layer.

Recommended test order:

1. domain unit tests
2. use case tests with mocked ports
3. adapter tests
4. integration tests

This gives fast feedback and protects the business rules from framework changes.

---

## 6. How the current project files fit the architecture

The repository already contains several files that naturally belong to the outer layers.

### Application entry point

`src/main/java/com/dinobugger/cleanproject/CleanprojectApplication.java`

This is the Spring Boot bootstrap class. It belongs to the framework boundary, not the domain.

### Development configuration

`src/main/resources/application-dev.yaml`

This file configures:

- PostgreSQL datasource
- Liquibase changelog
- JPA validation
- Docker Compose support

This is infrastructure configuration.

### Test configuration

`src/main/resources/application-test.yaml`

This file configures:

- H2 in-memory database
- Liquibase changelog
- schema validation
- Docker Compose disabled for tests

This is also infrastructure configuration.

### Database migration

`src/main/resources/db/changelog/db.changelog-master.yaml`

This is part of the persistence/infrastructure layer because it manages database schema evolution.

### Logging setup

`src/main/resources/log4j2-spring.xml`

This is a runtime infrastructure concern.

---

## 7. Strong points of Clean Architecture

### 1. Business rules are easier to protect

The core logic is isolated from database and web framework details, so it is less likely to break when the outer layers change.

### 2. Testing is easier

You can test the domain and use cases without starting Spring, PostgreSQL, or Docker.

### 3. Code becomes easier to understand

Each layer has a clear responsibility, which helps new contributors read the code faster.

### 4. Framework changes become less painful

If you switch from JPA to another persistence technology, only the infrastructure layer should change.

### 5. Better long-term maintainability

As the project grows, the separation of concerns prevents everything from being mixed into controllers or repositories.

---

## 8. Weak points and tradeoffs

Clean Architecture is powerful, but it is not free.

### 1. More initial structure

You must create more packages, interfaces, and mappers than in a simple layered CRUD app.

### 2. More boilerplate

Mapping between DTOs, commands, entities, and persistence models can create extra code.

### 3. Slower to start for tiny projects

If the application is very small, a strict clean architecture can feel heavier than necessary.

### 4. Team discipline is required

The architecture only works well if everyone respects the dependency direction.

### 5. Over-engineering risk

If you add too many abstractions too early, the code may become harder to follow instead of easier.

---

## 9. Practical rules for contributors

Use these rules when adding new code to this repository.

1. Put business rules in the domain layer.
2. Put use case orchestration in the application layer.
3. Keep Spring MVC controllers thin.
4. Keep JPA repositories and Liquibase files in infrastructure-related locations.
5. Do not let domain code import Spring Boot annotations unless there is a strong reason.
6. Keep dependencies pointing inward.
7. Write unit tests for the domain and use cases first.
8. Add integration tests only where infrastructure behavior must be verified.
9. Treat database migrations as infrastructure, not domain logic.
10. Keep logging and environment configuration outside the business core.

---

## 10. Example feature flow

A typical feature should follow this flow:

1. A request enters through a controller.
2. The controller converts the request into an application command.
3. The use case executes the business rule.
4. The use case calls an output port.
5. A repository adapter stores or loads data.
6. The adapter returns the result.
7. The use case returns a response model.
8. The controller maps it back to HTTP.

This flow keeps the domain core protected from web and persistence details.

---

## 11. Recommended testing strategy

Because this project already has a separate test profile, Clean Architecture fits well with a layered testing approach.

### Domain tests

Use plain unit tests for business objects and rules.

### Application tests

Mock output ports and verify use case behavior.

### Adapter tests

Test controllers and persistence adapters separately.

### Integration tests

Use the `test` profile with H2 and Liquibase to verify the full Spring Boot wiring.

This matches the current repository setup and keeps test feedback fast.

---

## 12. Summary

Clean Architecture is a good fit for this repository because it keeps the business core independent from Spring Boot, JPA, Liquibase, and logging details.

The recommended approach is:

- domain first
- use cases second
- ports to define boundaries
- adapters and infrastructure at the edges
- tests for each layer

If you follow those rules, the project will stay easier to maintain, easier to test, and easier to extend.

