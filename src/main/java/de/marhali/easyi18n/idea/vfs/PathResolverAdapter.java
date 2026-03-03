package de.marhali.easyi18n.idea.vfs;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.*;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.template.path.PathTemplate;
import de.marhali.easyi18n.core.ports.PathResolverPort;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    public @NotNull Set<@NotNull I18nPath> resolvePaths(@NotNull ProjectConfigModule module, @NotNull PathTemplate pathTemplate) {
        // Wait for indexes to be built (smart mode)
        DumbService.getInstance(project).waitForSmartMode();

        String parentPath = pathTemplate.getMostCommonParentPath();
        Path parentPathNio = Path.of(parentPath);

        VirtualFile vf = LocalFileSystem.getInstance().findFileByNioFile(parentPathNio);

        if (vf == null) {
            throw new RuntimeException("Could not find most common parent: " + parentPathNio);
        }

        if (!vf.isDirectory()) {
            vf = vf.getParent();
            if (vf == null) {
                throw new RuntimeException("Could not resolve parent of most common parent: " + parentPathNio);
            }
        }

        GlobalSearchScope scope = ProjectScope.getContentScope(project)
            .intersectWith(GlobalSearchScopes.directoryScope(project, vf, true));

        var fileType = resolveModuleFileType(pathTemplate);

        // fileType should not be unknown type
        if (fileType == UnknownFileType.INSTANCE) {
            throw new IllegalArgumentException("Received unknown file type for path template: " + pathTemplate);
        }

        // Retrieve all files (paths) from the FileTypeIndex
        Collection<VirtualFile> paths = ReadAction.compute(() -> FileTypeIndex.getFiles(fileType, scope));

        var result = new HashSet<I18nPath>();

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
