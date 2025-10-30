package de.marhali.easyi18n.config;

/**
 * @param <State>   Associated state holder
 * @param <Builder> UI component builder
 * @author marhali
 */
public interface ConfigComponent<State, Builder> {
    void buildComponent(Builder builder);

    void applyStateToComponent(State state);

    boolean isModified();

    void applyChangesToState();
}
