package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.ModuleI18nPath;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Service to identify relevant translation files.
 *
 * @author marhali
 */
public class I18nPathDetector {

    private final @NotNull I18nStore store;
    private final @NotNull CachedModuleTemplates cachedModuleTemplates;

    public I18nPathDetector(@NotNull I18nStore store, @NotNull CachedModuleTemplates cachedModuleTemplates) {
        this.store = store;
        this.cachedModuleTemplates = cachedModuleTemplates;
    }

    /**
     * Checks if the given path is an actual translation file path
     * @param path Path to check
     * @return {@link ModuleI18nPath} or {@code null} if the provided path does not belong to any translation file
     */
    public @Nullable ModuleI18nPath detectModuleI18nPath(@NotNull String path) {
        var moduleIds = store.getSnapshot().getModuleIds();

        for (ModuleId moduleId : moduleIds) {
            var modulePathTemplate = cachedModuleTemplates.resolve(moduleId).path();

            if (path.startsWith(modulePathTemplate.getMostCommonParentPath())) {
                var pathParams = modulePathTemplate.matchCanonical(path);

                if (pathParams != null) {
                    // Matched guardable path (actual relevant translation file)
                    // Currently under the principle: first come, first served (module view)
                    return new ModuleI18nPath(moduleId, new I18nPath(path, pathParams));
                }
            }
        }

        return null;
    }
}
