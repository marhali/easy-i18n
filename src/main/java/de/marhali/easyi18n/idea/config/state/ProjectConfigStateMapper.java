package de.marhali.easyi18n.idea.config.state;

import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.service.LocaleIdFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Mapper between {@link ProjectConfig} and {@link ProjectConfigState}.
 *
 * @author marhali
 */
public final class ProjectConfigStateMapper {
    public static @NotNull ProjectConfig toDomain(@NotNull ProjectConfigState state) {
        var modules = new HashMap<ModuleId, ProjectConfigModule>(state.modules.size());

        for (ProjectConfigModuleState moduleState : state.modules.values()) {
            var moduleDomain = ProjectConfigModuleStateMapper.toDomain(moduleState);
            modules.put(moduleDomain.id(), moduleDomain);
        }

        return new ProjectConfig(
            state.keyComment,
            state.sorting,
            LocaleIdFactory.fromInput(state.previewLocale),
            modules
        );
    }

    public static @NotNull ProjectConfigState fromDomain(@NotNull ProjectConfig domain) {
        var state = new ProjectConfigState();

        state.keyComment = domain.keyComment();
        state.sorting = domain.sorting();
        state.previewLocale = domain.previewLocale().tag();
        state.modules = new HashMap<>(domain.modules().size());

        for (ProjectConfigModule moduleDomain : domain.modules().values()) {
            var moduleState = ProjectConfigModuleStateMapper.fromDomain(moduleDomain);
            state.modules.put(moduleState.id, moduleState);
        }

        return state;
    }
}
