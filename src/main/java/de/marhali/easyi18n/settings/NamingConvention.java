package de.marhali.easyi18n.settings;

import com.google.common.base.CaseFormat;

import java.util.Arrays;

public enum NamingConvention {
    SNAKE_CASE("Snake Case"),

    CAMEL_CASE("Camel Case"),

    CAMEL_CASE_UPPERCASE("Camel Case Uppercase"),
    ;

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
}
