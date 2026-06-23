# Bakery Inventory Management System

Complete Java Swing desktop application for bakery inventory control with SQLite persistence.

## Default Login

- Username: `gerente`
- Password: `12345678`

Only one manager account can exist. The manager account is created automatically on first start and cannot be deleted.

## Requirements

- Java 17 or newer
- Maven 3.8 or newer

## Run

From this folder:

```bash
mvn clean compile exec:java
```

Build a runnable JAR:

```bash
mvn clean package
java -jar target/bakery-inventory-system-1.0.0.jar
```

The SQLite database is created automatically at:

```text
data/bakery_inventory.db
```

## Project Structure

```text
src/main/java/com/bakeryinventory
  App.java
  config/        Database connection and schema initialization
  dao/           Database access classes
  model/         Domain models
  service/       Authentication, password hashing, inventory logic
  ui/            Java Swing screens and dialogs
  util/          Validation and UI helpers
src/main/resources/db
  schema.sql
  sample_data.sql
```

## Sample Data

The file `src/main/resources/db/sample_data.sql` contains test products and can be run against the generated database with any SQLite client.

Example:

```bash
sqlite3 data/bakery_inventory.db < src/main/resources/db/sample_data.sql
```

## Main Features

- Secure login with salted PBKDF2 password hashes.
- Manager-only user creation.
- Product add, edit, delete, and search.
- Stock comparison against ideal quantity.
- Expiration monitoring for near-expiration and expired products.
- Visual table alerts and startup expiration warning dialog.
- Confirmation before deletions.
- SQLite tables, constraints, foreign keys, triggers, and sample data.
