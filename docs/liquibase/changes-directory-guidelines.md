# Liquibase `changes/` Directory Guidelines

This note explains how to organize Liquibase change files inside `src/main/resources/db/changelog/changes/`.

The current project uses a simple master changelog pattern:

- `db.changelog-master.yaml` is the entry point
- it includes files from the `changes/` directory
- each file contains one or more Liquibase `changeSet` blocks

When the project grows, the way you organize `changes/` matters a lot. A clear structure makes it easier for new contributors to find the right file, understand the migration history, and add new schema changes safely.

---

## 1. Table-based layout

### Idea

Group change files by table.

### Example structure

```text
changes/
├── user/
│   ├── 0001_create_user_table.yaml
│   ├── 0002_add_email_column.yaml
│   └── 0003_add_user_index.yaml
├── order/
│   ├── 0001_create_order_table.yaml
│   └── 0002_add_status_column.yaml
└── shared/
    └── 0001_create_audit_table.yaml
```

### When this works well

- the project has many changes focused on a few core tables
- each table has its own life cycle
- the team wants to browse migrations by table name first

### Pros

- easy to find everything related to one table
- good for table-centric domains
- helpful when schema work is split by ownership

### Cons

- harder to see the exact release history in chronological order
- cross-table changes can be difficult to place
- can become messy if a migration touches multiple tables

### Good fit

Use this layout when the schema is organized around a few important tables and most changes are table-specific.

---

## 2. Version-based layout

### Idea

Keep all files in one directory and prefix them with a version number.

### Example structure

```text
changes/
├── 0001_create_user_table.yaml
├── 0002_create_order_table.yaml
├── 0003_add_user_email.yaml
├── 0004_add_order_status.yaml
└── 0005_create_audit_table.yaml
```

### When this works well

- the team wants a simple and predictable history
- changes are often mixed across multiple tables
- contributors need a single chronological list of migrations

### Pros

- this is the most common and practical style
- very easy to read in order
- simple to maintain in small and medium projects
- works well with Git history and code reviews

### Cons

- the folder can get long as the project grows
- related files are not grouped by table name

### Recommendation for this repo

**This is the recommended default approach for this project.**

Why:

- the project currently has a small number of migrations
- new contributors can understand the order quickly
- it keeps `db.changelog-master.yaml` simple
- it is easy to add one new file for each logical schema change

### Suggested naming rule

Use a numeric prefix plus a short description:

- `0001_create_liquibase_example_table.yaml`
- `0002_add_user_email_column.yaml`
- `0003_create_audit_log_table.yaml`

---

## 3. Hybrid layout

### Idea

Combine version numbers with subfolders.

### Example structure

```text
changes/
├── 0001/
│   ├── 0001_create_user_table.yaml
│   └── 0002_create_user_indexes.yaml
├── 0002/
│   ├── 0003_create_order_table.yaml
│   └── 0004_add_order_status.yaml
└── shared/
    └── 0005_create_audit_table.yaml
```

Or, in a table-oriented hybrid style:

```text
changes/
├── 0001_user/
│   ├── 0001_create_user_table.yaml
│   └── 0002_add_user_email.yaml
├── 0002_order/
│   └── 0003_create_order_table.yaml
└── 0003_shared/
    └── 0004_create_audit_table.yaml
```

### When this works well

- the schema becomes large
- one release contains many related files
- different teams own different areas of the schema
- you want both chronological order and grouping by domain/table

### Pros

- easier to organize large migration sets
- keeps related files together
- still preserves a version-based timeline

### Cons

- more complex than a plain version-based layout
- contributors must follow the folder convention carefully
- can feel unnecessary for a small project

### Good fit

Use this only when the schema grows enough that a single flat `changes/` folder becomes hard to manage.

---

## 4. Which pattern should this project use?

For this repository, the best default is:

> **Version-based layout**

That is the simplest and safest pattern for now.

If the project grows and one domain becomes much larger than the others, you can move toward a hybrid structure later.

A table-based layout is also valid, but it is usually better for teams that already organize work by table ownership.

---

## 5. Practical rules for contributors

No matter which layout you choose, follow these rules:

1. **One logical change per file**
   - do not mix unrelated schema changes in one file

2. **Never rewrite old applied change sets**
   - if a change is already shared or applied, create a new file instead of editing the old one

3. **Keep filenames clear**
   - use short, descriptive names
   - include a version prefix when possible

4. **Update the master changelog**
   - remember to include the new file in `db.changelog-master.yaml`

5. **Prefer portable SQL/data types**
   - keep migrations compatible with the project’s supported databases when possible

6. **Review the change path before committing**
   - ensure the file sits in the right folder for the chosen layout

---

## 6. Quick recommendation

If you are unsure which structure to use, choose this:

- **Default:** version-based
- **For table-heavy modules:** table-based
- **For larger long-term projects:** hybrid

For this repo, the current setup is already aligned well with the **version-based** approach.

That means the simplest contribution workflow is:

1. create a new numbered YAML file in `changes/`
2. add it to `db.changelog-master.yaml`
3. run the tests
4. commit the migration

