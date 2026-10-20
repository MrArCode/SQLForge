package io.github.mrarcode.sqlforge.generators;

import io.github.mrarcode.sqlforge.models.Column;
import io.github.mrarcode.sqlforge.models.Table;
import io.github.mrarcode.sqlforge.models.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CRUDGenerator {

    public static String generateCreate(Table table) {
        StringBuilder create = new StringBuilder();
        create.append("CREATE TABLE ").append(table.getTableName()).append(" (\n");

        List<String> columnDefinitions = new ArrayList<>();
        List<String> primaryKeys = new ArrayList<>();
        List<String> foreignKeys = new ArrayList<>();

        for (Column column : table.getColumns()) {
            StringBuilder columnDef = new StringBuilder();
            columnDef.append("  ").append(column.getColumnName()).append(" ")
                    .append(getSQLType(column.getDataType()));

            if (column.isNotNull()) {
                columnDef.append(" NOT NULL");
            }
            if (column.isUnique()) {
                columnDef.append(" UNIQUE");
            }
            columnDefinitions.add(columnDef.toString());

            if (column.isPrimaryKey()) {
                primaryKeys.add(column.getColumnName());
            }
            if (column.getForeignKeyReference() != null) {
                foreignKeys.add("  FOREIGN KEY (" + column.getColumnName() + ") REFERENCES "
                        + column.getForeignKeyReference());
            }
        }

        create.append(String.join(",\n", columnDefinitions));

        if (!primaryKeys.isEmpty()) {
            create.append(",\n  PRIMARY KEY (").append(String.join(", ", primaryKeys)).append(")");
        }

        if (!foreignKeys.isEmpty()) {
            create.append(",\n").append(String.join(",\n", foreignKeys));
        }

        create.append("\n);");
        return create.toString();
    }

    public static String generateInsert(Table table, Map<Column, String> row) {
        List<String> columnNames = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for (Column column : table.getColumns()) {
            columnNames.add(column.getColumnName());
            values.add(row.get(column));
        }
        return "INSERT INTO " + table.getTableName() + " (" + String.join(", ", columnNames)
                + ") VALUES (" + String.join(", ", values) + ");\n";
    }

    public static String generateRead(Table table) {
        return "SELECT * FROM " + table.getTableName() + ";\n";
    }

    public static String generateUpdate(Table table, Map<Column, String> newValues, String primaryKeyValue) {
        List<String> setClauses = new ArrayList<>();
        for (Map.Entry<Column, String> entry : newValues.entrySet()) {
            setClauses.add(entry.getKey().getColumnName() + " = " + entry.getValue());
        }
        return "UPDATE " + table.getTableName() + " SET " + String.join(", ", setClauses)
                + " WHERE " + table.getPrimaryKeyColumn().getColumnName() + " = " + primaryKeyValue + ";\n";
    }

    public static String generateDelete(Table table, String primaryKeyValue) {
        return "DELETE FROM " + table.getTableName()
                + " WHERE " + table.getPrimaryKeyColumn().getColumnName() + " = " + primaryKeyValue + ";\n";
    }




    // No default branch: the compiler checks that every value of Types has a SQL type.
    private static String getSQLType(Types type) {
        return switch (type) {
            case INTEGER -> "INT";
            case STRING, VARCHAR, TEXT, EMAIL, PHONENUMBER, ADDRESS, CITY, COUNTRY, ZIPCODE, COMPANY, JOBTITLE,
                 FIRSTNAME, LASTNAME, NAME, PASSWORD, IP_ADDRESS, UUID, HEX_COLOR, FILE_NAME, LOREM_SENTENCE,
                 NATIONALITY, URL, USER_AGENT, CREDIT_CARD, MAC_ADDRESS, IBAN, ISBN -> "VARCHAR(255)";
            case LOREM_PARAGRAPH -> "TEXT";
            case LATITUDE -> "DECIMAL(8,6)";
            case LONGITUDE -> "DECIMAL(9,6)";
            case DATE -> "DATE";
            case DATE_TIME -> "DATETIME";
            case TIMESTAMP -> "TIMESTAMP";
            case TIME -> "TIME";
            case FLOAT -> "FLOAT";
            case DOUBLE -> "DOUBLE";
            case BOOLEAN -> "BOOLEAN";
            case CHAR -> "CHAR(1)";
            case BLOB -> "BLOB";
            case CURRENCY_CODE -> "VARCHAR(3)";
        };
    }
}

