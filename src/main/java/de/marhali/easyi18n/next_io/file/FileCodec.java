package de.marhali.easyi18n.next_io.file;

import de.marhali.easyi18n.next_io.file.json.JsonFileProcessor;

import java.util.Arrays;

/**
 * Enumeration of every supported file (content) format.
 * Mapping between file extension and file parser is handled via configuration.
 *
 * @author marhali
 */
public enum FileCodec {
    JSON("JSON", JsonFileProcessor.class),
    JSON5("JSON5", JsonFileProcessor.class),
    YAML("YAML", JsonFileProcessor.class),
    PROPERTIES("Properties", JsonFileProcessor.class),
    ;

    public static FileCodec fromDisplayName(String displayName) {
        return Arrays.stream(values())
            .filter(parser -> parser.displayName.equals(displayName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find file parser by displayName: " + displayName));
    }

    public static String[] displayNames() {
        return Arrays.stream(values())
            .map(FileCodec::getDisplayName)
            .toArray(String[]::new);
    }

    private final String displayName;
    private final Class<? extends FileProcessor> fileProcessorClass;

    FileCodec(String displayName, Class<? extends FileProcessor> fileProcessorClass) {
        this.displayName = displayName;
        this.fileProcessorClass = fileProcessorClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class<? extends FileProcessor> getFileProcessorClass() {
        return this.fileProcessorClass;
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }
}
