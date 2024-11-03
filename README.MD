# SQLForge

SQLForge is a Java-based tool that generates SQL scripts for database operations, supporting **CRUD** (Create, Read, Update, Delete) operations. Using the **Faker** library, it creates realistic test data, making it ideal for database testing or automated data manipulation.

## Features

- Generate `CREATE TABLE`, `INSERT`, `SELECT`, `UPDATE`, and `DELETE` SQL statements.
- Define primary/foreign keys, unique constraints, and data types.
- Automated test data generation with **Faker**.

## Requirements

- Java 8 or higher
- Maven or Gradle

## Installation

To add SQLForge to your project using JitPack:

### Gradle

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.MrArCode:SQLForge:1.0.0")
}
