package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the absolute key path for a desired translation.
 * The key could be based one or many sections.
 * Classes implementing this structure need to take care on how to layer translations paths.
 * @author marhali
 */
public class KeyPath extends ArrayList<String> {

    public KeyPath() {}

    public KeyPath(@Nullable String... path) {
        super.addAll(List.of(path));
    }

    public KeyPath(@NotNull List<String> path) {
        super(path);
    }

    public KeyPath(@NotNull KeyPath path, @Nullable String... pathToAppend) {
        this(path);
        super.addAll(List.of(pathToAppend));
    }

    @Override
    public String toString() {
        // Just a simple array view (e.g. [first, second]) - use KeyPathConverter to properly convert a key path
        return super.toString();
    }
}
