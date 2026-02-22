package de.marhali.easyi18n.idea.wiring;

import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.application.I18nApplication;
import de.marhali.easyi18n.core.application.command.*;
import de.marhali.easyi18n.core.application.command.handler.*;
import de.marhali.easyi18n.core.application.cqrs.CommandDispatcher;
import de.marhali.easyi18n.core.application.cqrs.QueryDispatcher;
import de.marhali.easyi18n.core.application.query.ConfiguredModulesQuery;
import de.marhali.easyi18n.core.application.query.ModuleLocalesQuery;
import de.marhali.easyi18n.core.application.query.ModuleViewQuery;
import de.marhali.easyi18n.core.application.query.TranslationByKeyQuery;
import de.marhali.easyi18n.core.application.query.handler.ConfiguredModulesQueryHandler;
import de.marhali.easyi18n.core.application.query.handler.ModuleLocalesQueryHandler;
import de.marhali.easyi18n.core.application.query.handler.ModuleViewQueryHandler;
import de.marhali.easyi18n.core.application.query.handler.TranslationByKeyQueryHandler;
import de.marhali.easyi18n.core.application.service.*;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.config.FileCodec;
import de.marhali.easyi18n.core.domain.model.MapImplProvider;
import de.marhali.easyi18n.core.ports.*;
import de.marhali.easyi18n.idea.config.ProjectConfigAdapter;
import de.marhali.easyi18n.idea.event.DomainEventPublisherAdapter;
import de.marhali.easyi18n.idea.vfs.FileSystemAdapter;
import de.marhali.easyi18n.idea.vfs.PathResolverAdapter;
import de.marhali.easyi18n.infra.InMemoryFileProcessorRegistry;
import de.marhali.easyi18n.infra.json.JsonFileProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Cheap way of dependency injection to build a project-specific {@link I18nApplication}.
 *
 * @author marhali
 */
public final class CoreWiring {

    private CoreWiring() {}

    public static @NotNull I18nApplication create(@NotNull Project project) {
        // Adapters
        ProjectConfigPort projectConfigPort = new ProjectConfigAdapter(project);
        DomainEventPublisherPort domainEventPublisherPort = new DomainEventPublisherAdapter(project);
        PathResolverPort pathResolverPort = new PathResolverAdapter(project);
        FileSystemPort fileSystemPort = new FileSystemAdapter(project);
        FileProcessorRegistryPort fileProcessorRegistryPort = new InMemoryFileProcessorRegistry(
            Map.of(FileCodec.JSON, () -> new JsonFileProcessor(fileSystemPort))
        );

        // State
        MapImplProvider mapImplProvider = new SortableMapImplProvider(projectConfigPort);
        I18nStore store = new InMemoryI18nStore();

        // Services
        CachedModuleTemplates cachedModuleTemplates = new CachedModuleTemplates(projectConfigPort);
        ModuleLoader moduleLoader = new DefaultModuleLoader(cachedModuleTemplates, pathResolverPort, fileProcessorRegistryPort);
        ModulePersistor modulePersistor = new DefaultModulePersistor(cachedModuleTemplates, fileProcessorRegistryPort);
        EnsureLoadedService ensureLoadedService = new EnsureLoadedService(store, projectConfigPort, moduleLoader);
        EnsurePersistService ensurePersistService = new EnsurePersistService(store, projectConfigPort, modulePersistor);
        ModuleViewProjector moduleViewProjector = new ModuleViewProjector(cachedModuleTemplates);

        // Commands
        var commands = new CommandDispatcher();
        commands.register(ReloadCommand.class, new ReloadCommandHandler(store, domainEventPublisherPort));
        commands.register(InvalidateProjectConfigCommand.class, new InvalidateProjectConfigCommandHandler(store, cachedModuleTemplates, domainEventPublisherPort));
        commands.register(AddI18nRecordCommand.class, new AddI18nRecordCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(UpdateI18nRecordCommand.class, new UpdateI18nRecordCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(RemoveI18nRecordCommand.class, new RemoveI18nRecordCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(RemoveI18nValueCommand.class, new RemoveI18nValueCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(UpdateI18nKeyCommand.class, new UpdateI18nKeyCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(UpdateI18nValueCommand.class, new UpdateI18nValueCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));

        // Queries
        var queries = new QueryDispatcher();
        queries.register(ConfiguredModulesQuery.class, new ConfiguredModulesQueryHandler(projectConfigPort));
        queries.register(ModuleLocalesQuery.class, new ModuleLocalesQueryHandler(ensureLoadedService, store));
        queries.register(TranslationByKeyQuery.class, new TranslationByKeyQueryHandler(ensureLoadedService, store));
        queries.register(ModuleViewQuery.class, new ModuleViewQueryHandler(ensureLoadedService, store, moduleViewProjector));

        return new I18nApplication(commands, queries);
    }
}
