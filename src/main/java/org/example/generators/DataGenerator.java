package org.example.generators;

import org.example.models.Column;

public interface DataGenerator {
    /**
     * Returns a ready-to-use SQL literal for the column, such as {@code 'Alice'}, {@code 42} or {@code NULL}.
     * It is not called for primary and foreign key columns: {@link ScriptBuilder} fills those in itself.
     */
    String generateData(Column column);
}
