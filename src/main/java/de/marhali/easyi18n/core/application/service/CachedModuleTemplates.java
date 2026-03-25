package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.domain.template.file.DefaultFileTemplate;
import de.marhali.easyi18n.core.domain.template.flavor.DefaultEditorFlavorTemplate;
import de.marhali.easyi18n.core.domain.template.key.DefaultKeyTemplate;
import de.marhali.easyi18n.core.domain.template.path.DefaultPathTemplate;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache for module-specific {@link Templates}.
 *
 * @author marhali
 */
public class CachedModuleTemplates {

    private final @NotNull ProjectConfigPort projectConfigPort;
    private final @NotNull Map<@NotNull ModuleId, @NotNull Templates> cache;

    public CachedModuleTemplates(@NotNull ProjectConfigPort projectConfigPort) {
        this(projectConfigPort, new HashMap<>());
    }

    public CachedModuleTemplates(@NotNull ProjectConfigPort projectConfigPort, @NotNull Map<@NotNull ModuleId, @NotNull Templates> cache) {
        this.projectConfigPort = projectConfigPort;
        this.cache = cache;
    }

    /**
     * Retrieves templates for a specific module.
     * @param moduleId Module identifier
     * @return {@link Templates}
     */
    public @NotNull Templates resolve(@NotNull ModuleId moduleId) {
        return cache.computeIfAbsent(moduleId, this::internalResolveCacheMiss);
    }

    /**
     * Invalidates cached templates for a specific module.
     * @param moduleId Module identifier
     */
    public void invalidate(@NotNull ModuleId moduleId) {
        cache.remove(moduleId);
    }

    /**
     * Invalidates all cached module templates.
     */
    public void invalidateAll() {
        cache.clear();
    }

    private @NotNull Templates internalResolveCacheMiss(@NotNull ModuleId moduleId) {
        var projectConfig = projectConfigPort.read();
        var moduleConfig = projectConfig.modules().get(moduleId);

        if (moduleConfig == null) {
            throw new IllegalArgumentException("Unknown module: " + moduleId);
        }

        var pathTemplate = DefaultPathTemplate.compile(moduleConfig.pathTemplate());
        var fileTemplate = DefaultFileTemplate.compile(moduleConfig.fileTemplate());
        var keyTemplate = DefaultKeyTemplate.compile(moduleConfig.keyTemplate());
        var flavorTemplate = DefaultEditorFlavorTemplate.compile(moduleConfig.editorFlavorTemplate());

        return new Templates(pathTemplate, fileTemplate, keyTemplate, flavorTemplate);
    }
}
