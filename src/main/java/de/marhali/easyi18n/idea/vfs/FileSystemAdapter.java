package de.marhali.easyi18n.idea.vfs;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import de.marhali.easyi18n.core.ports.FileSystemPort;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

/**
 * IntelliJ file system adapter.
 *
 * @author marhali
 */
public class FileSystemAdapter implements FileSystemPort {

    private final @NotNull Project project;

    public FileSystemAdapter(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public @NotNull String read(@NotNull String path) throws IOException {
        Path nio = Path.of(path);

        return ReadAction.compute(() -> {
            VirtualFile vf = LocalFileSystem.getInstance().findFileByNioFile(nio);

            if (vf == null || !vf.isValid() || vf.isDirectory()) {
                throw new IOException("Could not find VirtualFile from path: " + path);
            }

            Document cachedDocument = FileDocumentManager.getInstance().getCachedDocument(vf);

            if (cachedDocument != null) {
                return cachedDocument.getText();
            }

            return VfsUtilCore.loadText(vf);
        });
    }

    @Override
    public void write(@NotNull String path, @NotNull String content) throws IOException {
        Path nio = Path.of(path);

        VirtualFile vf = LocalFileSystem.getInstance().findFileByNioFile(nio);

        if (vf == null || !vf.isValid() || vf.isDirectory()) {
            throw new IOException("Could not find VirtualFile from path: " + path);
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                WriteCommandAction.writeCommandAction(project)
                    .withName("Apply Translation Change")
                    .run(() -> {
                        ReadonlyStatusHandler.OperationStatus status = ReadonlyStatusHandler.getInstance(project).ensureFilesWritable(Collections.singleton(vf));

                        if (status.hasReadonlyFiles()) {
                            throw new IllegalStateException("Cannot apply changes on files in read-only mode");
                        }

                        FileDocumentManager fdm =  FileDocumentManager.getInstance();
                        Document document = fdm.getDocument(vf);

                        if (document == null) {
                            throw new IllegalStateException("Document is not available");
                        }

                        if (Objects.equals(document.getText(), content)) {
                            // Nothing changed
                            return;
                        }

                        document.setText(content);

                        PsiDocumentManager pdm = PsiDocumentManager.getInstance(project);
                        pdm.commitDocument(document);

                        PsiFile psi = PsiManager.getInstance(project).findFile(vf);

                        if (psi != null) {
                            CodeStyleManager.getInstance(project).reformat(psi);
                            pdm.doPostponedOperationsAndUnblockDocument(document);
                        }

                        fdm.saveDocument(document);
                    });
            } catch (Throwable e) {
                e.printStackTrace(); // TODO: ex handling
            }
        });
    }
}
