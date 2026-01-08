package de.marhali.easyi18n.core.domain.config;

import de.marhali.easyi18n.config.project.KeyNamingConvention;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.next_io.file.FileCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Project-specific configuration options.
 *
 * @param keyComment Indicates whether translation key can be commented or not.
 * @param editorAssistance Indicates whether editor code assistance should be enabled or not.
 * @param sorting Indicates whether translation keys should be sorted alphabetically.
 * @param previewLocale Defines the locale to be used for development and preview.
 * @param modules Configured modules.
 *
 * @author marhali
 */
public record ProjectConfig(
    @NotNull Boolean keyComment,
    @NotNull Boolean editorAssistance,
    @NotNull Boolean sorting,
    @NotNull LocaleId previewLocale,
    @NotNull Map<@NotNull ModuleId, @NotNull ModuleDefinition> modules
    ) {

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
    public record ModuleDefinition(
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
    }
}
