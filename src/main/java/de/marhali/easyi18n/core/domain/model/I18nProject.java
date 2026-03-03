package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable i18n project.
 *
 * @param modules Project modules
 *
 * @see MutableI18nProject
 * @author marhali
 */
public record I18nProject(
    @NotNull Map<@NotNull ModuleId, @NotNull I18nModule> modules
    ) {

    /**
     * Note: The underlying {@link Map} implementation is not implementation aware - but this should be no problem for an empty snapshot
     * @return empty project
     */
    public static @NotNull I18nProject empty() {
        return new I18nProject(Map.of());
    }

    /**
     * Checks whether the specified module exists or not.
     * @param moduleId Module identifier
     * @return {@code true} if module is existing, otherwise {@code false}
     */
    public boolean hasModule(@NotNull ModuleId moduleId) {
        return modules.containsKey(moduleId);
    }

    /**
     * Retrieves the specified module.
     * @param moduleId Module identifier
     * @return Nullable {@link I18nModule}
     */
    public @Nullable I18nModule getModule(@NotNull ModuleId moduleId) {
        return hasModule(moduleId) ? modules.get(moduleId) : null;
    }

    /**
     * Retrieves a list of currently loaded module identifiers.
     * @return Set of {@link ModuleId}'s
     */
    public @NotNull Set<@NotNull ModuleId> getModuleIds() {
        return modules.keySet();
    }

    /**
     * Retrieves the specified module.
     * @param moduleId Module identifier
     * @return {@link I18nModule} or throws {@link NullPointerException} if unknown
     */
    public @NotNull I18nModule getModuleOrThrow(@NotNull ModuleId moduleId) {
        return Objects.requireNonNull(getModule(moduleId), "Project does not contain module with: " + moduleId);
    }
}
