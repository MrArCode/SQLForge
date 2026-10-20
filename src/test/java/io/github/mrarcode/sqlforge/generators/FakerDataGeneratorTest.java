package io.github.mrarcode.sqlforge.generators;

import io.github.mrarcode.sqlforge.models.Column;
import io.github.mrarcode.sqlforge.models.Types;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FakerDataGeneratorTest {

    private final Locale originalLocale = Locale.getDefault();

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(originalLocale);
    }

    // pl-PL and de-DE write a decimal comma, ar-EG and fa-IR use their own digits, th-TH uses the Buddhist calendar
    @ParameterizedTest
    @ValueSource(strings = {"pl-PL", "de-DE", "ar-EG", "fa-IR", "th-TH"})
    void numbersAndDatesAreValidSqlInEveryLocale(String languageTag) {
        Locale.setDefault(Locale.forLanguageTag(languageTag));
        FakerDataGenerator generator = new FakerDataGenerator();

        Map<Types, String> expectedFormats = Map.of(
                Types.FLOAT, "\\d+\\.\\d+",
                Types.DOUBLE, "\\d+\\.\\d+",
                Types.LATITUDE, "-?\\d+\\.\\d+",
                Types.LONGITUDE, "-?\\d+\\.\\d+",
                Types.DATE, "'(19|20)\\d\\d-\\d\\d-\\d\\d'",
                Types.DATE_TIME, "'(19|20)\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d'",
                Types.TIMESTAMP, "'(19|20)\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d'",
                Types.TIME, "'\\d\\d:\\d\\d:\\d\\d'");

        expectedFormats.forEach((type, format) -> {
            for (int i = 0; i < 20; i++) {
                String value = generator.generateData(new Column("Sample", type));
                assertTrue(value.matches(format), languageTag + " " + type + " -> " + value);
            }
        });
    }
}
