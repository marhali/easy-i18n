package de.marhali.easyi18n.core.domain.config.preset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Every preset needs to implement this interface to specify the preset state.
 *
 * @param <State> Underlying state
 *
 * @author marhali
 */
public interface PresetProvider<State> {
    /**
     * Returns the preset state. Implementations might apply elements from the previous state if applicable.
     * @param previousState Current state
     * @return Preset state
     */
    @NotNull State applyPreset(@Nullable State previousState);
}
