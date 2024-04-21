package de.marhali.easyi18n.settings.presets;

import com.google.common.base.CaseFormat;

import java.util.Arrays;

/**
 * Enum representing different naming conventions.
 * Provides utility methods to convert keys to the specified convention.
 */
public enum NamingConvention {
    CAMEL_CASE("Camel Case"),
    CAMEL_CASE_UPPERCASE("Camel Case (Uppercase)"),
    SNAKE_CASE("Snake Case"),
    SNAKE_CASE_UPPERCASE("Snake Case (Uppercase)");

    private final String name;

    private NamingConvention(String name) {
        this.name = name;
    }

    /**
     * Retrieves the name of the current instance of the class.
     *
     * @return the name of the current instance
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }

    /**
     * Converts a string representation of a naming convention to the corresponding NamingConvention enum value.
     *
     * @param name the string representation of the naming convention
     * @return the corresponding NamingConvention enum value
     */
    static public NamingConvention fromString(String name) {
        for (NamingConvention value : NamingConvention.values()) {
            if (value.getName().equals(name))
                return value;
        }
        return NamingConvention.CAMEL_CASE;
    }

    /**
     * Returns an array of strings representing the names of the enum values in the {@link NamingConvention} enum.
     *
     * @return an array of strings representing the enum names
     */
    static public String[] getEnumNames() {
        return Arrays.stream(NamingConvention.values())
                .map(NamingConvention::getName)
                .toArray(String[]::new);
    }

    /**
     * Converts a given key to the specified naming convention.
     *
     * @param key        the key to convert
     * @param convention the naming convention to convert the key to
     * @return the converted key
     */
    static public String convertKeyToConvention(String key, NamingConvention convention) {
        String newKey = key.toLowerCase();
        newKey = newKey.replaceAll("\\s+", "_");
        return switch (convention) {
            case SNAKE_CASE:
                yield formatToSnakeCase(newKey, false);
            case SNAKE_CASE_UPPERCASE:
                yield formatToSnakeCase(newKey, true);
            case CAMEL_CASE:
                yield formatToCamelCase(newKey, false);
            case CAMEL_CASE_UPPERCASE:
                yield formatToCamelCase(newKey, true);

        };
    }

    static private String formatToCamelCase(String key, boolean capitalized) {
        return CaseFormat.LOWER_UNDERSCORE.to(capitalized ? CaseFormat.UPPER_CAMEL : CaseFormat.LOWER_CAMEL, key);
    }

    static private String formatToSnakeCase(String key, boolean capitalized) {
        return CaseFormat.LOWER_UNDERSCORE.to(capitalized ? CaseFormat.UPPER_UNDERSCORE : CaseFormat.LOWER_UNDERSCORE, key);
    }
}
