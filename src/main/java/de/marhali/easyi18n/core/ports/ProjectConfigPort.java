package de.marhali.easyi18n.core.ports;

import de.marhali.easyi18n.core.domain.config.ProjectConfig;
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
     * @return {@link ProjectConfig}
     */
    @NotNull ProjectConfig read();
}
