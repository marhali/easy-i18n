package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mutable i18n project.
 *
 * @author marhali
 */
public final class MutableI18nProject {

    public static @NotNull MutableI18nProject empty() {
        return new MutableI18nProject(new HashMap<>());
    }

    /**
     * Map including every managed module with their translations.
     */
    private final @NotNull Map<@NotNull ModuleId, @NotNull MutableI18nModule> modules;

    public MutableI18nProject(
        @NotNull Map<@NotNull ModuleId, @NotNull MutableI18nModule> modules
    ) {
        this.modules = modules;
    }

    /**
     * Checks if a specific i18n module exists.
     * @param moduleId Module identifier
     * @return {@code true} if module exists, otherwise {@code false}
     */
    public boolean hasModule(@NotNull ModuleId moduleId) {
        return this.modules.containsKey(moduleId);
    }

    /**
     * Retrieves a specific i18n module by name.
     * @see #hasModule(ModuleId)
     * @param moduleId Module identifier
     * @return {@link MutableI18nModule} or {@link NoSuchElementException} if the requested module is unknown
     */
    public @NotNull MutableI18nModule getModule(@NotNull ModuleId moduleId) {
        if (!hasModule(moduleId)) {
            throw new NoSuchElementException("Module by id " + moduleId + " does not exist");
        }

        return this.modules.get(moduleId);
    }

    /**
     * Retrieves the requested module or if not existing creates it properly.
     * @param moduleId Module identifier
     * @return {@link MutableI18nModule}
     */
    public @NotNull MutableI18nModule getOrCreateModule(@NotNull ModuleId moduleId) {
        return this.modules.computeIfAbsent(moduleId, (_moduleId) -> MutableI18nModule.empty());
    }

    /**
     * @return Modules keyset
     */
    public @NotNull Set<@NotNull ModuleId> getAllModuleIds() {
        return this.modules.keySet();
    }

    /**
     * @return Compound set of all locales used in every module
     */
    public @NotNull Set<@NotNull LocaleId> getAllLocaleIds() {
        return this.modules.values().stream()
            .flatMap((module) -> module.getLocales().stream())
            .collect(Collectors.toSet());
    }

    /**
     * Clears all modules.
     */
    public void clear() {
        this.modules.clear();
    }

    /**
     * Transforms this project to its immutable representation.
     * @return Immutable {@link I18nProject}
     */
    public @NotNull I18nProject toSnapshot() {
        Map<@NotNull ModuleId, @NotNull I18nModule> modulesCopy = modules.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toSnapshot(),
                (contentA, contentB) -> contentA
            ));

        return new I18nProject(modulesCopy);
    }
}
