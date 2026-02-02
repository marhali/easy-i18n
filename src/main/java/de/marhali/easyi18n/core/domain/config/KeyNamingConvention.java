package de.marhali.easyi18n.core.domain.config;

import com.google.common.base.CaseFormat;

/**
 * Enum representing different naming conventions.
 * Provides utility methods to convert keys to the specified convention.
 *
 * @author marhali
 */
public enum KeyNamingConvention {
    CAMEL_CASE,
    PASCAL_CASE,
    SNAKE_CASE,
    SNAKE_CASE_UPPERCASE,
    ;

    /**
     * Converts a given key to the specified naming convention.
     *
     * @param key        the key to convert
     * @param convention the naming convention to convert the key to
     * @return the converted key
     */
    static public String convertKeyToConvention(String key, KeyNamingConvention convention) {
        String newKey = key.toLowerCase();
        newKey = newKey.replaceAll("\\s+", "_");
        return switch (convention) {
            case SNAKE_CASE -> formatToSnakeCase(newKey, false);
            case SNAKE_CASE_UPPERCASE -> formatToSnakeCase(newKey, true);
            case CAMEL_CASE -> formatToCamelCase(newKey, false);
            case PASCAL_CASE -> formatToCamelCase(newKey, true);

        };
    }

    static private String formatToCamelCase(String key, boolean capitalized) {
        return CaseFormat.LOWER_UNDERSCORE.to(capitalized ? CaseFormat.UPPER_CAMEL : CaseFormat.LOWER_CAMEL, key);
    }

    static private String formatToSnakeCase(String key, boolean capitalized) {
        return CaseFormat.LOWER_UNDERSCORE.to(capitalized ? CaseFormat.UPPER_UNDERSCORE : CaseFormat.LOWER_UNDERSCORE, key);
    }
}
