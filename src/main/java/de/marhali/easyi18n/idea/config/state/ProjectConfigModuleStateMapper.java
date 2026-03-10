package de.marhali.easyi18n.idea.config.state;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nKeyPrefix;
import de.marhali.easyi18n.idea.service.ModuleIdFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper between {@link ProjectConfigModule} and {@link ProjectConfigModuleState}.
 *
 * @author marhali
 */
public final class ProjectConfigModuleStateMapper {

    private static final @NotNull ProjectConfigModule DEFAULTS = ProjectConfigModule.fromDefaultPreset();

    private ProjectConfigModuleStateMapper() {}

    public static @NotNull ProjectConfigModule toDomain(@NotNull ProjectConfigModuleState state) {
        return new ProjectConfigModule(
            state.id != null ? ModuleIdFactory.fromInput(state.id) : DEFAULTS.id(),
            Optional.ofNullable(state.pathTemplate).orElse(DEFAULTS.pathTemplate()),
            Optional.ofNullable(state.fileCodec).orElse(DEFAULTS.fileCodec()),
            Optional.ofNullable(state.fileTemplate).orElse(DEFAULTS.fileTemplate()),
            Optional.ofNullable(state.keyTemplate).orElse(DEFAULTS.keyTemplate()),
            Optional.ofNullable(state.rootDirectory).orElse(DEFAULTS.rootDirectory()),
            state.defaultKeyPrefixes != null
                ? state.defaultKeyPrefixes.stream().map(I18nKeyPrefix::of).collect(Collectors.toSet())
                : DEFAULTS.defaultKeyPrefixes(),
            Optional.ofNullable(state.i18nTemplate).orElse(DEFAULTS.i18nTemplate()),
            Optional.ofNullable(state.keyNamingConvention).orElse(DEFAULTS.keyNamingConvention()),
            state.editorRules != null
                ? state.editorRules.stream().map(EditorRuleStateMapper::toDomain).toList()
                : DEFAULTS.editorRules()
        );
    }

    public static @NotNull ProjectConfigModuleState fromDomain(@NotNull ProjectConfigModule domain) {
        return new ProjectConfigModuleState(
            domain.id().name(),
            domain.pathTemplate(),
            domain.fileCodec(),
            domain.fileTemplate(),
            domain.keyTemplate(),
            domain.rootDirectory(),
            domain.defaultKeyPrefixes().stream().map(I18nKeyPrefix::canonicalPrefix).collect(Collectors.toSet()),
            domain.i18nTemplate(),
            domain.keyNamingConvention(),
            domain.editorRules().stream().map(EditorRuleStateMapper::fromDomain).toList()
        );
    }
}
