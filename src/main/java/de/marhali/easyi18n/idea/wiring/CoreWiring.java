package de.marhali.easyi18n.idea.wiring;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.application.I18nApplication;
import de.marhali.easyi18n.core.application.command.*;
import de.marhali.easyi18n.core.application.command.handler.*;
import de.marhali.easyi18n.core.application.cqrs.CommandDispatcher;
import de.marhali.easyi18n.core.application.cqrs.QueryDispatcher;
import de.marhali.easyi18n.core.application.query.*;
import de.marhali.easyi18n.core.application.query.handler.*;
import de.marhali.easyi18n.core.application.service.*;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.config.FileCodec;
import de.marhali.easyi18n.core.domain.model.ImplementationProvider;
import de.marhali.easyi18n.core.ports.*;
import de.marhali.easyi18n.idea.config.ProjectConfigAdapter;
import de.marhali.easyi18n.idea.event.DomainEventPublisherAdapter;
import de.marhali.easyi18n.idea.vfs.FileSystemAdapter;
import de.marhali.easyi18n.idea.vfs.FileSystemListener;
import de.marhali.easyi18n.idea.vfs.PathResolverAdapter;
import de.marhali.easyi18n.infra.InMemoryFileProcessorRegistry;
import de.marhali.easyi18n.infra.json.JsonFileProcessor;
import de.marhali.easyi18n.infra.json5.Json5FileProcessor;
import de.marhali.easyi18n.infra.properties.PropertiesFileProcessor;
import de.marhali.easyi18n.infra.yaml.YamlFileProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Cheap way of dependency injection to build a project-specific {@link I18nApplication}.
 *
 * @author marhali
 */
public final class CoreWiring {

    private CoreWiring() {}

    public static @NotNull I18nApplication create(@NotNull Project project, @NotNull Disposable parentDisposable) {
        // Adapters
        ProjectConfigPort projectConfigPort = new ProjectConfigAdapter(project);
        DomainEventPublisherPort domainEventPublisherPort = new DomainEventPublisherAdapter(project);
        PathResolverPort pathResolverPort = new PathResolverAdapter(project);
        FileSystemPort fileSystemPort = new FileSystemAdapter(project);
        FileProcessorRegistryPort fileProcessorRegistryPort = new InMemoryFileProcessorRegistry(
            Map.of(
                FileCodec.JSON, () -> new JsonFileProcessor(fileSystemPort),
                FileCodec.JSON5, () -> new Json5FileProcessor(fileSystemPort),
                FileCodec.YAML, () -> new YamlFileProcessor(fileSystemPort),
                FileCodec.PROPERTIES, () -> new PropertiesFileProcessor(fileSystemPort)
            )
        );

        // State
        ImplementationProvider implementationProvider = new SortableImplementationProvider(projectConfigPort);
        I18nStore store = new InMemoryI18nStore(implementationProvider);

        // Services
        CachedModuleTemplates cachedModuleTemplates = new CachedModuleTemplates(projectConfigPort);
        CachedModuleRules cachedModuleRules = new CachedModuleRules(projectConfigPort);
        I18nPathDetector i18nPathDetector = new I18nPathDetector(store, cachedModuleTemplates);
        TrackedI18nPathsService trackedI18nPathsService = new TrackedI18nPathsService();
        ModuleLoader moduleLoader = new DefaultModuleLoader(cachedModuleTemplates, pathResolverPort, fileProcessorRegistryPort, trackedI18nPathsService);
        ModulePersistor modulePersistor = new DefaultModulePersistor(cachedModuleTemplates, fileProcessorRegistryPort, trackedI18nPathsService, fileSystemPort);
        EnsureLoadedService ensureLoadedService = new EnsureLoadedService(store, projectConfigPort, moduleLoader);
        EnsurePersistService ensurePersistService = new EnsurePersistService(store, projectConfigPort, modulePersistor);
        ModuleViewProjector moduleViewProjector = new ModuleViewProjector(cachedModuleTemplates);
        EditorElementModuleResolver editorElementModuleResolver = new EditorElementModuleResolver(projectConfigPort);
        I18nKeyCandidateResolver keyResolver = new I18nKeyCandidateResolver(projectConfigPort, store);
        new FileSystemListener(project, parentDisposable, i18nPathDetector);

        // Commands
        var commands = new CommandDispatcher();
        commands.register(ReloadCommand.class, new ReloadCommandHandler(store, domainEventPublisherPort));
        commands.register(InvalidateProjectConfigCommand.class, new InvalidateProjectConfigCommandHandler(store, cachedModuleTemplates, cachedModuleRules, domainEventPublisherPort));
        commands.register(EnsureModuleLoadedCommand.class, new EnsureModuleLoadedCommandHandler(ensureLoadedService));
        commands.register(AddI18nRecordCommand.class, new AddI18nRecordCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(UpdateI18nRecordCommand.class, new UpdateI18nRecordCommandHandler(implementationProvider, ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(RemoveI18nRecordCommand.class, new RemoveI18nRecordCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(RemoveI18nRecordsCommand.class, new RemoveI18nRecordsCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(RemoveI18nValueCommand.class, new RemoveI18nValueCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(UpdateI18nKeyCommand.class, new UpdateI18nKeyCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(UpdatePartialI18nKeyCommand.class, new UpdatePartialI18nKeyCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(UpdateI18nValueCommand.class, new UpdateI18nValueCommandHandler(ensureLoadedService, ensurePersistService, store, domainEventPublisherPort));
        commands.register(ModuleI18nPathsChangedCommand.class, new ModuleI18nPathsChangedCommandHandler(store, domainEventPublisherPort));

        // Queries
        var queries = new QueryDispatcher();
        queries.register(ConfiguredModulesQuery.class, new ConfiguredModulesQueryHandler(projectConfigPort));
        queries.register(ModuleLocalesQuery.class, new ModuleLocalesQueryHandler(ensureLoadedService, store));
        queries.register(TranslationByKeyQuery.class, new TranslationByKeyQueryHandler(ensureLoadedService, store));
        queries.register(ModuleViewQuery.class, new ModuleViewQueryHandler(ensureLoadedService, store, moduleViewProjector));
        queries.register(EditorElementModuleQuery.class, new EditorElementModuleQueryHandler(editorElementModuleResolver));
        queries.register(EditorElementI18nEntryPreviewQuery.class, new EditorElementI18nEntryPreviewQueryHandler(store, cachedModuleRules, keyResolver, projectConfigPort));
        queries.register(EditorElementSuggestionsQuery.class, new EditorElementSuggestionsQueryHandler(store, cachedModuleRules, projectConfigPort));

        return new I18nApplication(commands, queries);
    }
}
