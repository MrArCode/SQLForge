package org.example.generator;

import org.example.model.Column;
import org.example.model.Table;
import org.example.model.Types;

import java.util.ArrayList;
import java.util.List;

public class CRUDGenerator {

    public static String generateCreate(Table table) {
        StringBuilder create = new StringBuilder();
        create.append("CREATE TABLE ").append(table.getTableName()).append(" (\n");

        List<String> primaryKeys = new ArrayList<>();
        List<String> foreignKeys = new ArrayList<>();

        for (int i = 0; i < table.getColumns().size(); i++) {
            Column column = table.getColumns().get(i);
            create.append("  ").append(column.getColumnName()).append(" ")
                    .append(getSQLType(column.getDataType()));

            if (column.isNotNull()) {
                create.append(" NOT NULL");
            }
            if (column.isUnique()) {
                create.append(" UNIQUE");
            }
            if (column.isPrimaryKey()) {
                primaryKeys.add(column.getColumnName());
            }
            if (column.getForeignKeyReference() != null) {
                foreignKeys.add("FOREIGN KEY (" + column.getColumnName() + ") REFERENCES "
                        + column.getForeignKeyReference());
            }

            create.append(",");
            create.append("\n");
        }

        if (!primaryKeys.isEmpty()) {
            create.append("  PRIMARY KEY (").append(String.join(", ", primaryKeys)).append("),\n");
        }

        for (String fk : foreignKeys) {
            create.append("  ").append(fk).append(",\n");
        }

        int lastIndex = create.lastIndexOf(",");
        if (lastIndex != -1) {
            create.deleteCharAt(lastIndex);
        }

        create.append("\n);");
        return create.toString();
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

