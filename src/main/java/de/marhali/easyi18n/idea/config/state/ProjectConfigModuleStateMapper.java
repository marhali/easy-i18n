package de.marhali.easyi18n.idea.config.state;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.idea.service.ModuleIdFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Mapper between {@link ProjectConfigModule} and {@link ProjectConfigModuleState}.
 *
 * @author marhali
 */
public final class ProjectConfigModuleStateMapper {
    public static @NotNull ProjectConfigModule toDomain(@NotNull ProjectConfigModuleState state) {
        return new ProjectConfigModule(
            ModuleIdFactory.fromInput(state.id),
            state.pathTemplate,
            state.fileCodec,
            state.fileTemplate,
            state.keyTemplate,
            state.rootDirectory,
            state.defaultNamespace,
            state.i18nTemplate,
            state.keyNamingConvention
        );
    }

    public static @NotNull ProjectConfigModuleState fromDomain(@NotNull ProjectConfigModule domain) {
        var state = new ProjectConfigModuleState();

        state.id = domain.id().name();
        state.pathTemplate = domain.pathTemplate();
        state.fileCodec = domain.fileCodec();
        state.fileTemplate = domain.fileTemplate();
        state.keyTemplate = domain.keyTemplate();
        state.rootDirectory = domain.rootDirectory();
        state.defaultNamespace = domain.defaultNamespace();
        state.i18nTemplate = domain.i18nTemplate();
        state.keyNamingConvention = domain.keyNamingConvention();

        return state;
    }
}
