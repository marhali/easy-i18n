package de.marhali.easyi18n.core.domain.config.preset;

import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Preset that demonstrates usage in a monorepo project with frontend and backend.
 *
 * @author marhali
 */
public class ProjectConfigPresetMonorepo implements PresetProvider<ProjectConfig> {
    @Override
    public @NotNull ProjectConfig applyPreset(@Nullable ProjectConfig previousState) {
        var builder = previousState == null ? ProjectConfig.fromDefaultPreset().toBuilder() : previousState.toBuilder();

        var frontendModule = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(new ModuleId("frontend"))
            .build();

        var backendModule = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(new ModuleId("backend"))
            .build();

        return builder
            .modules()
            .module(moduleBuilder -> frontendModule)
            .module(moduleBuilder -> backendModule)
            .build();
    }
}
