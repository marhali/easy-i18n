package de.marhali.easyi18n.core.domain.config;

import de.marhali.easyi18n.core.domain.config.preset.ProjectConfigModulePresetDefault;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Module-specific configuration options within a project.
 *
 * @param id Module identifier.
 * @param pathTemplate File path template syntax.
 * @param fileCodec File format to use for reading / writing.
 * @param fileTemplate File content template syntax.
 * @param keyTemplate Key template syntax.
 * @param rootDirectory Root directory from which this module configuration applies.
 * @param defaultNamespace Namespace to use as default if none is supplied. Can be an empty string to ignore this feature. @deprecated We do not want to force the user to label this functionality as namespace, the user should define a list of key(prefixes) that should be used
 * @param i18nTemplate Template to apply for translation message extraction.
 * @param keyNamingConvention Defines the used key naming convention.
 *
 * @author marhali
 */
public record ProjectConfigModule(
    @NotNull ModuleId id,
    @NotNull String pathTemplate,
    @NotNull FileCodec fileCodec,
    @NotNull String fileTemplate,
    @NotNull String keyTemplate,
    @NotNull String rootDirectory,
    @NotNull @Deprecated String defaultNamespace,
    @NotNull String i18nTemplate,
    @NotNull KeyNamingConvention keyNamingConvention
) {
    public static @NotNull ProjectConfigModule fromDefaultPreset() {
        return new ProjectConfigModulePresetDefault().applyPreset(null);
    }

    public static @NotNull ProjectConfigModuleBuilder builder() {
        return new ProjectConfigModuleBuilder();
    }

    public @NotNull ProjectConfigModuleBuilder toBuilder() {
        return new ProjectConfigModuleBuilder(this);
    }
}
