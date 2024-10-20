package org.example.generator;

import org.example.model.Table;

import java.util.ArrayList;
import java.util.List;

public class ScriptBuilder {
    private List<Table> tables = new ArrayList<>();
    private boolean includeCreate = false;
    private boolean includeInsert = false;
    private int numberOfInserts = 0;
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
        }

        return script.toString();
    }
}
