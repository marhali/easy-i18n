package de.marhali.easyi18n.config;

/**
 * Every preset needs to implement this interface to specify the preset state.
 * @param <State> Underlying state
 */
public interface PresetProvider<State> {
    /**
     * Returns the preset state. Implementations might apply elements from the previous state if applicable.
     * @param previousState Current state
     * @return Preset state
     */
    State applyPreset(State previousState);
}
