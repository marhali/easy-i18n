package de.marhali.easyi18n.idea.config.state;

import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.idea.service.LocaleIdFactory;
import de.marhali.easyi18n.idea.service.ModuleIdFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper between {@link ProjectConfig} and {@link ProjectConfigState}.
 *
 * @author marhali
 */
public final class ProjectConfigStateMapper {

    private static final @NotNull ProjectConfig DEFAULTS = ProjectConfig.fromDefaultPreset();

    public static @NotNull ProjectConfig toDomain(@NotNull ProjectConfigState state) {
        return new ProjectConfig(
            Optional.ofNullable(state.keyComment).orElse(DEFAULTS.keyComment()),
            Optional.ofNullable(state.sorting).orElse(DEFAULTS.sorting()),
            state.previewLocale != null ? LocaleIdFactory.fromInput(state.previewLocale) : DEFAULTS.previewLocale(),
            state.modules != null
                ? state.modules.entrySet().stream().collect(Collectors.toMap(
                    (entry) -> ModuleIdFactory.fromInput(entry.getKey()),
                    (entry) -> ProjectConfigModuleStateMapper.toDomain(entry.getValue())))
                : DEFAULTS.modules()
        );
    }

    public static @NotNull ProjectConfigState fromDomain(@NotNull ProjectConfig domain) {
        return new ProjectConfigState(
            domain.keyComment(),
            domain.sorting(),
            domain.previewLocale().tag(),
            domain.modules().entrySet().stream().collect(Collectors.toMap(
                (entry) -> entry.getKey().name(),
                (entry) -> ProjectConfigModuleStateMapper.fromDomain(entry.getValue())
            ))
        );
    }
}
