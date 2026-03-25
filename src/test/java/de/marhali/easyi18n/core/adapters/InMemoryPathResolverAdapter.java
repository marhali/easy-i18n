package de.marhali.easyi18n.core.adapters;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.template.path.PathTemplate;
import de.marhali.easyi18n.core.ports.PathResolverPort;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author marhali
 */
public class InMemoryPathResolverAdapter implements PathResolverPort {

    private final @NotNull Map<@NotNull ModuleId, @NotNull Set<@NotNull I18nPath>> paths;

    public InMemoryPathResolverAdapter() {
        this.paths = new HashMap<>();
    }

    @Override
    public @NotNull Set<@NotNull I18nPath> resolvePaths(@NotNull ProjectConfigModule module, @NotNull PathTemplate pathTemplate) {
        return paths.getOrDefault(module.id(), Set.of());
    }

    public void clearAll() {
        this.paths.clear();
    }

    public void clear(@NotNull ModuleId moduleId) {
        this.paths.remove(moduleId);
    }

    public void add(@NotNull ModuleId moduleId, @NotNull I18nPath path) {
        this.paths.computeIfAbsent(moduleId, (_moduleId) -> new HashSet<>()).add(path);
    }

    public void put(@NotNull ModuleId moduleId, @NotNull Set<@NotNull I18nPath> paths) {
        this.paths.put(moduleId, paths);
    }
}
