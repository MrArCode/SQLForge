package org.example;

import org.example.generators.FakerDataGenerator;
import org.example.generators.ScriptBuilder;
import org.example.models.Column;
import org.example.models.Table;
import org.example.models.Types;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {


        Table usersTable = new Table("Users");

        usersTable.addColumn(new Column("ID", Types.INTEGER)
                .setPrimaryKey(true)
                .setNotNull(true));

        usersTable.addColumn(new Column("FirstName", Types.FIRSTNAME)
                .setNotNull(true));

        usersTable.addColumn(new Column("LastName", Types.LASTNAME)
                .setNotNull(true));

        usersTable.addColumn(new Column("Email", Types.EMAIL)
                .setUnique(true)
                .setNotNull(true));


        Table ordersTable = new Table("Orders");
        ordersTable.addColumn(new Column("OrderID", Types.INTEGER)
                .setPrimaryKey(true)
                .setNotNull(true));

        ordersTable.addColumn(new Column("UserID", Types.INTEGER)
                .setForeignKey("Users(ID)")
                .setNotNull(true));

        ordersTable.addColumn(new Column("OrderDate", Types.DATE_TIME)
                .setNotNull(true));


        FakerDataGenerator dataGenerator = new FakerDataGenerator();


        String script = ScriptBuilder.builder()
                .addTable(usersTable)
                .addTable(ordersTable)
                .includeCreate()
                .includeInsert( 10)
                .includeRead()
                .includeUpdate(10)
                .includeDelete(10)
                .withDataGenerator(dataGenerator)
                .buildScript();

        System.out.println(script);

        Path outputFile = Path.of("output", "script.sql");
        try {
            Files.createDirectories(outputFile.getParent());
            Files.writeString(outputFile, script);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
