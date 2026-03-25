package de.marhali.easyi18n.core.domain.config;

import de.marhali.easyi18n.core.domain.config.preset.ProjectConfigModulePresetDefault;
import de.marhali.easyi18n.core.domain.model.I18nKeyPrefix;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorRule;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * Module-specific configuration options within a project.
 *
 * @param id Module identifier.
 * @param pathTemplate File path template syntax.
 * @param fileCodec File format to use for reading / writing.
 * @param fileTemplate File content template syntax.
 * @param keyTemplate Key template syntax.
 * @param rootDirectory Root directory from which this module configuration applies.
 * @param defaultKeyPrefixes Set of default translation key prefixes.
 * @param editorFlavorTemplate Template to apply for translation message extraction.
 * @param editorRules Editor assistance rules
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
    @NotNull Set<@NotNull I18nKeyPrefix> defaultKeyPrefixes,
    @NotNull String editorFlavorTemplate,
    @NotNull List<@NotNull EditorRule> editorRules
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
