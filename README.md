<a id="readme-top"></a>

<!-- PROJECT SHIELDS -->
<div align="center">

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![JitPack][jitpack-shield]][jitpack-url]
[![Build][build-shield]][build-url]

</div>

<!-- PROJECT LOGO / HEADER -->
<br />
<div align="center">
  <h1 align="center">SQLForge</h1>

  <p align="center">
    A lightweight Java library for generating SQL scripts populated with realistic fake data.
    <br />
    <a href="#usage"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="#running-the-demo">View Demo</a>
    &middot;
    <a href="https://github.com/MrArCode/SQLForge/issues/new?labels=bug">Report Bug</a>
    &middot;
    <a href="https://github.com/MrArCode/SQLForge/issues/new?labels=enhancement">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#add-to-your-project">Add to Your Project</a></li>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#running-the-demo">Running the Demo</a></li>
        <li><a href="#supported-column-types">Supported Column Types</a></li>
        <li><a href="#api-reference">API Reference</a></li>
      </ul>
    </li>
    <li><a href="#project-status">Project Status</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

Instead of writing dozens of `INSERT` statements by hand, **SQLForge** lets you define your database schema in Java using a clean, fluent API—complete with column types, primary keys, foreign keys, `NOT NULL`, and `UNIQUE` constraints. It then generates a ready-to-run SQL script with `CREATE TABLE`, `INSERT`, `SELECT`, `UPDATE`, and `DELETE` statements populated by [Datafaker](https://www.datafaker.net/). Foreign keys automatically reference primary keys generated for parent tables, keeping relational data consistent out of the box.

SQLForge started in October 2024 during my university database coursework. Setting up practice schemas and coming up with believable test data by hand was easily the most tedious part of every assignment, so I built a tool to automate it. This was one of my first Java projects when I was learning the ropes as a programmer, and I'm currently refreshing and cleaning it up.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

* [![Java][Java-badge]][Java-url]
* [![Gradle][Gradle-badge]][Gradle-url]
* [![Lombok][Lombok-badge]][Lombok-url]
* [![Datafaker][Datafaker-badge]][Datafaker-url]
* [![JUnit][JUnit-badge]][JUnit-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

### Add to Your Project

SQLForge is published on [JitPack](https://jitpack.io/#MrArCode/SQLForge). Add the JitPack repository and the dependency to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.MrArCode:SQLForge:v0.2.0")
}
```

SQLForge needs Java 25 or newer. Snippets for Maven and other build tools are on the [JitPack page](https://jitpack.io/#MrArCode/SQLForge).

### Prerequisites

To build SQLForge from source, follow the steps below. You need:

* **Java 8 or newer** to start the Gradle wrapper

> [!NOTE]
> The project is compiled with Java 25. Gradle uses a JDK 25 that it finds on your machine or downloads one automatically, so you don't need to change `JAVA_HOME`.

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/MrArCode/SQLForge.git
   cd SQLForge
   ```
2. Build the project using the Gradle wrapper:
   ```sh
   ./gradlew build
   ```
   *(On Windows, run `gradlew.bat build` instead.)*

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Usage

Define your tables, pick the SQL operations you need, and build the script:

```java
import io.github.mrarcode.sqlforge.generators.FakerDataGenerator;
import io.github.mrarcode.sqlforge.generators.ScriptBuilder;
import io.github.mrarcode.sqlforge.models.Column;
import io.github.mrarcode.sqlforge.models.Table;
import io.github.mrarcode.sqlforge.models.Types;

import java.nio.file.Files;
import java.nio.file.Path;

Table users = new Table("Users");
users.addColumn(new Column("ID", Types.INTEGER).setPrimaryKey(true).setNotNull(true));
users.addColumn(new Column("FirstName", Types.FIRSTNAME).setNotNull(true));
users.addColumn(new Column("Email", Types.EMAIL).setUnique(true).setNotNull(true));

Table orders = new Table("Orders");
orders.addColumn(new Column("OrderID", Types.INTEGER).setPrimaryKey(true).setNotNull(true));
orders.addColumn(new Column("UserID", Types.INTEGER).setForeignKey("Users(ID)").setNotNull(true));
orders.addColumn(new Column("OrderDate", Types.DATE_TIME).setNotNull(true));

String script = ScriptBuilder.builder()
        .addTable(users)   // add parent tables before tables that reference them
        .addTable(orders)
        .includeCreate()
        .includeInsert(3)
        .includeRead()
        .withDataGenerator(new FakerDataGenerator())
        .buildScript();

Files.writeString(Path.of("script.sql"), script);
```

This produces a `script.sql` file like the following (with fresh random data on every run):

```sql
CREATE TABLE Users (
  ID INT NOT NULL,
  FirstName VARCHAR(255) NOT NULL,
  Email VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (ID)
);

CREATE TABLE Orders (
  OrderID INT NOT NULL,
  UserID INT NOT NULL,
  OrderDate DATETIME NOT NULL,
  PRIMARY KEY (OrderID),
  FOREIGN KEY (UserID) REFERENCES Users(ID)
);

INSERT INTO Users (ID, FirstName, Email) VALUES (1, 'Leonora', 'taylor.pagac@yahoo.com');
INSERT INTO Users (ID, FirstName, Email) VALUES (2, 'Yolanda', 'roger.herzog@gmail.com');
INSERT INTO Users (ID, FirstName, Email) VALUES (3, 'Deadra', 'alfredia.towne@hotmail.com');

INSERT INTO Orders (OrderID, UserID, OrderDate) VALUES (1, 1, '2026-09-18 15:15:30');
INSERT INTO Orders (OrderID, UserID, OrderDate) VALUES (2, 2, '2024-06-10 14:35:46');
INSERT INTO Orders (OrderID, UserID, OrderDate) VALUES (3, 3, '2025-07-15 10:06:38');

SELECT * FROM Users;

SELECT * FROM Orders;
```

### Running the Demo

[`Main.java`](src/main/java/io/github/mrarcode/sqlforge/Main.java) has a complete example that generates all CRUD operations (`CREATE`, `INSERT`, `SELECT`, `UPDATE`, and `DELETE`), prints the result to the console, and writes it to `output/script.sql`.

Run it with Gradle:

```sh
./gradlew run
```

You can also start `io.github.mrarcode.sqlforge.Main` from your IDE. The `output/` folder is created automatically if it doesn't exist.

### Supported Column Types

The [`Types`](src/main/java/io/github/mrarcode/sqlforge/models/Types.java) enum controls both the SQL column type used in `CREATE TABLE` and the kind of fake data generated for that column:

| SQL type | `Types` values |
| --- | --- |
| `INT` | `INTEGER` |
| `VARCHAR(255)` | `STRING`, `VARCHAR`, `TEXT`, `NAME`, `FIRSTNAME`, `LASTNAME`, `EMAIL`, `PHONENUMBER`, `PASSWORD`, `USER_AGENT`, `HEX_COLOR`, `FILE_NAME`, `LOREM_SENTENCE`, `UUID`, `ADDRESS`, `CITY`, `COUNTRY`, `ZIPCODE`, `IP_ADDRESS`, `MAC_ADDRESS`, `COMPANY`, `JOBTITLE`, `URL`, `NATIONALITY`, `CREDIT_CARD`, `IBAN`, `ISBN` |
| `TEXT` | `LOREM_PARAGRAPH` |
| `VARCHAR(3)` | `CURRENCY_CODE` |
| `CHAR(1)` | `CHAR` |
| `DECIMAL(8,6)` | `LATITUDE` |
| `DECIMAL(9,6)` | `LONGITUDE` |
| `FLOAT` | `FLOAT` |
| `DOUBLE` | `DOUBLE` |
| `BOOLEAN` | `BOOLEAN` |
| `DATE` | `DATE` |
| `DATETIME` | `DATE_TIME` |
| `TIMESTAMP` | `TIMESTAMP` |
| `TIME` | `TIME` |
| `BLOB` | `BLOB` (inserts `NULL` by default) |

### API Reference

#### `ScriptBuilder`

| Method | Description |
| --- | --- |
| `ScriptBuilder.builder()` | Creates a new script builder instance. |
| `addTable(Table table)` | Registers a table in the script. Tables are processed in the order they are added, so add referenced tables first. |
| `includeCreate()` | Includes `CREATE TABLE` statements with column constraints and keys. |
| `includeInsert(int count)` | Generates `count` `INSERT` statements per table. |
| `includeRead()` | Includes a `SELECT * FROM <table>;` query for each table. |
| `includeUpdate(int count)` | Generates up to `count` `UPDATE` statements per table, one per inserted row (requires a primary key). |
| `includeDelete(int count)` | Generates up to `count` `DELETE` statements per table, skipping rows that other rows still reference (requires a primary key). |
| `withDataGenerator(DataGenerator generator)` | Sets the data provider used for `INSERT` and `UPDATE` statements. |
| `buildScript()` | Builds and returns the full SQL script as a `String`. |

The script lists all `CREATE TABLE` statements first, followed by every `INSERT`, `SELECT`, `UPDATE`, and `DELETE`. `DELETE` statements go through the tables in reverse order, so foreign keys stay valid.

#### `Table`

| Method | Description |
| --- | --- |
| `new Table(String tableName)` | Creates a table definition with the given name. |
| `addColumn(Column column)` | Appends a column to the table. |

#### `Column`

All configuration setters return the `Column` instance for method chaining.

| Method | Description |
| --- | --- |
| `new Column(String columnName, Types dataType)` | Defines a column with the given name and [type](#supported-column-types). |
| `setPrimaryKey(boolean isPrimaryKey)` | Marks the column as a primary key (auto-generates sequential IDs starting at `1`). |
| `setNotNull(boolean isNotNull)` | Adds a `NOT NULL` constraint. |
| `setUnique(boolean isUnique)` | Adds a `UNIQUE` constraint. |
| `setForeignKey(String reference)` | Links the column to another table's primary key (e.g., `"Users(ID)"`) and picks from already generated IDs. |

#### `DataGenerator`

Want to plug in your own data source instead of Datafaker? Implement the `DataGenerator` interface:

```java
public interface DataGenerator {
    String generateData(Column column);
}
```

`generateData` is called for every column except primary and foreign keys, which SQLForge fills in itself, and should return a valid SQL literal (such as a single-quoted string `'Alice'`, a number, or `NULL`).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- PROJECT STATUS -->
## Project Status

SQLForge is under active development: the code is being cleaned up and fixed step by step, so the API and the generated SQL may still change.

Found a bug or have an idea? [Open an issue](https://github.com/MrArCode/SQLForge/issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feat/amazing-feature`)
3. Commit your Changes following [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) (`git commit -m 'feat: add support for PostgreSQL types'`)
4. Push to the Branch (`git push origin feat/amazing-feature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->
## License

Distributed under the MIT License. See [`LICENSE`](LICENSE) for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

**MrArCode** – [@MrArCode](https://github.com/MrArCode)

Project Link: [https://github.com/MrArCode/SQLForge](https://github.com/MrArCode/SQLForge)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* [Datafaker](https://www.datafaker.net/) for realistic test data generation
* [Best-README-Template](https://github.com/othneildrew/Best-README-Template)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/MrArCode/SQLForge.svg?style=for-the-badge
[contributors-url]: https://github.com/MrArCode/SQLForge/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/MrArCode/SQLForge.svg?style=for-the-badge
[forks-url]: https://github.com/MrArCode/SQLForge/network/members
[stars-shield]: https://img.shields.io/github/stars/MrArCode/SQLForge.svg?style=for-the-badge
[stars-url]: https://github.com/MrArCode/SQLForge/stargazers
[issues-shield]: https://img.shields.io/github/issues/MrArCode/SQLForge.svg?style=for-the-badge
[issues-url]: https://github.com/MrArCode/SQLForge/issues
[license-shield]: https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge
[license-url]: LICENSE
[jitpack-shield]: https://img.shields.io/jitpack/version/com.github.MrArCode/SQLForge?style=for-the-badge
[jitpack-url]: https://jitpack.io/#MrArCode/SQLForge
[build-shield]: https://img.shields.io/github/actions/workflow/status/MrArCode/SQLForge/build.yml?branch=master&style=for-the-badge
[build-url]: https://github.com/MrArCode/SQLForge/actions/workflows/build.yml

[Java-badge]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://openjdk.org/
[Gradle-badge]: https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white
[Gradle-url]: https://gradle.org/
[Lombok-badge]: https://img.shields.io/badge/Lombok-BC4521?style=for-the-badge&logo=projectlombok&logoColor=white
[Lombok-url]: https://projectlombok.org/
[Datafaker-badge]: https://img.shields.io/badge/Datafaker-1F6FEB?style=for-the-badge
[Datafaker-url]: https://www.datafaker.net/
[JUnit-badge]: https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white
[JUnit-url]: https://junit.org/
