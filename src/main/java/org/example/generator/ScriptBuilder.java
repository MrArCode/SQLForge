package org.example.generator;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Table;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ScriptBuilder {
    private List<Table> tables = new ArrayList<>();
    private boolean includeCreate = false;
    private boolean includeInsert = false;
    private boolean includeRead = false;
    private boolean includeDrop = false;
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

    public ScriptBuilder includeCreate(boolean includeCreate) {
        this.includeCreate = includeCreate;
        return this;
    }

    public ScriptBuilder includeRead(boolean includeRead) {
        this.includeRead = includeRead;
        return this;
    }

    public ScriptBuilder includeUpdate(boolean includeUpdate, int numberOfUpdates) {
        this.includeUpdate = includeUpdate;
        this.numberOfUpdates = numberOfUpdates;
        return this;
    }

    public ScriptBuilder includeDelete(boolean includeDelete, int numberOfDeletes) {
        this.includeDelete = includeDelete;
        this.numberOfDeletes = numberOfDeletes;
        return this;
    }


    public ScriptBuilder includeInsert(boolean includeInsert, int numberOfInserts) {
        this.includeInsert = includeInsert;
        this.numberOfInserts = numberOfInserts;
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
            if (includeInsert && dataGenerator != null) {
                script.append(table.generateInserts(numberOfInserts, dataGenerator)).append("\n");
            }

            if (includeRead && dataGenerator != null) {
                script.append(CRUDGenerator.generateRead(table)).append("\n");
            }
            if (includeUpdate) {
                script.append(CRUDGenerator.generateUpdate(table, numberOfUpdates, dataGenerator)).append("\n");
            }
            if (includeDelete) {
                script.append(CRUDGenerator.generateDelete(table, numberOfDeletes)).append("\n");
            }

        }

        return script.toString();
    }
}
