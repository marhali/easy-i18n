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
import java.util.Collection;

/**
 * Module loader using the underlying io ports.
 *
 * @author marhali
 */
public class DefaultModuleLoader implements ModuleLoader {

    private final @NotNull CachedModuleTemplates cachedModuleTemplates;
    private final @NotNull PathResolverPort pathResolverPort;
    private final @NotNull FileProcessorRegistryPort fileProcessorRegistryPort;

    public DefaultModuleLoader(@NotNull CachedModuleTemplates cachedModuleTemplates, @NotNull PathResolverPort pathResolverPort, @NotNull FileProcessorRegistryPort fileProcessorRegistryPort) {
        this.cachedModuleTemplates = cachedModuleTemplates;
        this.pathResolverPort = pathResolverPort;
        this.fileProcessorRegistryPort = fileProcessorRegistryPort;
    }

    @Override
    public void loadInto(@NotNull ProjectConfigModule config, @NotNull MutableI18nModule store) {
        Templates templates = cachedModuleTemplates.resolve(config.id());

        FileProcessorPort fileProcessorPort = fileProcessorRegistryPort.get(config.fileCodec());
        Collection<@NotNull I18nPath> paths = pathResolverPort.resolvePaths(config, templates.path());

        for (I18nPath path : paths) {
            try {
                fileProcessorPort.readInto(config, templates, path, store);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
