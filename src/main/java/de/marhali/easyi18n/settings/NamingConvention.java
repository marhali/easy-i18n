package de.marhali.easyi18n.settings;

import com.google.common.base.CaseFormat;

public enum NamingConvention {
    SNAKE_CASE("Snake"),
    CAMEL_CASE("Camel"),;
    private final String name;

    private NamingConvention(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }
}
