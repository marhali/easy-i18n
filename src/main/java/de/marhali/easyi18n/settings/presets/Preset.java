package de.marhali.easyi18n.settings.presets;

import de.marhali.easyi18n.settings.ProjectSettings;

/**
 * Enumeration of all available configuration presets.
 * Every preset needs to be registered here to be properly recognized.
 * @author marhali
 */
public enum Preset {
    DEFAULT(DefaultPreset.class),
    VUE_I18N(VueI18nPreset.class),
    REACT_I18NEXT(ReactI18NextPreset.class);

    private final Class<? extends ProjectSettings> clazz;

    Preset(Class<? extends ProjectSettings> clazz) {
        this.clazz = clazz;
    }

    public ProjectSettings config() {
        try {
            return this.clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }
}
