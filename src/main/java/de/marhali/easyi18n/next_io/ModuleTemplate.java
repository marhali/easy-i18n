package de.marhali.easyi18n.next_io;

import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.next_io.file.FileTemplate;
import de.marhali.easyi18n.next_io.key.KeyTemplate;
import de.marhali.easyi18n.next_io.path.PathTemplate;
import org.jetbrains.annotations.NotNull;

/**
 * Holder of module-specific template syntax values.
 *
 * @author marhali
 */
public record ModuleTemplate(
    @NotNull PathTemplate path,
    @NotNull FileTemplate file,
    @NotNull KeyTemplate key
) {
    public ModuleTemplate(ProjectConfigModule config) {
        this(
            PathTemplate.compile(config.getPathTemplate()), // TODO: maybe PathMacroManager#expandPath
            FileTemplate.compile(config.getFileTemplate()),
            KeyTemplate.compile(config.getKeyTemplate())
        );
    }
}
