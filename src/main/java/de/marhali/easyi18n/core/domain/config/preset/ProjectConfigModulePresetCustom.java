package de.marhali.easyi18n.core.domain.config.preset;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Dummy preset that does nothing besides indicating that a custom configuration is active.
 *
 * @author marhali
 */
public class ProjectConfigModulePresetCustom implements PresetProvider<ProjectConfigModule> {
    @Override
    public @NotNull ProjectConfigModule applyPreset(@Nullable ProjectConfigModule previousState) {
        if (previousState == null) {
            return ProjectConfigModule.fromDefaultPreset();
        } else {
            return previousState.toBuilder().build();
        }
    }
}
