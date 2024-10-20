package org.example.generator;

import com.github.javafaker.Faker;
import org.example.model.Column;
import org.example.model.Types;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FakerDataGenerator implements DataGenerator {
    private final Faker faker;
    private static Map<String, AtomicInteger> idGenerators = new HashMap<>();
    private Map<String, List<String>> primaryKeyValues = new HashMap<>();
    private Random random = new Random();

    public FakerDataGenerator() {
        faker = new Faker(new Locale("en"));
    }

    @Override
    public String generateData(Column column) {
        Types type = column.getDataType();
        String columnName = column.getColumnName();

        if (column.isPrimaryKey()) {
            AtomicInteger idGenerator = idGenerators.computeIfAbsent(column.getTableName(), k -> new AtomicInteger(1));
            String idValue = String.valueOf(idGenerator.getAndIncrement());
            primaryKeyValues.computeIfAbsent(column.getTableName(), k -> new ArrayList<>()).add(idValue);
            return idValue;
        } else if (column.getForeignKeyReference() != null) {
            String[] refParts = column.getForeignKeyReference().split("\\(");
            String refTableName = refParts[0];
            List<String> refValues = primaryKeyValues.get(refTableName);
            if (refValues != null && !refValues.isEmpty()) {
                String refValue = refValues.get(random.nextInt(refValues.size()));
                return refValue;
            } else {
                return "NULL";
            }
        }

        switch (type) {
            case INTEGER:
                return String.valueOf(faker.number().numberBetween(1, 1000));
            case STRING:
            case VARCHAR:
            case TEXT:
                return "'" + faker.lorem().word().replace("'", "''") + "'";
            case CHAR:
                return "'" + faker.lorem().character() + "'";
            case DATE:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return "'" + sdf.format(faker.date().birthday()) + "'";
            case TIMESTAMP:
            case DATE_TIME:
                SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return "'" + sdfTimestamp.format(faker.date().past(1000, java.util.concurrent.TimeUnit.DAYS)) + "'";
            case FLOAT:
                return String.valueOf(faker.number().randomDouble(2, 1, 1000));
            case DOUBLE:
                return String.valueOf(faker.number().randomDouble(5, 1, 100000));
            case BOOLEAN:
                return String.valueOf(faker.bool().bool());
            case CITY:
                return "'" + faker.address().city().replace("'", "''") + "'";
            case COUNTRY:
                return "'" + faker.address().country().replace("'", "''") + "'";
            case EMAIL:
                return "'" + faker.internet().emailAddress().replace("'", "''") + "'";
            case PHONENUMBER:
                return "'" + faker.phoneNumber().phoneNumber().replace("'", "''") + "'";
            case ADDRESS:
                return "'" + faker.address().fullAddress().replace("'", "''") + "'";
            case ZIPCODE:
                return "'" + faker.address().zipCode().replace("'", "''") + "'";
            case COMPANY:
                return "'" + faker.company().name().replace("'", "''") + "'";
            case JOBTITLE:
                return "'" + faker.job().title().replace("'", "''") + "'";
            case NAME:
                return "'" + faker.name().fullName().replace("'", "''") + "'";
            case FIRSTNAME:
                return "'" + faker.name().firstName().replace("'", "''") + "'";
            case LASTNAME:
                return "'" + faker.name().lastName().replace("'", "''") + "'";
            case UUID:
                return "'" + faker.internet().uuid().replace("'", "''") + "'";
            case IBAN:
                return "'" + faker.finance().iban().replace("'", "''") + "'";
            case CREDIT_CARD:
                return "'" + faker.finance().creditCard().replace("'", "''") + "'";
            case URL:
                return "'" + faker.internet().url().replace("'", "''") + "'";
            case IP_ADDRESS:
                return "'" + faker.internet().ipV4Address().replace("'", "''") + "'";
            case MAC_ADDRESS:
                return "'" + faker.internet().macAddress().replace("'", "''") + "'";
            case LATITUDE:
                return String.valueOf(faker.address().latitude());
            case LONGITUDE:
                return String.valueOf(faker.address().longitude());
            case USER_AGENT:
                return "'" + faker.internet().userAgentAny().replace("'", "''") + "'";
            case PASSWORD:
                return "'" + faker.internet().password().replace("'", "''") + "'";
            case HEX_COLOR:
                return "'" + faker.color().hex().replace("'", "''") + "'";
            case FILE_NAME:
                return "'" + faker.file().fileName().replace("'", "''") + "'";
            case LOREM_SENTENCE:
                return "'" + faker.lorem().sentence().replace("'", "''") + "'";
            case LOREM_PARAGRAPH:
                return "'" + faker.lorem().paragraph().replace("'", "''") + "'";
            case TIME:
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                return "'" + timeFormat.format(faker.date().birthday()) + "'";
            case ISBN:
                return "'" + faker.code().isbn13().replace("'", "''") + "'";
            case CURRENCY_CODE:
                return "'" + faker.currency().code().replace("'", "''") + "'";
            case NATIONALITY:
                return "'" + faker.nation().nationality().replace("'", "''") + "'";
            default:
                return "NULL";
        }
    }
}
