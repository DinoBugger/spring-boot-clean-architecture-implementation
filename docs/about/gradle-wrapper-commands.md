# Gradle Wrapper Commands

This guide lists the most useful Gradle Wrapper and also commands that I configure for this project.

> On Windows PowerShell, run commands from the project root with `.\gradlew.bat`.

## General usage

```powershell
.\gradlew.bat <task>
```

Examples:

```powershell
.\gradlew.bat help
.\gradlew.bat tasks --all
.\gradlew.bat build
```

## Common commands
# Gradle Wrapper Commands (project-specific)

This document summarizes the useful Gradle Wrapper commands for this project and documents a small workaround for Liquibase migrations on the project's Gradle/Plugin combination.

Note: run commands from the project root on Windows PowerShell with:

```powershell
.\gradlew.bat <task>
```

Examples:

```powershell
.\gradlew.bat help
.\gradlew.bat tasks --all
.\gradlew.bat build
```

## Common commands

| Command | Purpose |
| --- | --- |
| `.\gradlew.bat help` | Shows Gradle help and usage information. |
| `.\gradlew.bat tasks` | Lists common tasks available in the project. |
| `.\gradlew.bat tasks --all` | Lists every available task, including plugin-generated tasks. |
| `.\gradlew.bat clean` | Deletes the `build/` directory. |
| `.\gradlew.bat build` | Compiles code, runs tests, and assembles the project. |
| `.\gradlew.bat test` | Runs the test suite. |
| `.\gradlew.bat check` | Runs all verification tasks. |
| `.\gradlew.bat classes` | Compiles the main source code. |
| `.\gradlew.bat testClasses` | Compiles the test source code. |
| `.\gradlew.bat bootRun` | Starts the Spring Boot application locally. |
| `.\gradlew.bat bootJar` | Builds an executable Spring Boot JAR. |
| `.\gradlew.bat dependencies` | Displays declared dependencies. |
| `.\gradlew.bat dependencyInsight` | Shows why a dependency is present. |
| `.\gradlew.bat javaToolchains` | Shows detected Java toolchains. |

## Project-specific commands

### Development workflow

Make sure Docker Desktop is running when you use the compose tasks.

| Command | Purpose |
| --- | --- |
| `.\gradlew.bat runDev` | Starts the development workflow: brings up the `dev` Docker Compose environment and then runs the application with `bootRun`. |
| `.\gradlew.bat devComposeUp` | Start the development Docker Compose services (`docker-compose.dev.yaml`). |
| `.\gradlew.bat devComposeDown` | Stop and remove the development containers. |
| `.\gradlew.bat devComposeLogs` | Show logs from the development containers. |
| `.\gradlew.bat devComposeBuild` | Build images used by the development compose setup. |
| `.\gradlew.bat devComposePull` | Pull/refresh images for the development compose setup. |

### Database / Liquibase

Background: the project declares the Gradle Liquibase plugin (`org.liquibase.gradle` v2.2.2). On Gradle 9.x this plugin's built-in tasks are not compatible (they call Gradle APIs in a way that causes a MissingMethodException). Running the plugin-provided `update` task may therefore fail with a method/compatibility error.

Workaround implemented in the build (safe and non-invasive): additional Gradle tasks have been added that run the Liquibase CLI via the `liquibaseRuntime` classpath using `JavaExec`. These tasks avoid the plugin's incompatible task implementation and let you run migrations directly from Gradle.

Available (new) tasks and usage:

| Task | Purpose |
| --- | --- |
| `.\gradlew.bat liquibaseStatus` | Show pending Liquibase changesets (safe check). |
| `.\gradlew.bat liquibaseUpdate` | Apply pending Liquibase changesets (runs the Liquibase CLI using the runtime classpath). |
| `.\gradlew.bat liquibaseUpdateSql` | Print the SQL that would be executed for pending changes. |
| `.\gradlew.bat liquibaseValidate` | Validate the changelog for errors. |

Examples:

```powershell
# Check whether there are pending changes
.\gradlew.bat liquibaseStatus

# Apply migrations
.\gradlew.bat liquibaseUpdate

# Print SQL for pending changes (dry-run)
.\gradlew.bat liquibaseUpdateSql

# Validate changelog
.\gradlew.bat liquibaseValidate
```

If you prefer to run Liquibase using another approach, alternatives are:
- Start the Spring Boot application (it is configured to run Liquibase at startup) and let Spring Boot apply migrations:

```powershell
.\gradlew.bat bootRun
```

- Use the Liquibase Docker image or local Liquibase CLI to run migrations outside Gradle.

### Notes about the original `update` task

You may still see the plugin-provided `update` task (from `org.liquibase.gradle`). On this project + Gradle 9.4.1 that task can fail with an exception like:

"No signature of method: org.liquibase.gradle.LiquibaseTask.exec() is applicable..."

This indicates the plugin's task implementation is incompatible with the Gradle API. The safe approaches are:
- Use the `liquibase*` tasks added to the build (recommended). These run Liquibase directly and work with the declared `liquibaseRuntime` dependencies.
- Upgrade the Liquibase Gradle plugin to a version that supports Gradle 9 (requires testing). Or
- Downgrade the Gradle wrapper to a version compatible with the current plugin (risky for other plugins).

## Recommended commands by goal

Run the app locally (no Docker):

```powershell
.\gradlew.bat bootRun
```

Run the app with the dev environment (Docker + app):

```powershell
.\gradlew.bat runDev
```

Verify the project (build & tests):

```powershell
.\gradlew.bat clean build
```

Inspect all available tasks:

```powershell
.\gradlew.bat tasks --all
```

## Environment / Notes

- Gradle Wrapper: **9.4.1**
- Java toolchain: **Java 21**
- Default DB values used by the tasks (can be overridden via environment variables or `-P` properties):
  - DB_POSTGRES_URL (default: `jdbc:postgresql://localhost:5432/spring_template_db?sslmode=disable`)
  - DB_POSTGRES_USERNAME (default: `user_admin`)
  - DB_POSTGRES_PASSWORD (default: `password_secret`)
- If you run the Liquibase tasks and they fail due to DB connectivity, ensure Postgres is running and reachable (or start the dev compose first: `.\gradlew.bat devComposeUp`).
- If you want, I can try upgrading the Liquibase Gradle plugin or downgrade the wrapper; tell me which approach you prefer and I will perform the change and test it.
