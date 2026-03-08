package de.marhali.easyi18n.core.domain.config.preset;

import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default preset for {@link ProjectConfig}.
 *
 * @author marhali
 */
public class ProjectConfigPresetDefault implements PresetProvider<ProjectConfig> {
    @Override
    public @NotNull ProjectConfig applyPreset(@Nullable ProjectConfig previousState) {
        return ProjectConfig.builder()
            // Common
            .sorting(true)
            .previewLocale(new LocaleId("en"))
            // Modules
            .modules()
            .module(_builder -> ProjectConfigModule.fromDefaultPreset())
            .build();
    }
}
