package org.example.generators;

import org.example.models.Column;
import org.example.models.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ScriptBuilder {
    private final List<Table> tables = new ArrayList<>();
    private boolean includeCreate = false;
    private boolean includeInsert = false;
    private boolean includeRead = false;
    private boolean includeUpdate = false;
    private boolean includeDelete = false;
    private int numberOfInserts = 0;
    private int numberOfUpdates = 0;
    private int numberOfDeletes = 0;
    private DataGenerator dataGenerator;
    private final Random random = new Random();

    private ScriptBuilder() { }

    public static ScriptBuilder builder() {
        return new ScriptBuilder();
    }

    public ScriptBuilder addTable(Table table) {
        this.tables.add(table);
        return this;
    }

    public ScriptBuilder includeCreate() {
        this.includeCreate = true;
        return this;
    }

    public ScriptBuilder includeRead() {
        this.includeRead = true;
        return this;
    }

    public ScriptBuilder includeInsert(int numberOfInserts) {
        this.includeInsert = true;
        this.numberOfInserts = numberOfInserts;
        return this;
    }

    public ScriptBuilder includeUpdate(int numberOfUpdates) {
        this.includeUpdate = true;
        this.numberOfUpdates = numberOfUpdates;
        return this;
    }

    public ScriptBuilder includeDelete(int numberOfDeletes) {
        this.includeDelete = true;
        this.numberOfDeletes = numberOfDeletes;
        return this;
    }

    public ScriptBuilder withDataGenerator(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
        return this;
    }

    public String buildScript() {
        if (includeInsert && dataGenerator == null) {
            throw new IllegalStateException("DataGenerator is required for INSERT statements.");
        }
        if (includeUpdate && dataGenerator == null) {
            throw new IllegalStateException("DataGenerator is required for UPDATE statements.");
        }

        // Rows inserted by this script, per table. Each row maps a column to the SQL literal written for it.
        Map<Table, List<Map<Column, String>>> rows = new HashMap<>();
        StringBuilder script = new StringBuilder();

        // Statements are grouped by operation, so every table exists before rows are inserted into it
        // and every row exists before it is updated or deleted.
        if (includeCreate) {
            for (Table table : tables) {
                script.append(CRUDGenerator.generateCreate(table)).append("\n\n");
            }
        }
        if (includeInsert) {
            for (Table table : tables) {
                appendInserts(script, table, rows);
            }
        }
        if (includeRead) {
            for (Table table : tables) {
                script.append(CRUDGenerator.generateRead(table)).append("\n");
            }
        }
        if (includeUpdate) {
            for (Table table : tables) {
                appendUpdates(script, table, rows);
            }
        }
        if (includeDelete) {
            // Referencing tables are added after the tables they point to, so going backwards deletes their rows first.
            for (Table table : tables.reversed()) {
                appendDeletes(script, table, rows);
            }
        }

        return script.toString();
    }

    private void appendInserts(StringBuilder script, Table table, Map<Table, List<Map<Column, String>>> rows) {
        List<Map<Column, String>> tableRows = new ArrayList<>();
        for (int i = 1; i <= numberOfInserts; i++) {
            Map<Column, String> row = new HashMap<>();
            for (Column column : table.getColumns()) {
                row.put(column, column.isPrimaryKey() ? String.valueOf(i) : generateValue(column, rows));
            }
            tableRows.add(row);
            script.append(CRUDGenerator.generateInsert(table, row));
        }
        rows.put(table, tableRows);
        script.append("\n");
    }

    private void appendUpdates(StringBuilder script, Table table, Map<Table, List<Map<Column, String>>> rows) {
        Column primaryKey = table.getPrimaryKeyColumn();
        if (primaryKey != null) {
            List<Map<Column, String>> tableRows = rows.get(table);
            List<Map<Column, String>> rowsToUpdate = tableRows == null
                    ? assumedRows(table, numberOfUpdates)
                    : tableRows.subList(0, Math.min(numberOfUpdates, tableRows.size()));

            for (Map<Column, String> row : rowsToUpdate) {
                Map<Column, String> newValues = new LinkedHashMap<>();
                for (Column column : table.getColumns()) {
                    if (!column.isPrimaryKey()) {
                        newValues.put(column, generateValue(column, rows));
                    }
                }
                if (newValues.isEmpty()) {
                    break;
                }
                row.putAll(newValues);
                script.append(CRUDGenerator.generateUpdate(table, newValues, row.get(primaryKey)));
            }
        }
        script.append("\n");
    }

    private void appendDeletes(StringBuilder script, Table table, Map<Table, List<Map<Column, String>>> rows) {
        Column primaryKey = table.getPrimaryKeyColumn();
        if (primaryKey != null) {
            List<Map<Column, String>> tableRows = rows.containsKey(table)
                    ? rows.get(table)
                    : assumedRows(table, numberOfDeletes);
            Set<String> referencedKeys = keysStillReferenced(table, rows);

            int deleted = 0;
            Iterator<Map<Column, String>> iterator = tableRows.iterator();
            while (deleted < numberOfDeletes && iterator.hasNext()) {
                String key = iterator.next().get(primaryKey);
                if (!referencedKeys.contains(key)) {
                    script.append(CRUDGenerator.generateDelete(table, key));
                    iterator.remove();
                    deleted++;
                }
            }
        }
        script.append("\n");
    }

    // Foreign keys reuse the primary key of a random row that was already inserted into the referenced table.
    private String generateValue(Column column, Map<Table, List<Map<Column, String>>> rows) {
        if (column.getForeignKeyReference() == null) {
            return dataGenerator.generateData(column);
        }
        Table referencedTable = findTable(column.getReferencedTableName());
        if (referencedTable == null || referencedTable.getPrimaryKeyColumn() == null) {
            return "NULL";
        }
        List<Map<Column, String>> candidates = rows.getOrDefault(referencedTable, List.of());
        if (candidates.isEmpty()) {
            return "NULL";
        }
        return candidates.get(random.nextInt(candidates.size())).get(referencedTable.getPrimaryKeyColumn());
    }

    // Primary keys of the table that rows of any table still point to through a foreign key.
    private Set<String> keysStillReferenced(Table table, Map<Table, List<Map<Column, String>>> rows) {
        Set<String> keys = new HashSet<>();
        for (Table other : tables) {
            for (Column column : other.getColumns()) {
                if (table.getTableName().equals(column.getReferencedTableName())) {
                    for (Map<Column, String> row : rows.getOrDefault(other, List.of())) {
                        keys.add(row.get(column));
                    }
                }
            }
        }
        return keys;
    }

    // Without INSERT statements in this script, assume the table already holds rows with primary keys 1..count.
    private List<Map<Column, String>> assumedRows(Table table, int count) {
        List<Map<Column, String>> assumed = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Map<Column, String> row = new HashMap<>();
            row.put(table.getPrimaryKeyColumn(), String.valueOf(i));
            assumed.add(row);
        }
        return assumed;
    }

    private Table findTable(String tableName) {
        for (Table table : tables) {
            if (table.getTableName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

}
