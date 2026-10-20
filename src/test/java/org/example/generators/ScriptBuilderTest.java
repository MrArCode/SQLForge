package org.example.generators;

import org.example.generators.H2Database.ExecutedStatement;
import org.example.models.Column;
import org.example.models.Table;
import org.example.models.Types;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptBuilderTest {

    @Test
    void readmeExampleRunsOnDatabase() throws SQLException {
        String script = ScriptBuilder.builder()
                .addTable(users())
                .addTable(orders())
                .includeCreate()
                .includeInsert(3)
                .includeRead()
                .withDataGenerator(new FakerDataGenerator())
                .buildScript();

        List<ExecutedStatement> inserts = H2Database.run(script).stream()
                .filter(statement -> statement.sql().startsWith("INSERT"))
                .toList();
        assertEquals(6, inserts.size());
        inserts.forEach(insert -> assertEquals(1, insert.updateCount(), insert.sql()));
    }

    @Test
    void demoScriptRunsOnDatabaseThatEnforcesForeignKeys() throws SQLException {
        // The same operations as Main: every statement type and as many deletes as inserted rows
        String script = ScriptBuilder.builder()
                .addTable(users())
                .addTable(orders())
                .includeCreate()
                .includeInsert(10)
                .includeRead()
                .includeUpdate(10)
                .includeDelete(10)
                .withDataGenerator(new FakerDataGenerator())
                .buildScript();

        H2Database.run(script);
    }

    @Test
    void deletesKeepRowsThatOtherRowsStillReference() throws SQLException {
        String script = ScriptBuilder.builder()
                .addTable(users())
                .addTable(orders())
                .includeCreate()
                .includeInsert(10)
                .includeDelete(3)
                .withDataGenerator(new FakerDataGenerator())
                .buildScript();

        H2Database.run(script);
    }

    @Test
    void everyUpdateAndDeleteChangesExactlyOneRow() throws SQLException {
        String script = ScriptBuilder.builder()
                .addTable(users())
                .addTable(orders())
                .includeCreate()
                .includeInsert(3)
                .includeUpdate(5)
                .includeDelete(5)
                .withDataGenerator(new FakerDataGenerator())
                .buildScript();

        List<ExecutedStatement> changes = H2Database.run(script).stream()
                .filter(statement -> statement.sql().startsWith("UPDATE") || statement.sql().startsWith("DELETE"))
                .toList();
        changes.forEach(change -> assertEquals(1, change.updateCount(), change.sql()));
        assertEquals(12, changes.size(), "3 updates and 3 deletes per table");
    }

    @Test
    void everyScriptStartsPrimaryKeysAtOne() {
        ScriptBuilder builder = ScriptBuilder.builder()
                .addTable(users())
                .includeInsert(1)
                .withDataGenerator(new FakerDataGenerator());
        String withAnotherGenerator = ScriptBuilder.builder()
                .addTable(users())
                .includeInsert(1)
                .withDataGenerator(new FakerDataGenerator())
                .buildScript();

        for (String script : List.of(builder.buildScript(), builder.buildScript(), withAnotherGenerator)) {
            assertTrue(script.contains("VALUES (1, "), script);
        }
    }

    @ParameterizedTest
    @EnumSource(Types.class)
    void everyTypeCanBeCreatedAndFilled(Types type) throws SQLException {
        Table samples = new Table("Samples");
        samples.addColumn(new Column("ID", Types.INTEGER).setPrimaryKey(true).setNotNull(true));
        samples.addColumn(new Column("Sample", type));

        String script = ScriptBuilder.builder()
                .addTable(samples)
                .includeCreate()
                .includeInsert(50)
                .withDataGenerator(new FakerDataGenerator())
                .buildScript();

        H2Database.run(script);
    }

    private static Table users() {
        Table users = new Table("Users");
        users.addColumn(new Column("ID", Types.INTEGER).setPrimaryKey(true).setNotNull(true));
        users.addColumn(new Column("FirstName", Types.FIRSTNAME).setNotNull(true));
        users.addColumn(new Column("Email", Types.EMAIL).setUnique(true).setNotNull(true));
        return users;
    }

    private static Table orders() {
        Table orders = new Table("Orders");
        orders.addColumn(new Column("OrderID", Types.INTEGER).setPrimaryKey(true).setNotNull(true));
        orders.addColumn(new Column("UserID", Types.INTEGER).setForeignKey("Users(ID)").setNotNull(true));
        orders.addColumn(new Column("OrderDate", Types.DATE_TIME).setNotNull(true));
        return orders;
    }
}
