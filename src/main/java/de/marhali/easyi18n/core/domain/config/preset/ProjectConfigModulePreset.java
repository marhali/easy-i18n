package de.marhali.easyi18n.core.domain.config.preset;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.config.preset.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enumeration of all available module configuration presets.
 * Every preset needs to be registered here to be properly recognized.
 *
 * @author marhali
 */
public enum ProjectConfigModulePreset {
    DEFAULT(ProjectConfigModulePresetDefault.class),
    CUSTOM(ProjectConfigModulePresetCustom.class),
    // Custom framework specific implementations
    ANGULAR_NGX_TRANSLATE(AngularNgxTranslateModulePreset.class),
    RAILS(RailsModulePreset.class),
    VUE_I18N(VueI18nModulePreset.class),
    REACT_I18NEXT(ReactI18nextModulePreset.class),
    SPRING_BOOT(SpringBootModulePreset.class),
    LARAVEL(LaravelModulePreset.class),
    ;

    public static @NotNull ProjectConfigModulePreset determineFromState(@NotNull ProjectConfigModule state) {
        if (state.equals(DEFAULT.applyPreset(state))) {
            return DEFAULT;
        }

        // We could check all other cases here, but the benefits for this would not make much impact

        return CUSTOM;
    }

    private final @NotNull Class<? extends PresetProvider<ProjectConfigModule>> presetProvider;

    ProjectConfigModulePreset(@NotNull Class<? extends PresetProvider<ProjectConfigModule>> presetProvider) {
        this.presetProvider = presetProvider;
    }

    public @NotNull ProjectConfigModule applyPreset(@Nullable ProjectConfigModule previousState) {
        try {
            return presetProvider.getDeclaredConstructor().newInstance().applyPreset(previousState);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
