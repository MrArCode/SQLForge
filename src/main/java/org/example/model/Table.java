package org.example.model;

import lombok.Getter;
import lombok.Setter;
import org.example.generator.DataGenerator;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Table {
    private final String tableName;
    private final List<Column> columns = new ArrayList<>();

    public Table(String tableName) {
        this.tableName = tableName;
    }

    public void addColumn(Column column) {
        column.setTableName(this.tableName);
        columns.add(column);
    }

    public String generateInserts(int numberOfRows, DataGenerator dataGenerator) {
        StringBuilder allInserts = new StringBuilder();
        for (int i = 0; i < numberOfRows; i++) {
            StringBuilder insert = new StringBuilder();
            insert.append("INSERT INTO ").append(tableName).append(" (");


            for (int j = 0; j < columns.size(); j++) {
                insert.append(columns.get(j).getColumnName());
                if (j < columns.size() - 1) {
                    insert.append(", ");
                }
            }

            insert.append(") VALUES (");

            for (int j = 0; j < columns.size(); j++) {
                Column column = columns.get(j);
                insert.append(dataGenerator.generateData(column));
                if (j < columns.size() - 1) {
                    insert.append(", ");
                }
            }

            insert.append(");");
            allInserts.append(insert).append("\n");
        }
        return allInserts.toString();
    }
}
