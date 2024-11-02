package org.example;

import org.example.generator.FakerDataGenerator;
import org.example.generator.ScriptBuilder;
import org.example.model.Column;
import org.example.model.Table;
import org.example.model.Types;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
                .includeCreate(true)
                .includeInsert(true, 10)
                .includeRead(true)
                .withDataGenerator(dataGenerator)
                .buildScript();

        System.out.println(script);

        String fileName = "output" + File.separator + "script.sql";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(script);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
