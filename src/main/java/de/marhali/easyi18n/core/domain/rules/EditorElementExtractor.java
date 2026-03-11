package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Editor element extractor.
 *
 * @param <E> Underlying editor element class
 * @param <F> Underlying editor file class
 *
 * @author marhali
 */
public interface EditorElementExtractor<E, F> {
    /**
     * Extracts the underlying editor element.
     * @param element Underlying editor element
     * @param file Underlying editor file
     * @return {@link EditorElement} or {@code null} if value could not be extracted
     */
    @Nullable EditorElement extract(@NotNull E element, @Nullable F file);
}
