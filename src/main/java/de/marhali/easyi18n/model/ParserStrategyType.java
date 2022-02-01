package de.marhali.easyi18n.model;

/**
 * Represents all supported file parser strategies.
 * @author marhali
 */
public enum ParserStrategyType {
    JSON,
    YAML,
    PROPERTIES;

    public String getExampleFilePattern() {
        return "*." + toString().toLowerCase();
    }

    public int toIndex() {
        int index = 0;

        for(ParserStrategyType strategy : values()) {
            if(strategy == this) {
                return index;
            }

            index++;
        }

        throw new NullPointerException();
    }

    public static ParserStrategyType fromIndex(int index) {
        return values()[index];
    }
}
