package de.marhali.easyi18n.core.domain.template.file;

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
}
