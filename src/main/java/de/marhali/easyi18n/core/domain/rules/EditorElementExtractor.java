package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Editor element extractor.
 *
 * @param <T> Underlying editor element class
 *
 * @author marhali
 */
public interface EditorElementExtractor<T> {
    /**
     * Extracts the underlying editor element.
     * @param value Underlying editor element
     * @return {@link EditorElement} or {@code null} if value could not be extracted
     */
    @Nullable EditorElement extract(@NotNull T value);
}
