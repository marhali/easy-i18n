package de.marhali.easyi18n.next_io;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.next_domain.I18nKey;
import de.marhali.easyi18n.next_domain.I18nParams;
import de.marhali.easyi18n.next_domain.I18nProjectStore;
import de.marhali.easyi18n.next_domain.MapImplFactory;
import de.marhali.easyi18n.next_io.file.FileProcessor;
import de.marhali.easyi18n.next_io.path.PathProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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

    public void write(@NotNull I18nProjectStore store) throws Exception {
        for (ProjectConfigModule module : this.config.getModules()) {
            var moduleName = module.getName();

            if (!store.hasModule(moduleName)) {
                continue;
            }

            var moduleStore = store.getModule(moduleName);
            var moduleTemplate = this.moduleTemplates.get(module);
            var moduleProcessor = FileProcessor.from(this.config, module, moduleTemplate, store);
            var moduleKeysByPaths = new HashMap<I18nPath, Set<TranslationConsumer>>();

            for (I18nKey key : moduleStore.getKeys()) {
                var paramsBuilder = moduleTemplate.key().parse(key).toBuilder();

                var locales = moduleStore.getTranslation(key).getLocales();
                paramsBuilder.add(I18nBuiltinParam.LOCALE, locales.toArray(new String[0]));

                var params = paramsBuilder.build();
                var consumer = new TranslationConsumer(key, moduleStore.getTranslation(key), params);
                var paths = moduleTemplate.path().build(params);

                for (I18nPath path : paths) {
                    moduleKeysByPaths.computeIfAbsent(path, (_path) -> new HashSet<>()).add(consumer);
                }
            }

            for (I18nPath path : moduleKeysByPaths.keySet()) {
                var file = new I18nFile(path.asVirtualFile(), path.params());
                var consumers = moduleKeysByPaths.get(path);
                moduleProcessor.write(file, consumers);
            }

            // TODO: might be useful: track I18nFile's from last read and delete if write does not care about them anymore
        }
    }
}
