package de.marhali.easyi18n.core.domain.config;

import de.marhali.easyi18n.core.domain.config.preset.ProjectConfigPresetDefault;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Project-specific configuration options.
 *
 * @param keyComment Indicates whether translation key can be commented or not.
 * @param sorting Indicates whether translation keys should be sorted alphabetically.
 * @param previewLocale Defines the locale to be used for development and preview.
 * @param modules Configured modules.
 *
 * @author marhali
 */
public record ProjectConfig(
    @NotNull Boolean keyComment,
    @NotNull Boolean sorting,
    @NotNull LocaleId previewLocale,
    @NotNull Map<@NotNull ModuleId, @NotNull ProjectConfigModule> modules
    ) {

    public static @NotNull ProjectConfig fromDefaultPreset() {
        return new ProjectConfigPresetDefault().applyPreset(null);
    }

    public static @NotNull ProjectConfigBuilder builder() {
        return new ProjectConfigBuilder();
    }

    public @NotNull ProjectConfigBuilder toBuilder() {
        return new ProjectConfigBuilder(this);
    }
}
