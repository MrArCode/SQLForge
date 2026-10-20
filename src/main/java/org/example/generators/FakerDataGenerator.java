package org.example.generators;

import com.github.javafaker.Faker;
import org.example.models.Column;
import org.example.models.Types;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

// Numbers and dates are formatted with Locale.ROOT: SQL needs a decimal point, ASCII digits
// and the Gregorian calendar, whatever the system locale is.
public class FakerDataGenerator implements DataGenerator {
    private final Faker faker;

    public FakerDataGenerator() {
        faker = new Faker(Locale.ENGLISH);
    }

    @Override
    public String generateData(Column column) {
        Types type = column.getDataType();

        // No default branch: the compiler checks that every value of Types has a generator.
        return switch (type) {
            case INTEGER -> String.valueOf(faker.number().numberBetween(1, 1000));
            case STRING, VARCHAR, TEXT -> "'" + faker.lorem().word().replace("'", "''") + "'";
            case CHAR -> "'" + faker.lorem().character() + "'";
            case DATE -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
                yield "'" + sdf.format(randomBirthday()) + "'";
            }
            case TIMESTAMP, DATE_TIME -> {
                SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
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
            case LATITUDE -> String.format(Locale.ROOT, "%.6f", faker.number().randomDouble(6, -90, 90));
            case LONGITUDE -> String.format(Locale.ROOT, "%.6f", faker.number().randomDouble(6, -180, 180));
            case USER_AGENT -> "'" + faker.internet().userAgentAny().replace("'", "''") + "'";
            case PASSWORD -> "'" + faker.internet().password().replace("'", "''") + "'";
            case HEX_COLOR -> "'" + faker.color().hex().replace("'", "''") + "'";
            case FILE_NAME -> "'" + faker.file().fileName().replace("'", "''") + "'";
            case LOREM_SENTENCE -> "'" + faker.lorem().sentence().replace("'", "''") + "'";
            case LOREM_PARAGRAPH -> "'" + faker.lorem().paragraph().replace("'", "''") + "'";
            case TIME -> {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ROOT);
                yield "'" + timeFormat.format(randomBirthday()) + "'";
            }
            case ISBN -> "'" + faker.code().isbn13().replace("'", "''") + "'";
            case CURRENCY_CODE -> "'" + faker.currency().code().replace("'", "''") + "'";
            case NATIONALITY -> "'" + faker.nation().nationality().replace("'", "''") + "'";
            case BLOB -> "NULL";
        };
    }

    // Someone aged 18 to 65. Faker's own birthday() takes the current year from the default calendar,
    // which is not Gregorian everywhere (in Thailand it is 543 years ahead).
    private Date randomBirthday() {
        ZonedDateTime now = ZonedDateTime.now();
        return faker.date().between(Date.from(now.minusYears(65).toInstant()), Date.from(now.minusYears(18).toInstant()));
    }
}
