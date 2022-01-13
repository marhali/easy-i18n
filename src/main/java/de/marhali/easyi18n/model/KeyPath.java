package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a full translation key with all sections.
 * Implementations can use single section or variable section length variants.
 * The respective layer (io, presentation) is responsible for using the correct mapping mechanism.
 * @author marhali
 */
public class KeyPath extends ArrayList<String> {

    public static final String DELIMITER = ".";

    public static KeyPath of(@NotNull String... path) {
        return new KeyPath(List.of(path));
    }

    public KeyPath() {
        super();
    }

    public KeyPath(@NotNull KeyPath path, String... pathToAppend) {
        this(path);
        this.addAll(List.of(pathToAppend));
    }

    public KeyPath(@NotNull Collection<? extends String> c) {
        super(c);
    }

    public KeyPath(@NotNull String simplePath) {
        this(List.of(simplePath.split(Pattern.quote(DELIMITER))));
    }

    /**
     * <b>Note: </b>Use {@link KeyPathConverter} if you want to keep hierarchy.
     * @return simple path representation by adding delimiter between the secton nodes
     */
    public String toSimpleString() {
        StringBuilder builder = new StringBuilder();

        for(String section : this) {
            if(builder.length() > 0) {
                builder.append(DELIMITER);
            }

            builder.append(section);
        }

        return builder.toString();
    }
}