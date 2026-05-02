package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an editor file path.
 *
 * @param canonical Canonical file path
 *
 * @author marhali
 */
public record EditorFilePath(
    @Nullable String canonical
) {
    /**
     * Shorthand to construct an unknown editor file path.
     * @return {@link EditorFilePath}
     */
    public static @NotNull EditorFilePath empty() {
        return new EditorFilePath(null);
    }
}
