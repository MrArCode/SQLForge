package org.example.models;

import lombok.Getter;
import lombok.Setter;

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

    public Column getPrimaryKeyColumn() {
        for (Column column : columns) {
            if (column.isPrimaryKey()) {
                return column;
            }
        }
        return null;
    }

}
