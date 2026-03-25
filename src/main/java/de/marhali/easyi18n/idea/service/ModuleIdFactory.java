package de.marhali.easyi18n.idea.service;

import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Factory to construct {@link ModuleId}.
 *
 * @author marhali
 */
public final class ModuleIdFactory {
    private ModuleIdFactory() {}

    public static @NotNull ModuleId fromInput(@NotNull String input) {
        return new ModuleId(input);
    }
}
