package de.marhali.easyi18n.next_io.file;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @param key
 * @param value
 * @param comment
 * @author marhali
 */
public record FileTranslation(
    @NotNull List<String> key,
    @NotNull Object value,
    @Nullable String comment
) {
}
