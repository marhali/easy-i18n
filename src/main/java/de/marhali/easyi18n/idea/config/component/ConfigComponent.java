package de.marhali.easyi18n.idea.config.component;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Superclass of every configuration component.
 *
 * @param <ComponentBuilder> Builder to use for component creation
 * @param <ConfigState> Underlying component state
 * @param <ConfigBuilder> State builder
 *
 * @author marhali
 */
public abstract class ConfigComponent<ComponentBuilder, ConfigState, ConfigBuilder> {

    /**
     * The associated idea {@link Project}.
     */
    protected final @NotNull Project project;

    protected ConfigComponent(@NotNull Project project) {
        this.project = project;
    }

    /**
     * Builds the component by using the provided builder.
     * @param builder ComponentBuilder
     */
    public abstract void buildComponent(@NotNull ComponentBuilder builder);

    /**
     * Checks whether the represented is modified by comparing it against the provided origin state.
     * @param originState Origin state
     * @return true if the component differs from the provided origin state, otherwise false
     */
    public abstract boolean isModified(@NotNull ConfigState originState);

    /**
     * Writes the provided state to the component
     * @param state State to apply
     */
    public abstract void writeStateToComponent(@NotNull ConfigState state);

    /**
     * Reads the state from the component and applies it to the provided builder.
     * @param builder ConfigBuilder
     */
    public abstract void readStateFromComponent(@NotNull ConfigBuilder builder);
}
