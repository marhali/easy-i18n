package de.marhali.easyi18n.config.project.preset;

import de.marhali.easyi18n.config.project.ProjectConfig;

/**
 * Custom preset that does nothing besides indicating that a custom configuration is active.
 *
 * @author marhali
 */
public class ProjectConfigPresetCustom implements ProjectConfigPresetProvider {
    @Override
    public ProjectConfig applyPreset(ProjectConfig previousState) {
        return new ProjectConfig(previousState);
    }
}
