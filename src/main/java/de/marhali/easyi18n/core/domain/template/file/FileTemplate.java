package de.marhali.easyi18n.core.domain.template.file;

import de.marhali.easyi18n.core.domain.model.I18nBuiltinParam;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Translation file content template.
 *
 * @author marhali
 */
public interface FileTemplate {
    /**
     * Retrieves {@link LevelledFileTemplate} at the specified level (hierarchy).
     * If the requested level is higher than the defined template levels, the last level will be used.
     * @param level Level to retrieve
     * @return {@link LevelledFileTemplate}
     */
    @NotNull LevelledFileTemplate getAtLevel(@NotNull Integer level);

    /**
     * Retrieves all file template levels.
     * @return List of {@link LevelledFileTemplate}
     */
    @NotNull List<LevelledFileTemplate> getLevels();

    /**
     * Checks whether the given parameter name is needed at any file template level or not.
     * @param parameterName Parameter name
     * @return {@code true} if parameter name is needed at any level, otherwise {@code false}
     */
    boolean needsParameter(@NotNull String parameterName);

    /**
     * @see #needsParameter(String) 
     */
    default boolean needsParameter(@NotNull I18nBuiltinParam parameter) {
        return needsParameter(parameter.getParameterName());
    }
}
