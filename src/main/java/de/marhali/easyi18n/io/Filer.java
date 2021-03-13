package de.marhali.easyi18n.io;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

/**
 * Singleton service for file io operations.
 * @author marhali
 */
public class Filer {

    private static Filer INSTANCE;

    private final Project project;

    public static Filer getInstance(Project project) {
        return INSTANCE == null ? INSTANCE = new Filer(project) : INSTANCE;
    }

    private Filer(Project project) {
        this.project = project;
    }

    public VirtualFile getFile() {
        VirtualFile vfs = LocalFileSystem.getInstance().findFileByIoFile(new File(project.getBasePath() + "/src/lang/de.json"));
        return vfs;
    }
}