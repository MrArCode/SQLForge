package org.example.model;

public class Column {
    private String columnName;
    private Types dataType;
    private boolean isPrimaryKey = false;
    private boolean isNotNull = false;
    private boolean isUnique = false;
    private String foreignKeyReference = null;
    private String tableName;

    public Column(String columnName, Types dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }


    public String getColumnName() {
        return columnName;
    }

    public Column setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public Types getDataType() {
        return dataType;
    }

    public Column setDataType(Types dataType) {
        this.dataType = dataType;
        return this;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public Column setPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
        return this;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public Column setNotNull(boolean isNotNull) {
        this.isNotNull = isNotNull;
        return this;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public Column setUnique(boolean isUnique) {
        this.isUnique = isUnique;
        return this;
    }

    public String getForeignKeyReference() {
        return foreignKeyReference;
    }

    public Column setForeignKey(String foreignKeyReference) {
        this.foreignKeyReference = foreignKeyReference;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public Column setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }
}
