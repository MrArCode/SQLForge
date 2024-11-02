package org.example.generator;

import com.github.javafaker.Faker;
import org.example.model.Column;
import org.example.model.Types;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FakerDataGenerator implements DataGenerator {
    private final Faker faker;
    private static final Map<String, AtomicInteger> idGenerators = new HashMap<>();
    private final Map<String, List<String>> primaryKeyValues = new HashMap<>();
    private final Random random = new Random();

    public FakerDataGenerator() {
        faker = new Faker(new Locale("en"));
    }

    @Override
    public String generateData(Column column) {
        Types type = column.getDataType();

        if (column.isPrimaryKey()) {
            AtomicInteger idGenerator = idGenerators.computeIfAbsent(column.getTableName(), _ -> new AtomicInteger(1));
            String idValue = String.valueOf(idGenerator.getAndIncrement());
            primaryKeyValues.computeIfAbsent(column.getTableName(), _ -> new ArrayList<>()).add(idValue);
            return idValue;
        } else if (column.getForeignKeyReference() != null) {
            String[] refParts = column.getForeignKeyReference().split("\\(");
            String refTableName = refParts[0];
            List<String> refValues = primaryKeyValues.get(refTableName);
            if (refValues != null && !refValues.isEmpty()) {
                return refValues.get(random.nextInt(refValues.size()));
            } else {
                return "NULL";
            }
        }

        return switch (type) {
            case INTEGER -> String.valueOf(faker.number().numberBetween(1, 1000));
            case STRING, VARCHAR, TEXT -> "'" + faker.lorem().word().replace("'", "''") + "'";
            case CHAR -> "'" + faker.lorem().character() + "'";
            case DATE -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                yield "'" + sdf.format(faker.date().birthday()) + "'";
            }
            case TIMESTAMP, DATE_TIME -> {
                SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                yield "'" + sdfTimestamp.format(faker.date().past(1000, java.util.concurrent.TimeUnit.DAYS)) + "'";
            }
            case FLOAT -> String.valueOf(faker.number().randomDouble(2, 1, 1000));
            case DOUBLE -> String.valueOf(faker.number().randomDouble(5, 1, 100000));
            case BOOLEAN -> String.valueOf(faker.bool().bool());
            case CITY -> "'" + faker.address().city().replace("'", "''") + "'";
            case COUNTRY -> "'" + faker.address().country().replace("'", "''") + "'";
            case EMAIL -> "'" + faker.internet().emailAddress().replace("'", "''") + "'";
            case PHONENUMBER -> "'" + faker.phoneNumber().phoneNumber().replace("'", "''") + "'";
            case ADDRESS -> "'" + faker.address().fullAddress().replace("'", "''") + "'";
            case ZIPCODE -> "'" + faker.address().zipCode().replace("'", "''") + "'";
            case COMPANY -> "'" + faker.company().name().replace("'", "''") + "'";
            case JOBTITLE -> "'" + faker.job().title().replace("'", "''") + "'";
            case NAME -> "'" + faker.name().fullName().replace("'", "''") + "'";
            case FIRSTNAME -> "'" + faker.name().firstName().replace("'", "''") + "'";
            case LASTNAME -> "'" + faker.name().lastName().replace("'", "''") + "'";
            case UUID -> "'" + faker.internet().uuid().replace("'", "''") + "'";
            case IBAN -> "'" + faker.finance().iban().replace("'", "''") + "'";
            case CREDIT_CARD -> "'" + faker.finance().creditCard().replace("'", "''") + "'";
            case URL -> "'" + faker.internet().url().replace("'", "''") + "'";
            case IP_ADDRESS -> "'" + faker.internet().ipV4Address().replace("'", "''") + "'";
            case MAC_ADDRESS -> "'" + faker.internet().macAddress().replace("'", "''") + "'";
            case LATITUDE -> String.valueOf(faker.address().latitude());
            case LONGITUDE -> String.valueOf(faker.address().longitude());
            case USER_AGENT -> "'" + faker.internet().userAgentAny().replace("'", "''") + "'";
            case PASSWORD -> "'" + faker.internet().password().replace("'", "''") + "'";
            case HEX_COLOR -> "'" + faker.color().hex().replace("'", "''") + "'";
            case FILE_NAME -> "'" + faker.file().fileName().replace("'", "''") + "'";
            case LOREM_SENTENCE -> "'" + faker.lorem().sentence().replace("'", "''") + "'";
            case LOREM_PARAGRAPH -> "'" + faker.lorem().paragraph().replace("'", "''") + "'";
            case TIME -> {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                yield "'" + timeFormat.format(faker.date().birthday()) + "'";
            }
            case ISBN -> "'" + faker.code().isbn13().replace("'", "''") + "'";
            case CURRENCY_CODE -> "'" + faker.currency().code().replace("'", "''") + "'";
            case NATIONALITY -> "'" + faker.nation().nationality().replace("'", "''") + "'";
            default -> "NULL";
        };
    }
}
