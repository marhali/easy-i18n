package de.marhali.easyi18n.next_io;

import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.next_domain.I18nProjectStore;
import de.marhali.easyi18n.next_domain.MapImplFactory;
import de.marhali.easyi18n.next_io.file.FileProcessor;
import de.marhali.easyi18n.next_io.path.PathProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages read and write on translation files.
 * New IOHandler
 *
 * @author marhali
 */
public class PersistenceHandler {

    private final @NotNull Project project;
    private final @NotNull ProjectConfig config;
    private final @NotNull MapImplFactory mapImplFactory;
    private final @NotNull Map<ProjectConfigModule, ModuleTemplate> moduleTemplates;

    private final @NotNull PathProcessor pathProcessor;

    public PersistenceHandler(@NotNull Project project, @NotNull ProjectConfig config) {
        this.project = project;
        this.config = config;
        this.mapImplFactory = new MapImplFactory(config);
        this.moduleTemplates = config.getModules().stream()
            .collect(Collectors.toMap(module -> module, ModuleTemplate::new));

        this.pathProcessor = new PathProcessor(project, this.moduleTemplates);
    }

    public @NotNull I18nProjectStore read() throws Exception {
        I18nProjectStore store = new I18nProjectStore(this.mapImplFactory);
        Map<ProjectConfigModule, List<I18nFile>> moduleFiles = this.pathProcessor.processPaths();

        for (ProjectConfigModule module : moduleFiles.keySet()) {
            var files = moduleFiles.get(module);
            var template = this.moduleTemplates.get(module);
            var fileProcessor = FileProcessor.from(this.config, module, template, store);

            for (I18nFile file : files) {
                fileProcessor.read(file);
            }
        }

        return store;
    }

    // TODO: write(I18nProjectStore)
}
