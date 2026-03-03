package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Service to track processed translation files.
 * Used to determine if translation files became obsolete ofter a command (write action).
 *
 * @author marhali
 */
public class TrackedI18nPathsService {

    private final @NotNull Map<@NotNull ModuleId, @NotNull Set<@NotNull I18nPath>> modulePaths;

    public TrackedI18nPathsService() {
        this.modulePaths = new HashMap<>();
    }

    /**
     * Invalidates all tracked paths for the specified module.
     * @param moduleId Module identifier
     */
    public void invalidateModule(@NotNull ModuleId moduleId) {
        this.modulePaths.remove(moduleId);
    }

    /**
     * Overrides tracked paths for the specified module.
     * @param moduleId Module identifier
     * @param paths Paths to remember
     */
    public void put(@NotNull ModuleId moduleId, @NotNull Set<@NotNull I18nPath> paths) {
        modulePaths.put(moduleId, paths);
    }

    /**
     * Retrieves the tracked translation file paths for the given module.
     * @param moduleId Module identifier
     * @return Set of {@link I18nPath}'s
     */
    public @NotNull Set<@NotNull I18nPath> getTrackedPathsForModule(@NotNull ModuleId moduleId) {
        return modulePaths.getOrDefault(moduleId, Set.of());
    }
}
