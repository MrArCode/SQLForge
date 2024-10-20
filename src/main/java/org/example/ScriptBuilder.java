package org.example;

import java.util.ArrayList;
import java.util.List;

public class ScriptBuilder {
    private List<Table> tables = new ArrayList<>();
    private boolean includeCreate = false;
    private boolean includeDrop = false;
    private boolean includeInsert = false;
    private boolean includeUpdate = false;
    private boolean includeRead = false;
    private int numberOfInserts = 0;
    private String updateWhereClause = "";
    private String readWhereClause = "";

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

    public ScriptBuilder includeDrop(boolean includeDrop) {
        this.includeDrop = includeDrop;
        return this;
    }

    public ScriptBuilder includeInsert(boolean includeInsert, int numberOfInserts) {
        this.includeInsert = includeInsert;
        this.numberOfInserts = numberOfInserts;
        return this;
    }

    public ScriptBuilder includeUpdate(boolean includeUpdate, String whereClause) {
        this.includeUpdate = includeUpdate;
        this.updateWhereClause = whereClause;
        return this;
    }

    public ScriptBuilder includeRead(boolean includeRead, String whereClause) {
        this.includeRead = includeRead;
        this.readWhereClause = whereClause;
        return this;
    }

    public String buildScript() {
        StringBuilder script = new StringBuilder();

        for (Table table : tables) {
            if (includeCreate) {
                script.append(CRUD.generateCreate(table)).append("\n");
            }
            if (includeDrop) {
                script.append(CRUD.generateDrop(table)).append("\n");
            }
            if (includeInsert) {
                script.append(table.generateInserts(numberOfInserts)).append("\n");
            }
            if (includeUpdate) {
                script.append(CRUD.generateUpdate(table, updateWhereClause)).append("\n");
            }
            if (includeRead) {
                script.append(CRUD.generateRead(table, readWhereClause)).append("\n");
            }
        }

        return script.toString();
    }
}
