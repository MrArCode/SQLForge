package org.example.generators;

import org.example.generators.H2Database.ExecutedStatement;
import org.example.models.Column;
import org.example.models.Table;
import org.example.models.Types;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
