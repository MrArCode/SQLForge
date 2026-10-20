package io.github.mrarcode.sqlforge.generators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs a generated script on a fresh in-memory H2 database in MySQL mode,
 * which checks column types and enforces foreign keys.
 */
final class H2Database {

    record ExecutedStatement(String sql, int updateCount) { }

    private H2Database() { }

    static List<ExecutedStatement> run(String script) throws SQLException {
        List<ExecutedStatement> executed = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:;MODE=MySQL");
             Statement statement = connection.createStatement()) {
            for (String sql : splitStatements(script)) {
                statement.execute(sql);
                executed.add(new ExecutedStatement(sql, statement.getUpdateCount()));
            }
        }
        return executed;
    }

    // Every statement ends with ';' at the end of a line; values never contain a line break.
    private static List<String> splitStatements(String script) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : script.split("\n")) {
            if (line.isBlank()) {
                continue;
            }
            current.append(line).append('\n');
            if (line.endsWith(";")) {
                statements.add(current.substring(0, current.length() - 2));
                current.setLength(0);
            }
        }
        return statements;
    }
}
