package de.marhali.easyi18n.idea.service;

import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;

/**
 * Factory to construct {@link LocaleId}.
 *
 * @author marhali
 */
public final class LocaleIdFactory {
    private LocaleIdFactory() {}

    public static @NotNull LocaleId fromInput(@NotNull String input) {
        return new LocaleId(input);
    }
}
