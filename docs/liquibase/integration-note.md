## Liquibase Integration Note

This document explains how Liquibase is wired into this Spring Boot project, how to run it locally, and how to verify the setup if you want to implement yourself.

The current project stack is:

- Java 21
- Spring Boot 4.0.4
- Gradle wrapper
- PostgreSQL 16 for local development
- H2 in-memory database for tests
- Liquibase 5.x for schema versioning

---

## 0. If you are new to Liquibase
I can elaborate the basic is this
You must so familiar with git, liquibase does the same thing but for database schema(table)
It can be used to store the history of modifying on a specific table like add a column, change a column type, add an index, etc. It also can be used to create a new table from scratch(usually start with a prefix `0001_`)
Let's say when you are working on a project
Stage 1: Your job is creating a new table named `user` with 2 columns: `id`, `name`,
         you don't need to type sql command in PostgreSQL, instead you can create a new file named `0001_create_user_table.yaml` under `src/main/resources/db/changelog/changes/` with the content just like `0001_create_liquibase_example_table.yaml`, but with your columns
         When the app run, liquibase check database if these is not any table users it will automatically execute `CREATE TABLE ..`
Stage 2:  Now when your boss ask you to add a new column `email` to table `user`,
          image this the first yaml you created is just like a commit in git history, so you must not edit or rewrite it,
          DO: create a new file  named `0002_add_email_to_user.yaml` which the same with 0001 but with an additional change set to add the new column, then add this file to `db.changelog-master.yaml`'
          When the app run, liquibase will check if the column `email` exist in table `user`, if not it will execute `ALTER TABLE .. ADD COLUMN ..`
Stage 3: When teamwork
         Let's say a team member pull this project, they will never have to ask about what you have done to the dataschema,
         They just need to look at the changes directory

## 1. What Liquibase does in this project

Liquibase is used to manage database schema changes in a controlled and repeatable way.

Instead of creating tables manually, the database schema is described in YAML changelog files under `src/main/resources/db/changelog/`.

At application startup, Spring Boot reads the changelog and applies any missing change sets before the application begins serving requests.

In this repository, Hibernate is configured with `ddl-auto: validate`, which means:

- Liquibase is responsible for creating and evolving the schema
- Hibernate only checks that the entity model matches the database schema

This helps avoid accidental schema drift.

---

## 2. Project files involved in the Liquibase setup

The main files you should know are:

- `build.gradle` — Gradle and Liquibase plugin configuration
- `src/main/resources/application-dev.yaml` — development datasource and Liquibase configuration
- `src/main/resources/application-test.yaml` — test datasource and Liquibase configuration
- `src/main/resources/db/changelog/db.changelog-master.yaml` — master changelog file
- `src/main/resources/db/changelog/changes/0001_create_liquibase_example_table.yaml` — first example change set
- `src/test/java/com/dinobugger/cleanproject/database/IntegrationDatabaseTest.java` — database connectivity test

---

## 3. Prerequisites

Before running the project, make sure you have:

1. **Java 21** installed
2. **Docker Desktop** installed and running if you want to use the local PostgreSQL container (Remember to run docker desktop before starting the app in dev profile, otherwise it will fail to connect to the database)
3. A terminal that can run the Gradle wrapper (You can use the one integrated in your IDE or a standalone terminal)

The project uses the Gradle wrapper, so you do **not** need a global Gradle installation.

---

## 4. Database configuration for local development

Local development is configured for PostgreSQL.

### Default values

The repository provides default database settings in `gradle.properties`:

- URL: `jdbc:postgresql://localhost:5432/spring_template_db?sslmode=disable`
- Username: `user_admin`
- Password: `password_secret`

These values are also used as defaults in `application-dev.yaml`.

### Environment variables

You can override the defaults with these environment variables:

- `DB_POSTGRES_URL`
- `DB_POSTGRES_USERNAME`
- `DB_POSTGRES_PASSWORD`

The Gradle build and the Spring Boot application both read these values.

---

## 5. How the Gradle build is wired

The important parts of `build.gradle` are:

- `org.liquibase.gradle` plugin for Liquibase tasks
- `org.liquibase:liquibase-core` for the runtime migration engine
- `liquibaseRuntime` dependencies for running Liquibase from Gradle

The Liquibase Gradle activity is configured like this:

- changelog file: `src/main/resources/db/changelog/db.changelog-master.yaml`
- database URL, username, password: taken from the same DB properties used by the app
- driver: `org.postgresql.Driver`

The `bootRun` task also passes the database environment variables so the application can start with the same configuration.

---

## 6. Application-side Liquibase configuration

### Development profile

In `src/main/resources/application-dev.yaml`:

- the datasource points to PostgreSQL
- Liquibase is enabled with:

  - `spring.liquibase.change-log: classpath:db/changelog/db.changelog-master.yaml`

