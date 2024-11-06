# 💽 SQLForge: Streamlined SQL Script Generator

SQLForge is a Java-based tool that generates SQL scripts for database operations, supporting **CRUD** (Create, Read, Update, Delete) operations. Using the **Faker** library, it creates realistic test data, making it ideal for database testing or automated data manipulation.

## Features

- Generate `CREATE TABLE`, `INSERT`, `SELECT`, `UPDATE`, and `DELETE` SQL statements.
- Define primary/foreign keys, unique constraints, and data types.
- Automated test data generation with **Faker**.

---

## ⚙️ Core Methods

### ScriptBuilder Methods:

- `addTable(Table table)`: Add a table to the SQL script.
- `includeCreate()`: Generates `CREATE TABLE` for each table.
- `includeInsert(int numberOfRows)`: Adds `INSERT` statements for specified rows.
- `includeRead()`: Adds `SELECT *` statements for data retrieval.
- `includeUpdate(int numberOfUpdates)`: Adds `UPDATE` statements.
- `includeDelete(int numberOfDeletes)`: Adds `DELETE` statements.
- `withDataGenerator(DataGenerator generator)`: Sets a data generator (e.g., **Faker**) for sample data.

### Table and Column Methods:

- `addColumn(Column column)`: Define a column in a table.
- `setPrimaryKey()`, `setNotNull()`, `setUnique()`, `setForeignKey()`: Set constraints for each column.

---

## 📚 References

- **Java Faker**: Generates random, realistic data for testing purposes.
- **Official Documentation**: SQL standards and practices used within SQLForge.

---

> Developed by **MrArCode** | License: MIT License
