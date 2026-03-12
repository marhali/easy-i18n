package de.marhali.easyi18n.core.domain.rules;

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
}
