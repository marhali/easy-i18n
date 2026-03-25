package de.marhali.easyi18n.core.domain.config.preset;

import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Dummy preset that does nothing besides indicating that a custom configuration is active.
 *
 * @author marhali
 */
public class ProjectConfigPresetCustom implements PresetProvider<ProjectConfig> {
    @Override
    public @NotNull ProjectConfig applyPreset(@Nullable ProjectConfig previousState) {
        if (previousState == null) {
            return ProjectConfig.fromDefaultPreset();
        } else {
            return previousState.toBuilder().build();
        }
    }
}
