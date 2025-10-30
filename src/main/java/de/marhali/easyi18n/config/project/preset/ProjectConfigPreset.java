package de.marhali.easyi18n.config.project.preset;

import de.marhali.easyi18n.config.project.ProjectConfig;

import java.util.Arrays;

/**
 * Enumeration of all available configuration templates.
 * Every preset needs to be registered here to be properly recognized.
 *
 * @author marhali
 */
public enum ProjectConfigPreset {
    DEFAULT("Default", ProjectConfigPresetDefault.class),
    CUSTOM("Custom", ProjectConfigPresetCustom.class),
    MONOREPO("Monorepo", ProjectConfigPresetMonorepo.class),
    ;

    public static String[] displayNames() {
        return Arrays.stream(values())
            .map(ProjectConfigPreset::getDisplayName)
            .toArray(String[]::new);
    }

    public static ProjectConfigPreset fromDisplayName(String displayName) {
        return Arrays.stream(values())
            .filter(preset -> preset.displayName.equals(displayName))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException("Cannot find preset with displayName: " + displayName)
            );
    }

    private final String displayName;
    private final Class<? extends ProjectConfigPresetProvider> presetProvider;

    ProjectConfigPreset(String displayName, Class<? extends ProjectConfigPresetProvider> presetProvider) {
        this.displayName = displayName;
        this.presetProvider = presetProvider;
    }

    public ProjectConfig applyPreset(ProjectConfig previousState) {
        try {
            return this.presetProvider.getDeclaredConstructor().newInstance().applyPreset(previousState);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }
}
