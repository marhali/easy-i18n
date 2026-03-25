package de.marhali.easyi18n.core.ports;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.template.path.PathTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Port for resolving relevant translation file paths.
 *
 * @author marhali
 */
public interface PathResolverPort {
    /**
     * Resolves relevant translation file paths for the given module.
     * @param module Module config
     * @param pathTemplate Path template
     * @return Collection of matched {@link I18nPath}s
     */
    @NotNull Set<@NotNull I18nPath> resolvePaths(@NotNull ProjectConfigModule module, @NotNull PathTemplate pathTemplate);
}
