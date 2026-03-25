package de.marhali.easyi18n.idea.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.idea.config.state.ProjectConfigState;
import de.marhali.easyi18n.idea.config.state.ProjectConfigStateMapper;
import org.jetbrains.annotations.NotNull;

/**
 * Persists project-specific {@link ProjectConfig} as {@link ProjectConfigState}.
 *
 * @author marhali
 */
@Service(Service.Level.PROJECT)
@State(
    name = "ProjectConfigService",
    storages = @Storage("easy-i18n.xml")
)
public final class ProjectConfigService implements PersistentStateComponent<ProjectConfigState> {

    /**
     * Domain configuration state derived from configuration state
     */
    private volatile @NotNull ProjectConfig domainState;

    /**
     * Persisted configuration state.
     */
    private @NotNull ProjectConfigState state;

    public ProjectConfigService() {
        this.domainState = ProjectConfig.fromDefaultPreset();
        this.state = ProjectConfigStateMapper.fromDomain(this.domainState);
    }

    public @NotNull ProjectConfig getDomainState() {
        return this.domainState;
    }

    public void loadDomainState(@NotNull ProjectConfig domainState) {
        this.domainState = domainState;
        this.state = ProjectConfigStateMapper.fromDomain(this.domainState);
    }

    @Override
    public @NotNull ProjectConfigState getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull ProjectConfigState state) {
        this.state = state;
        this.domainState = ProjectConfigStateMapper.toDomain(this.state);
    }
}
