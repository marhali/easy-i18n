package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Immutable i18n project.
 *
 * @param modules Project modules
 *
 * @author marhali
 */
public record I18nProject(
    @NotNull Map<@NotNull ModuleId, @NotNull I18nModule> modules
    ) {

    /**
     * @return empty project
     */
    public static @NotNull I18nProject empty() {
        return new I18nProject(new HashMap<>());
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
     * Retrieves the specified module or an empty module if the module is unknown.
     * @param moduleId Module identifier
     * @return {@link I18nModule} or an empty module if moduleId is not set
     */
    public @NotNull I18nModule getOrEmptyModule(@NotNull ModuleId moduleId) {
        return modules.containsKey(moduleId) ? modules.get(moduleId) : I18nModule.empty();
    }

    /**
     * Retrieves the specified module.
     * @param moduleId Module identifier
     * @return Nullable {@link I18nModule}
     */
    public @Nullable I18nModule getModule(@NotNull ModuleId moduleId) {
        return hasModule(moduleId) ? modules.get(moduleId) : null;
    }
}
