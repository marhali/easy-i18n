package de.marhali.easyi18n.ionext.file;

import java.util.Arrays;

/**
 * Enumeration of every supported file (content) parser.
 * Mapping between file extension and file parser is handled via configuration.
 * @author marhali
 */
public enum FileParser {
    JSON("JSON"),
    JSON5("JSON5"),
    YAML("YAML"),
    PROPERTIES("Properties"),
    ;

    public static FileParser fromDisplayName(String displayName) {
        return Arrays.stream(values())
            .filter(parser -> parser.displayName.equals(displayName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find file parser by displayName: " + displayName));
    }

    public static String[] displayNames() {
        return Arrays.stream(values())
            .map(FileParser::getDisplayName)
            .toArray(String[]::new);
    }

    private final String displayName;

    FileParser(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }
}
