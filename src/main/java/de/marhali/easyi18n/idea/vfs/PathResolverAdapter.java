package de.marhali.easyi18n.idea.vfs;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.template.path.PathTemplate;
import de.marhali.easyi18n.core.ports.PathResolverPort;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * IntellIj path resolver adapter using {@link FileTypeIndex}.
 *
 * @author marhali
 */
public class PathResolverAdapter implements PathResolverPort {

    private final @NotNull Project project;

    public PathResolverAdapter(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public @NotNull Collection<@NotNull I18nPath> resolvePaths(@NotNull ProjectConfigModule module, @NotNull PathTemplate pathTemplate) {
        DumbService.getInstance(project).waitForSmartMode(); // TODO: maybe we can find st better

        var scope = GlobalSearchScope.projectScope(project); // TODO: intersect with module directory to improve performance
        var fileType = resolveModuleFileType(pathTemplate);

        // Retrieve all files (paths) from the FileTypeIndex
        Collection<VirtualFile> paths = ReadAction.compute(() -> FileTypeIndex.getFiles(fileType, scope));

        var result = new ArrayList<I18nPath>();

        // Iterate over these files and check if they are applicable
        for (VirtualFile virtualFile : paths) {
            var canonical = virtualFile.getPath();
            var resolvedParams = pathTemplate.matchCanonical(canonical);

            if (resolvedParams != null) {
                result.add(new I18nPath(canonical, resolvedParams));
            }
        }

        return result;
    }

    private @NotNull FileType resolveModuleFileType(@NotNull PathTemplate pathTemplate) {
        return FileTypeManager.getInstance().getFileTypeByExtension(pathTemplate.getFileExtension());
    }
}
