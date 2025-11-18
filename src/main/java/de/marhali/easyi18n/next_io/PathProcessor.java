package de.marhali.easyi18n.next_io;

import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.next_io.path.PathTemplate;

import java.util.*;

/**
 * Responsible for finding all relevant translation files based on the project-specific configuration.
 *
 * @author marhali
 */
public class PathProcessor {

    private final Project project;
    private final ProjectConfig config;

    private final Set<FileType> fileTypes;
    private final Map<ProjectConfigModule, PathTemplate> modulePathTemplates;

    public PathProcessor(Project project, ProjectConfig config) {
        this.project = project;
        this.config = config;

        this.fileTypes = determineFileTypes();
        this.modulePathTemplates = determineModulePathTemplates();
    }

    public List<I18nFile> findFiles() {
        List<I18nFile> result = new ArrayList<>();
        // TODO: maybe to increase perf. GlobalSearchScopes.directoryScope(project, ...);
        var scope = GlobalSearchScope.projectScope(project);

        for (FileType fileType : fileTypes) {
            Collection<VirtualFile> files = FileTypeIndex.getFiles(fileType, scope);

            for (VirtualFile file : files) {
                var path = file.getPath();

                for (Map.Entry<ProjectConfigModule, PathTemplate> entry : modulePathTemplates.entrySet()) {
                    var module = entry.getKey();
                    var template = entry.getValue();

                    var resolvedParams = template.match(path);

                    if (resolvedParams != null) {
                        result.add(new I18nFile(module, file, resolvedParams));
                        break; // It's enough if one entry matches (first come, first served)
                    }
                }
            }
        }

        return result;
    }

    private Set<FileType> determineFileTypes() {
        Set<FileType> fileTypes = new HashSet<>();

        for (List<String> extensions : config.getFileExtMapper().values()) {
            for (String extension : extensions) {
                var fileType = FileTypeManager.getInstance().getFileTypeByExtension(extension);
                fileTypes.add(fileType);
            }
        }

        return fileTypes;
    }

    private Map<ProjectConfigModule, PathTemplate> determineModulePathTemplates() {
        Map<ProjectConfigModule, PathTemplate> templates = new HashMap<>();

        for (ProjectConfigModule module : config.getModules()) {
            var pathTemplate = module.getPathTemplate();
            var expandedPathTemplate = PathMacroManager.getInstance(project).expandPath(pathTemplate);
            var compiledPathTemplate = PathTemplate.compile(expandedPathTemplate);
            templates.put(module, compiledPathTemplate);
        }

        return templates;
    }
}
