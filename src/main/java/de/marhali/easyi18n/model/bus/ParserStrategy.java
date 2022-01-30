package de.marhali.easyi18n.model.bus;

/**
 * Represents all supported file parser strategies.
 * @author marhali
 */
public enum ParserStrategy {
    JSON,
    YAML,
    PROPERTIES;

    public int toIndex() {
        int index = 0;

        for(ParserStrategy strategy : values()) {
            if(strategy == this) {
                return index;
            }

            index++;
        }

        throw new NullPointerException();
    }

    public static ParserStrategy fromIndex(int index) {
        return values()[index];
    }
}
