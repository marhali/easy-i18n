package de.marhali.easyi18n.core.domain.config.preset;

import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enumeration of all available configuration templates.
 * Every preset needs to be registered here to be properly recognized.
 *
 * @author marhali
 */
public enum ProjectConfigPreset {
    DEFAULT(ProjectConfigPresetDefault.class),
    CUSTOM(ProjectConfigPresetCustom.class),
    MONOREPO(ProjectConfigPresetMonorepo.class),
    ;

    public static @NotNull ProjectConfigPreset determineFromState(@NotNull ProjectConfig state) {
        if (state.equals(DEFAULT.applyPreset(state))) {
            return DEFAULT;
        } else if (state.equals(MONOREPO.applyPreset(state))) {
            return MONOREPO;
        }

        return CUSTOM;
    }

    private final Class<? extends PresetProvider<ProjectConfig>> presetProvider;

    ProjectConfigPreset(Class<? extends PresetProvider<ProjectConfig>> presetProvider) {
        this.presetProvider = presetProvider;
    }

    public @NotNull ProjectConfig applyPreset(@Nullable ProjectConfig previousState) {
        try {
            return presetProvider.getDeclaredConstructor().newInstance().applyPreset(previousState);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
