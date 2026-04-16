package de.marhali.easyi18n.integrationTests;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryFileSystemAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryPathResolverAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.I18nApplication;
import de.marhali.easyi18n.core.application.cqrs.CommandDispatcher;
import de.marhali.easyi18n.core.application.cqrs.QueryDispatcher;
import de.marhali.easyi18n.core.application.service.*;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.config.FileCodec;
import de.marhali.easyi18n.core.domain.model.ImplementationProvider;
import de.marhali.easyi18n.core.ports.FileProcessorRegistryPort;
import de.marhali.easyi18n.infra.InMemoryFileProcessorRegistry;
import de.marhali.easyi18n.infra.json.JsonFileProcessor;
import de.marhali.easyi18n.infra.json5.Json5FileProcessor;
import de.marhali.easyi18n.infra.properties.PropertiesFileProcessor;
import de.marhali.easyi18n.infra.yaml.YamlFileProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Test-only wiring that assembles {@link I18nApplication} using in-memory adapters
 * without any IntelliJ Platform dependencies.
 *
 * @author marhali
 */
public class IntegrationTestWiring {

    public final @NotNull InMemoryProjectConfigAdapter projectConfig;
    public final @NotNull InMemoryFileSystemAdapter fileSystem;
    public final @NotNull InMemoryPathResolverAdapter pathResolver;
    public final @NotNull CachedModuleTemplates cachedModuleTemplates;
    public final @NotNull InMemoryDomainEventPublisherAdapter eventPublisher;
    public final @NotNull I18nStore store;
    public final @NotNull DefaultEnsureLoadedService ensureLoadedService;
    public final @NotNull DefaultEnsurePersistService ensurePersistService;

    public final @NotNull CommandDispatcher commands;
    public final @NotNull QueryDispatcher queries;
    public final @NotNull I18nApplication application;

    public IntegrationTestWiring() {
        projectConfig = new InMemoryProjectConfigAdapter();
        fileSystem = new InMemoryFileSystemAdapter();
        pathResolver = new InMemoryPathResolverAdapter();
        eventPublisher = new InMemoryDomainEventPublisherAdapter();

        FileProcessorRegistryPort fileProcessorRegistry = new InMemoryFileProcessorRegistry(Map.of(
            FileCodec.JSON, () -> new JsonFileProcessor(fileSystem, projectConfig),
            FileCodec.JSON5, () -> new Json5FileProcessor(fileSystem, projectConfig),
            FileCodec.YAML, () -> new YamlFileProcessor(fileSystem, projectConfig),
            FileCodec.PROPERTIES, () -> new PropertiesFileProcessor(fileSystem, projectConfig)
        ));

        ImplementationProvider implementationProvider = new SortableImplementationProvider(projectConfig);
        store = new InMemoryI18nStore(implementationProvider);

        cachedModuleTemplates = new CachedModuleTemplates(projectConfig);
        TrackedI18nPathsService trackedI18nPaths = new TrackedI18nPathsService();

        ModuleLoader moduleLoader = new DefaultModuleLoader(
            cachedModuleTemplates, pathResolver, fileProcessorRegistry, trackedI18nPaths
        );
        ModulePersistor modulePersistor = new DefaultModulePersistor(
            cachedModuleTemplates, fileProcessorRegistry, trackedI18nPaths, fileSystem
        );

        ensureLoadedService = new DefaultEnsureLoadedService(store, projectConfig, moduleLoader);
        ensurePersistService = new DefaultEnsurePersistService(store, projectConfig, modulePersistor);

        commands = new CommandDispatcher();
        queries = new QueryDispatcher();

        application = new I18nApplication(commands, queries);
    }
}
