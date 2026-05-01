# Liquibase Knowledge Summary for Spring Boot Projects

This quick guide explains how Liquibase works in this project and how to use it safely in a Clean Architecture setup.

## 1. Core Concepts and Mechanics

Liquibase does not scan your existing tables to guess what is missing. Instead, it relies on a tracking table named `DATABASECHANGELOG` in your database.

- **ChangeSet Identity Triplet**: Liquibase identifies each ChangeSet using:
  1. **ID**: Unique identifier defined in YAML.
  2. **Author**: Creator name defined in YAML.
  3. **File Path**: Path of the changelog file.
- **Checksum (fingerprint)**: Every ChangeSet has a hash based on its content. If you modify an already executed ChangeSet, Liquibase raises a checksum validation error to prevent history rewrites.

### Workflow

1. Liquibase reads `db.changelog-master.yaml`.
2. It checks the `DATABASECHANGELOG` table.
3. If a matching `ID/Author/Path` entry exists, it skips that ChangeSet.
4. If no entry exists, it executes the SQL and records the ChangeSet in the tracking table.

---

## 2. Essential Gradle Tasks (JavaExec)

In this project, these tasks run Liquibase CLI logic independently of Spring Boot startup:

| Task | Purpose | Use Case |
| --- | --- | --- |
| `liquibaseStatus` | Diagnosis | Check how many changes are pending in the database. |
| `liquibaseUpdate` | Execution | Apply new ChangeSets to the database. |
| `liquibaseUpdateSql` | Preview | Print SQL that would run without applying changes. |
| `liquibaseValidate` | Health check | Validate changelog syntax and history/checksum consistency. |

---

## 3. Spring Boot Lifecycle Integration

The `spring-boot-starter-liquibase` dependency automates migration execution.

- **Execution timing**: Liquibase runs right after app startup and before JPA/Hibernate or business logic initialization.
- **Benefit**: The database schema foundation is ready before application logic begins.
- **Control**: You can disable auto-run in `application.yml`:

```yaml
spring:
  liquibase:
    enabled: false
```

---

## 4. Golden Rules for Clean Architecture

1. **Never edit history**: After a ChangeSet is deployed, do not modify it. Create a new ChangeSet to fix issues.
2. **Keep it in infrastructure**: Liquibase scripts/configuration belong to the Infrastructure layer, not domain logic.
3. **Preserve domain independence**: Keep the Domain layer free of database concerns; Liquibase supports infrastructure persistence needs.
4. **Protect changelog files**: Changelogs are critical project artifacts; losing them blocks environment setup for new developers.

This document serves as a quick-start reference for safe database migration management in this project.
