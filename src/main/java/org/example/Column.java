package org.example;

public class Column {
    private String columnName;
    private Types dataType;

    public Column(String columnName, Types dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public String getColumnName() {
        return columnName;
    }

    public Types getDataType() {
        return dataType;
    }
}
