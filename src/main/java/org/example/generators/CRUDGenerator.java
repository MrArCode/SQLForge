package org.example.generators;

import org.example.model.Column;
import org.example.model.Table;
import org.example.model.Types;

import java.util.ArrayList;
import java.util.List;

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

    public static String generateRead(Table table) {
        return "SELECT * FROM " + table.getTableName() + ";\n";
    }

    public static String generateUpdate(Table table, int numberOfUpdates, DataGenerator dataGenerator) {
        StringBuilder updateStatements = new StringBuilder();

        List<Column> updatableColumns = new ArrayList<>();
        for (Column column : table.getColumns()) {
            if (!column.isPrimaryKey()) {
                updatableColumns.add(column);
            }
        }

        if (updatableColumns.isEmpty()) {
            return "";
        }

        Column primaryKey = table.getPrimaryKeyColumn();
        if (primaryKey == null) {
            return "";
        }

        for (int i = 0; i < numberOfUpdates; i++) {
            StringBuilder update = new StringBuilder();
            update.append("UPDATE ").append(table.getTableName()).append(" SET ");

            List<String> setClauses = new ArrayList<>();
            for (Column column : updatableColumns) {
                String setClause = column.getColumnName() + " = " + dataGenerator.generateData(column);
                setClauses.add(setClause);
            }
            update.append(String.join(", ", setClauses));

            update.append(" WHERE ").append(primaryKey.getColumnName()).append(" = ").append(i + 1);
            update.append(";\n");
            updateStatements.append(update);
        }

        return updateStatements.toString();
    }


    public static String generateDelete(Table table, int numberOfDeletes) {
        StringBuilder deleteStatements = new StringBuilder();

        Column primaryKey = table.getPrimaryKeyColumn();
        if (primaryKey == null) {
            return "";
        }

        for (int i = 0; i < numberOfDeletes; i++) {
            StringBuilder delete = new StringBuilder();
            delete.append("DELETE FROM ").append(table.getTableName())
                    .append(" WHERE ").append(primaryKey.getColumnName()).append(" = ").append(i + 1)
                    .append(";\n");
            deleteStatements.append(delete);
        }

        return deleteStatements.toString();
    }




    private static String getSQLType(Types type) {
        return switch (type) {
            case INTEGER -> "INT";
            case STRING, VARCHAR, TEXT, EMAIL, PHONENUMBER, ADDRESS, CITY, COUNTRY, ZIPCODE, COMPANY, JOBTITLE,
                 FIRSTNAME, LASTNAME, NAME, PASSWORD, IP_ADDRESS, UUID, HEX_COLOR, FILE_NAME, LOREM_SENTENCE,
                 LOREM_PARAGRAPH, NATIONALITY, URL, USER_AGENT -> "VARCHAR(255)";
            case DATE -> "DATE";
            case DATE_TIME -> "DATETIME";
            case TIMESTAMP -> "TIMESTAMP";
            case FLOAT -> "FLOAT";
            case DOUBLE -> "DOUBLE";
            case BOOLEAN -> "BOOLEAN";
            case CHAR -> "CHAR(1)";
            case BLOB -> "BLOB";
            case CURRENCY_CODE -> "VARCHAR(3)";
            default -> throw new IllegalArgumentException("Unknown SQL type for: " + type);
        };
    }
}

