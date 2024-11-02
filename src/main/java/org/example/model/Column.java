package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public Column setForeignKey(String foreignKeyReference) {
        this.foreignKeyReference = foreignKeyReference;
        return this;
    }
}
