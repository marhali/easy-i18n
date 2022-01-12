package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a full translation key with all sections.
 * Implementations can use single section or variable section length variants.
 * The respective layer (io, presentation) is responsible for using the correct mapping mechanism.
 * @author marhali
 */
public class KeyPath extends ArrayList<String> {

    public static final String DELIMITER = ".";

    public static KeyPath of(String... path) {
        return new KeyPath(List.of(path));
    }

    public KeyPath() {
        super();
    }

    public KeyPath(@NotNull Collection<? extends String> c) {
        super(c);
    }
}