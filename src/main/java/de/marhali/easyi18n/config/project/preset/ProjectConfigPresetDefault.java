package de.marhali.easyi18n.config.project.preset;

import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.ProjectConfigModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Default preset that is applied out of the box.
 *
 * @author marhali
 */
public class ProjectConfigPresetDefault implements ProjectConfigPresetProvider {
    @Override
    public ProjectConfig applyPreset(ProjectConfig unusedNullablePreviousState) {
        var cfg = new ProjectConfig();

        // Common
        cfg.setEditorAssistance(true);
        cfg.setSorting(true);
        cfg.setPreviewLocale("en");

        // Modules
        var defaultModule = ProjectConfigModule.fromDefaultPreset();
        cfg.setModules(new ArrayList<>(List.of(defaultModule)));

        return cfg;
    }
}
