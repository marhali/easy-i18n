package de.marhali.easyi18n.config.project.preset;

import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.next_io.file.FileParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // File ext mapper
        var fileExtMapper = Map.of(
            FileParser.JSON, List.of("json"),
            FileParser.JSON5, List.of("json5"),
            FileParser.YAML, List.of("yaml", "yml"),
            FileParser.PROPERTIES, List.of("properties")
        );

        cfg.setFileExtMapper(new HashMap<>(fileExtMapper));

        return cfg;
    }
}
