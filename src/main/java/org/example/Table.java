package org.example;

import com.github.javafaker.Faker;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Table {
    private static Map<String, AtomicInteger> idGenerators = new HashMap<>();
    private static final String OUTPUT_FOLDER = "output";
    private static final String INDEX_FOLDER = "index";

    private final String tableName;
    private final List<Column> columns = new ArrayList<>();
    private final Faker faker = new Faker(new Locale("en"));

    public Table(String tableName) {
        this.tableName = tableName;
        File indexFolder = new File(INDEX_FOLDER);
        if (!indexFolder.exists()) {
            indexFolder.mkdirs();
        }
        idGenerators.putIfAbsent(tableName, new AtomicInteger(loadLastID(tableName)));

        File outputFolder = new File(OUTPUT_FOLDER);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public String generateInserts(int numberOfRows) {
        StringBuilder allInserts = new StringBuilder();
        for (int i = 0; i < numberOfRows; i++) {
            StringBuilder insert = new StringBuilder();
            insert.append("INSERT INTO ");
            insert.append(tableName);
            insert.append(" (");

            for (int j = 0; j < columns.size(); j++) {
                insert.append(columns.get(j).getColumnName());
                if (j < columns.size() - 1) {
                    insert.append(", ");
                }
            }

            insert.append(") VALUES (");

            for (int j = 0; j < columns.size(); j++) {
                Column column = columns.get(j);
                insert.append(generateData(column));
                if (j < columns.size() - 1) {
                    insert.append(", ");
                }
            }

            insert.append(");");

            allInserts.append(insert);
            allInserts.append(System.lineSeparator());
        }

        saveLastID(tableName, idGenerators.get(tableName).get());

        return allInserts.toString();
    }

    private String generateData(Column column) {
        Types type = column.getDataType();
        String columnName = column.getColumnName();

        switch (type) {
            case INTEGER:
                if (columnName.equalsIgnoreCase("ID")) {
                    AtomicInteger idGenerator = idGenerators.get(tableName);
                    return String.valueOf(idGenerator.getAndIncrement());
                } else {
                    return String.valueOf(faker.number().numberBetween(1, 1000));
                }
            case STRING:
            case VARCHAR:
            case TEXT:
                return "'" + faker.lorem().word() + "'";
            case CHAR:
                return "'" + faker.lorem().character() + "'";
            case DATE:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return "'" + sdf.format(faker.date().birthday()) + "'";
            case TIMESTAMP:
                SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return "'" + sdfTimestamp.format(faker.date().past(1000, java.util.concurrent.TimeUnit.DAYS)) + "'";
            case FLOAT:
                return String.valueOf(faker.number().randomDouble(2, 1, 1000));
            case DOUBLE:
                return String.valueOf(faker.number().randomDouble(5, 1, 100000));
            case BOOLEAN:
                return String.valueOf(faker.bool().bool());
            case BLOB:
                return "'<BLOB data>'";
            case CITY:
                return "'" + faker.address().city() + "'";
            case COUNTRY:
                String country = faker.address().country();
                country = country.replace("'", "''");
                return "'" + country + "'";
            case EMAIL:
                return "'" + faker.internet().emailAddress() + "'";
            case PHONENUMBER:
                return "'" + faker.phoneNumber().phoneNumber() + "'";
            case ADDRESS:
                return "'" + faker.address().fullAddress() + "'";
            case ZIPCODE:
                return "'" + faker.address().zipCode() + "'";
            case COMPANY:
                return "'" + faker.company().name() + "'";
            case JOBTITLE:
                return "'" + faker.job().title() + "'";
            case NAME:
                String name = faker.name().fullName();
                name = name.replace("'", "''");
                return "'" + name + "'";
            case FIRSTNAME:
                String firstName = faker.name().firstName();
                firstName = firstName.replace("'", "''");
                return "'" + firstName + "'";
            case LASTNAME:
                String lastName = faker.name().lastName();
                lastName = lastName.replace("'", "''");
                return "'" + lastName + "'";
            case UUID:
                return "'" + faker.internet().uuid() + "'";
            case IBAN:
                return "'" + faker.finance().iban() + "'";
            case CREDIT_CARD:
                return "'" + faker.finance().creditCard() + "'";
            case URL:
                return "'" + faker.internet().url() + "'";
            case IP_ADDRESS:
                return "'" + faker.internet().ipV4Address() + "'";
            case MAC_ADDRESS:
                return "'" + faker.internet().macAddress() + "'";
            case LATITUDE:
                return String.valueOf(faker.address().latitude());
            case LONGITUDE:
                return String.valueOf(faker.address().longitude());
            case USER_AGENT:
                return "'" + faker.internet().userAgentAny() + "'";
            case PASSWORD:
                return "'" + faker.internet().password() + "'";
            case HEX_COLOR:
                return "'" + faker.color().hex() + "'";
            case FILE_NAME:
                return "'" + faker.file().fileName() + "'";
            case LOREM_SENTENCE:
                return "'" + faker.lorem().sentence() + "'";
            case LOREM_PARAGRAPH:
                return "'" + faker.lorem().paragraph() + "'";
            case TIME:
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                return "'" + timeFormat.format(faker.date().birthday()) + "'";
            case DATE_TIME:
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return "'" + dateTimeFormat.format(faker.date().birthday()) + "'";
            case ISBN:
                return "'" + faker.code().isbn13() + "'";
            case CURRENCY_CODE:
                return "'" + faker.currency().code() + "'";
            case NATIONALITY:
                return "'" + faker.nation().nationality() + "'";
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private void saveLastID(String tableName, int lastID) {
        String idFileName = INDEX_FOLDER + File.separator + tableName + "_lastID.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(idFileName))) {
            writer.write(String.valueOf(lastID));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int loadLastID(String tableName) {
        String idFileName = INDEX_FOLDER + File.separator + tableName + "_lastID.txt";
        File file = new File(idFileName);
        if (!file.exists()) {
            return 1;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            return Integer.parseInt(line);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }
}