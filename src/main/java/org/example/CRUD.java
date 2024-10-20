package org.example;

public class CRUD {

    public static String generateDrop(Table table) {
        return "DROP TABLE " + table.getTableName() + ";";
    }

    public static String generateCreate(Table table) {
        StringBuilder create = new StringBuilder();
        create.append("CREATE TABLE ").append(table.getTableName()).append(" (\n");

        for (int i = 0; i < table.getColumns().size(); i++) {
            Column column = table.getColumns().get(i);
            create.append("  ").append(column.getColumnName()).append(" ").append(getSQLType(column.getDataType()));

            if (i < table.getColumns().size() - 1) {
                create.append(",");
            }
            create.append("\n");
        }

        create.append(");");
        return create.toString();
    }

    public static String generateUpdate(Table table, String whereClause) {
        StringBuilder update = new StringBuilder();
        update.append("UPDATE ").append(table.getTableName()).append(" SET \n");

        for (int i = 0; i < table.getColumns().size(); i++) {
            Column column = table.getColumns().get(i);
            update.append("  ").append(column.getColumnName()).append(" = ");

            update.append(generatePlaceholderValue(column.getDataType()));

            if (i < table.getColumns().size() - 1) {
                update.append(",");
            }
            update.append("\n");
        }

        if (whereClause != null && !whereClause.isEmpty()) {
            update.append("WHERE ").append(whereClause).append(";");
        } else {
            update.append(";");
        }

        return update.toString();
    }

    public static String generateRead(Table table, String whereClause) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT ");

        for (int i = 0; i < table.getColumns().size(); i++) {
            Column column = table.getColumns().get(i);
            select.append(column.getColumnName());

            if (i < table.getColumns().size() - 1) {
                select.append(", ");
            }
        }

        select.append(" FROM ").append(table.getTableName());

        if (whereClause != null && !whereClause.isEmpty()) {
            select.append(" WHERE ").append(whereClause);
        }
        select.append(";");

        return select.toString();
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

    private static String generatePlaceholderValue(Types type) {
        return switch (type) {
            case INTEGER, FLOAT, DOUBLE -> "0";
            case BOOLEAN -> "FALSE";
            case DATE, DATE_TIME, TIMESTAMP -> "'1970-01-01'";
            default -> "''";
        };
    }
}