- Hibernate validation is enabled with:

  - `spring.jpa.hibernate.ddl-auto: validate`

This means the application expects the schema to already exist and match the model after Liquibase has run.

### Test profile

In `src/main/resources/application-test.yaml`:

- the datasource uses **H2 in-memory** database
- Liquibase uses the same master changelog
- Docker Compose is disabled for tests

This makes the test suite self-contained and independent from a local PostgreSQL or Docker setup.

---

## 7. Liquibase changelog structure

The changelog structure is intentionally simple:

```text
src/main/resources/db/changelog/
├── db.changelog-master.yaml
└── changes/
	└── 0001_create_liquibase_example_table.yaml
```

### Master changelog

`db.changelog-master.yaml` is the entry point. It includes one or more change set files.

### Change set file

`0001_create_liquibase_example_table.yaml` currently creates a sample table named `liquibase_example` with:

- `id` as a primary key
- `name` as a required text column
- `created_at` as a required timestamp column

The timestamp column uses a portable `CURRENT_TIMESTAMP` default so the change set can run on both PostgreSQL and H2.

---

## 8. How to run the project locally

### Option A: Run against PostgreSQL with Docker Compose

Start the application in the dev profile:

```powershell
.\gradlew.bat bootRun
```

The dev profile uses `docker-compose.yaml`, so Docker should be running.

If you want to override the defaults, set:

```powershell
$env:DB_POSTGRES_URL="jdbc:postgresql://localhost:5432/spring_template_db?sslmode=disable"
$env:DB_POSTGRES_USERNAME="user_admin"
$env:DB_POSTGRES_PASSWORD="password_secret"
```

Then run `bootRun` again.

### Option B: Run with custom PostgreSQL settings

If you already have a PostgreSQL instance, set the three `DB_POSTGRES_*` variables to point to it and then start the app normally.

---

## 9. How to verify Liquibase

### Build and compile check

You can confirm the project still loads correctly with:

```powershell
.\gradlew.bat help
.\gradlew.bat classes
```

### Test run

The test profile is designed to be self-contained. Run the test suite with the test profile active:

```powershell
.\gradlew.bat --% -Dspring.profiles.active=test test
```

Expected result:

- Spring Boot starts with the `test` profile
- Liquibase runs using the shared master changelog
- The integration test executes a simple `SELECT 1` query through `JdbcTemplate`
- The build finishes successfully

The current integration test is `IntegrationDatabaseTest`.

---

## 10. How to add a new Liquibase changeset

When you need to modify the schema, follow this pattern:

1. Create a new file under `src/main/resources/db/changelog/changes/`
2. Name it with a clear sequence prefix, for example:

   - `0002_add_customer_table.yaml`
   - `0003_add_indexes.yaml`

3. Add the new file to `db.changelog-master.yaml`
4. Keep each changeset small and focused
5. Prefer portable SQL and data types when possible
6. Do not edit older change sets after they have been shared or applied in other environments

### Good practice

- one logical schema change per changeset
- use descriptive `id` values
- keep the `author` field consistent
- avoid relying on database-specific behavior unless it is intentional

---

## 11. Troubleshooting

### 1. Missing database properties

If the app cannot connect to PostgreSQL, check that these values exist:

- `DB_POSTGRES_URL`
- `DB_POSTGRES_USERNAME`
- `DB_POSTGRES_PASSWORD`

If they are not set, the project falls back to the defaults in `gradle.properties` and `application-dev.yaml`.

### 2. Changelog file not found

If Liquibase reports that it cannot find the master changelog, verify this path:

```text
classpath:db/changelog/db.changelog-master.yaml
```

Also make sure the file exists under `src/main/resources/db/changelog/`.

### 3. Schema validation errors

If Hibernate fails with `ddl-auto: validate`, it usually means:

- the changelog did not create the expected table or column
- the entity model does not match the database schema
- a data type differs between the schema and the model

### 4. Test profile startup issues

The test profile uses H2 in PostgreSQL mode.

If a test fails, check that:

- the test profile is active
- `application-test.yaml` still points to the H2 datasource
- `spring.liquibase.change-log` is set correctly

---

## 12. Repository notes

- The Java package is `com.dinobugger.cleanproject`
- The Gradle group is `com.cleanproject`
- The sample table created by Liquibase is only a starter example
- You should replace it with real domain tables as the project grows

---

## 13. Quick summary

If you only remember three things, remember these:

1. **Development** uses PostgreSQL and Liquibase through `application-dev.yaml`
2. **Tests** use H2 in memory and the same Liquibase changelog
3. **Schema changes** should always be added as new Liquibase changesets, not manual database edits

That is the safest workflow for keeping the project consistent across environments.

