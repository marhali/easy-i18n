package de.marhali.easyi18n.core.application.state;

import de.marhali.easyi18n.core.domain.model.I18nProject;
import de.marhali.easyi18n.core.domain.model.MutableI18nProject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Core state that tracks all loaded translations for every module.
 *
 * @author marhali
 */
public interface I18nStore {
    /**
     *
     * @return {@link I18nProject}
     */
    @NotNull I18nProject getSnapshot();

    /**
     * Manipulates the underlying store state.
     * This is the only function which can be used to alter the tracked translation state.
     * @param mutateFn Mutation function
     */
    void mutate(@NotNull Consumer<@NotNull MutableI18nProject> mutateFn);

    /**
     * Holds the snapshot state until the consumer is released.
     * Thus, the store state cannot be manipulated during the consumer is active.
     * @param holdFn Hold function
     */
    void holdSnapshot(@NotNull Consumer<@NotNull I18nProject> holdFn);
}
