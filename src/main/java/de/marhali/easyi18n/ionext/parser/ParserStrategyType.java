package de.marhali.easyi18n.ionext.parser;

import de.marhali.easyi18n.ionext.parser.json.JsonParserStrategy;
import de.marhali.easyi18n.ionext.parser.properties.PropertiesParserStrategy;
import de.marhali.easyi18n.ionext.parser.yaml.YamlParserStrategy;

/**
 * Represents all supported file parser strategies.
 * @author marhali
 */
public enum ParserStrategyType {
    JSON(JsonParserStrategy.class),
    YAML(YamlParserStrategy.class),
    YML(YamlParserStrategy.class),
    PROPERTIES(PropertiesParserStrategy.class),
    ARB(JsonParserStrategy.class);

    private final Class<? extends ParserStrategy> strategy;

    ParserStrategyType(Class<? extends ParserStrategy> strategy) {
        this.strategy = strategy;
    }

    public Class<? extends ParserStrategy> getStrategy() {
        return strategy;
    }

    public String getFileExtension() {
        return toString().toLowerCase();
    }

    public String getExampleFilePattern() {
        return "*." + this.getFileExtension();
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
