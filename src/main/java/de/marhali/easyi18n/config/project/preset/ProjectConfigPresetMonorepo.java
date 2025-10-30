package de.marhali.easyi18n.config.project.preset;

import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.ProjectConfigModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Preset that demonstrates usage in a monorepo project with frontend and backend.
 *
 * @author marhali
 */
public class ProjectConfigPresetMonorepo implements ProjectConfigPresetProvider {
    @Override
    public ProjectConfig applyPreset(ProjectConfig previousState) {
        var state = new ProjectConfig(previousState);

        var frontendModule = ProjectConfigModule.fromDefaultPreset();
        frontendModule.setName("frontend");

        var backendModule = ProjectConfigModule.fromDefaultPreset();
        backendModule.setName("backend");

        state.setModules(new ArrayList<>(List.of(frontendModule, backendModule)));

        return state;
    }
}
