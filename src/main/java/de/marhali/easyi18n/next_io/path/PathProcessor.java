package de.marhali.easyi18n.next_io.path;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.next_io.I18nFile;
import de.marhali.easyi18n.next_io.ModuleTemplate;

import java.util.*;

/**
 * Responsible for finding all relevant translation files based on the project-specific configuration.
 *
 * @author marhali
 */
public class PathProcessor {

    private final Project project;
    private final Map<ProjectConfigModule, ModuleTemplate> moduleTemplates;
    private final Set<FileType> fileTypes;

    public PathProcessor(Project project, Map<ProjectConfigModule, ModuleTemplate> moduleTemplates) {
        this.project = project;
        this.moduleTemplates = moduleTemplates;
        this.fileTypes = determineFileTypes(moduleTemplates.values());
    }

    public Map<ProjectConfigModule, List<I18nFile>> processPaths() {
        Map<ProjectConfigModule, List<I18nFile>> result = new HashMap<>();

        // TODO: maybe to increase perf. GlobalSearchScopes.directoryScope(project, ...);
        var scope = GlobalSearchScope.projectScope(project);

        for (FileType fileType : fileTypes) {
            Collection<VirtualFile> files = FileTypeIndex.getFiles(fileType, scope);

            for (VirtualFile file : files) {
                var path = file.getPath();

                for (Map.Entry<ProjectConfigModule, ModuleTemplate> entry : moduleTemplates.entrySet()) {
                    var module = entry.getKey();
                    var template = entry.getValue();

                    var resolvedParams = template.path().match(path);

                    if (resolvedParams != null) {
                        result.computeIfAbsent(module, (_module) -> new ArrayList<>())
                            .add(new I18nFile(file, resolvedParams));
                        break; // It's enough if one entry matches (first come, first served)
                    }
                }
            }
        }

        return result;
    }

    private Set<FileType> determineFileTypes(Collection<ModuleTemplate> templates) {
        Set<FileType> fileTypes = new HashSet<>();

        for (ModuleTemplate template : templates) {
            var templateFileExtension = template.path().getFileExtension();

            if (templateFileExtension != null) {
                var fileType = FileTypeManager.getInstance().getFileTypeByExtension(templateFileExtension);
                fileTypes.add(fileType);
            }
        }

        return fileTypes;
    }
}
