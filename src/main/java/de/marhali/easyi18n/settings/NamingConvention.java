package de.marhali.easyi18n.settings;

import com.google.common.base.CaseFormat;

import java.util.Arrays;

public enum NamingConvention {
    CAMEL_CASE("Camel Case"),
    CAMEL_CASE_UPPERCASE("Camel Case Uppercase"),
    SNAKE_CASE("Snake Case"),
    SNAKE_CASE_UPPERCASE("Snake Case Uppercase");

    private final String name;

    private NamingConvention(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }

    static public NamingConvention fromSelector(String name) {
        String formated = name.replace(" ", "_");
        return valueOf(formated.toUpperCase());
    }

    static public String[] getEnumNames() {
        return Arrays.stream(NamingConvention.values())
                .map(NamingConvention::getName)
                .toArray(String[]::new);
    }

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
