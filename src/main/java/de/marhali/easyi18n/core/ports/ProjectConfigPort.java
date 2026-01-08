package de.marhali.easyi18n.core.ports;

import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.model.ProjectId;
import org.jetbrains.annotations.NotNull;

/**
 * Port for reading {@link ProjectConfig}
 *
 * @author marhali
 */
public interface ProjectConfigPort {
    /**
     * Reads the project configuration for a specific project
     *
     * @param projectId Project identifier
     * @return {@link ProjectConfig}
     */
    @NotNull ProjectConfig read(@NotNull ProjectId projectId);
}
