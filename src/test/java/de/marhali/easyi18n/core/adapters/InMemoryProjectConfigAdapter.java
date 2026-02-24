package de.marhali.easyi18n.core.adapters;

import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class InMemoryProjectConfigAdapter implements ProjectConfigPort {

    private final @NotNull ProjectConfig projectConfig;

    public InMemoryProjectConfigAdapter(@NotNull ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    @Override
    public @NotNull ProjectConfig read() {
        return projectConfig;
    }
}
