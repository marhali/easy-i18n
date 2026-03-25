package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.MutableI18nModule;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.ports.FileProcessorPort;
import de.marhali.easyi18n.core.ports.FileProcessorRegistryPort;
import de.marhali.easyi18n.core.ports.PathResolverPort;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

/**
 * Module loader using the underlying io ports.
 *
 * @author marhali
 */
public class DefaultModuleLoader implements ModuleLoader {

    private final @NotNull CachedModuleTemplates cachedModuleTemplates;
    private final @NotNull PathResolverPort pathResolverPort;
    private final @NotNull FileProcessorRegistryPort fileProcessorRegistryPort;
    private final @NotNull TrackedI18nPathsService trackedI18nPathsService;

    public DefaultModuleLoader(
        @NotNull CachedModuleTemplates cachedModuleTemplates,
        @NotNull PathResolverPort pathResolverPort,
        @NotNull FileProcessorRegistryPort fileProcessorRegistryPort,
        @NotNull TrackedI18nPathsService trackedI18nPathsService
    ) {
        this.cachedModuleTemplates = cachedModuleTemplates;
        this.pathResolverPort = pathResolverPort;
        this.fileProcessorRegistryPort = fileProcessorRegistryPort;
        this.trackedI18nPathsService = trackedI18nPathsService;
    }

    @Override
    public void loadInto(@NotNull ProjectConfigModule config, @NotNull MutableI18nModule store) {
        // Resolve module specific templates
        Templates templates = cachedModuleTemplates.resolve(config.id());

        // Retrieve module specific file processor
        FileProcessorPort fileProcessorPort = fileProcessorRegistryPort.get(config.fileCodec());

        // Resolve all relevant translation file paths using the PathTemplate
        Set<@NotNull I18nPath> paths = pathResolverPort.resolvePaths(config, templates.path());

        // Iterate over all relevant translation file paths and load it's content using the file processor
        for (I18nPath path : paths) {
            try {
                fileProcessorPort.readInto(config, templates, path, store);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Track processed translation file paths
        trackedI18nPathsService.put(config.id(), paths);
    }
}
