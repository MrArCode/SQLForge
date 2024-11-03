package org.example.generators;

import org.example.models.Table;

import java.util.ArrayList;
import java.util.List;

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
        StringBuilder script = new StringBuilder();

        for (Table table : tables) {
            if (includeCreate) {
                script.append(CRUDGenerator.generateCreate(table)).append("\n\n");
            }
            if (includeInsert) {
                if (dataGenerator == null) {
                    throw new IllegalStateException("DataGenerator is required for INSERT statements.");
                }
                script.append(table.generateInserts(numberOfInserts, dataGenerator)).append("\n");
            }
            if (includeRead) {
                script.append(CRUDGenerator.generateRead(table)).append("\n");
            }
            if (includeUpdate) {
                if (dataGenerator == null) {
                    throw new IllegalStateException("DataGenerator is required for UPDATE statements.");
                }
                script.append(CRUDGenerator.generateUpdate(table, numberOfUpdates, dataGenerator)).append("\n");
            }
            if (includeDelete) {
                script.append(CRUDGenerator.generateDelete(table, numberOfDeletes)).append("\n");
            }
        }

        return script.toString();
    }

}
