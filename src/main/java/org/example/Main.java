package org.example;

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
        usersTable.addColumn(new Column("ID", Types.INTEGER));
        usersTable.addColumn(new Column("FirstName", Types.FIRSTNAME));
        usersTable.addColumn(new Column("LastName", Types.LASTNAME));
        usersTable.addColumn(new Column("Email", Types.EMAIL));
        usersTable.addColumn(new Column("Password", Types.PASSWORD));
        usersTable.addColumn(new Column("RegistrationDate", Types.DATE));
        usersTable.addColumn(new Column("LastLogin", Types.TIMESTAMP));
        usersTable.addColumn(new Column("IPAddress", Types.IP_ADDRESS));
        usersTable.addColumn(new Column("Country", Types.COUNTRY));
        usersTable.addColumn(new Column("Currency", Types.CURRENCY_CODE));

        String script = ScriptBuilder.builder()
                .addTable(usersTable)
                .includeCreate(true)
                .includeDrop(true)
                .includeInsert(true, 10)
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
