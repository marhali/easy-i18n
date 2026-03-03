package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mutable i18n project.
 *
 * @author marhali
 */
public final class MutableI18nProject {

    public static @NotNull MutableI18nProject empty(@NotNull ImplementationProvider implementationProvider) {
        return new MutableI18nProject(implementationProvider, implementationProvider.getMap());
    }

    /**
     * Implementation provider to construct {@link Map} instances.
     */
    private final @NotNull ImplementationProvider implementationProvider;

    /**
     * Map including every managed module with their translations.
     */
    private final @NotNull Map<@NotNull ModuleId, @NotNull MutableI18nModule> modules;

    public MutableI18nProject(
        @NotNull ImplementationProvider implementationProvider,
        @NotNull Map<@NotNull ModuleId, @NotNull MutableI18nModule> modules
    ) {
        this.implementationProvider = implementationProvider;
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
     * Retrieves a specific i18n module by module identifier.
     * @param moduleId Module identifier
     * @return {@link MutableI18nModule} or {@code null} if the desired module does not exist
     */
    public @Nullable MutableI18nModule getModule(@NotNull ModuleId moduleId) {
        return this.modules.get(moduleId);
    }

    /**
     * Retrieves a specific i18n module by module identifier.
     * Throws a {@link NullPointerException} if the module does not exist.
     * @param moduleId Module identifier
     * @return {@link MutableI18nModule}
     */
    public @NotNull MutableI18nModule getModuleOrThrow(@NotNull ModuleId moduleId) {
        return Objects.requireNonNull(getModule(moduleId),
            "Project does not contain module with id: " + moduleId);
    }

    /**
     * Retrieves the requested module or if not existing creates it properly.
     * @param moduleId Module identifier
     * @return {@link MutableI18nModule}
     */
    public @NotNull MutableI18nModule getOrCreateModule(@NotNull ModuleId moduleId) {
        return this.modules.computeIfAbsent(moduleId,
            (_moduleId) -> MutableI18nModule.empty(implementationProvider));
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
    public void clearAll() {
        this.modules.clear();
    }

    /**
     * Clears a specific module.
     * @param moduleId Module identifier
     */
    public void clearModule(@NotNull ModuleId moduleId) {
        this.modules.remove(moduleId);
    }

    /**
     * Transforms this project to its immutable representation.
     * @return Immutable {@link I18nProject}
     */
    public @NotNull I18nProject toSnapshot() {
        Map<ModuleId, I18nModule> snapshotModules = this.implementationProvider.getMap();

        for (Map.Entry<ModuleId, MutableI18nModule> moduleEntry : this.modules.entrySet()) {
            snapshotModules.put(
                moduleEntry.getKey(),
                moduleEntry.getValue().toSnapshot()
            );
        }

        return new I18nProject(snapshotModules);
    }

    @Override
    public String toString() {
        return "MutableI18nProject{" +
            "implementationProvider=" + implementationProvider +
            ", modules=" + modules +
            '}';
    }
}
