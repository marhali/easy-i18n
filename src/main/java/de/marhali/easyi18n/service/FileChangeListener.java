package de.marhali.easyi18n.service;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;

import de.marhali.easyi18n.InstanceManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * Listens for file changes inside configured @localesPath. See {@link AsyncFileListener}.
 * Will trigger the reload function of the i18n instance if a relevant file was changed.
 * @author marhali
 */
public class FileChangeListener implements AsyncFileListener {

    private static final Logger logger = Logger.getInstance(FileChangeListener.class);

    private final @NotNull Project project;
    private @Nullable String localesPath;

    public FileChangeListener(@NotNull Project project) {
        this.project = project;
        this.localesPath = null; // Wait for any update before listening to file changes
    }

    public void updateLocalesPath(@Nullable String localesPath) {
        if(localesPath != null && !localesPath.isEmpty()) {
            VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(new File(localesPath));

            if(file != null && file.isDirectory()) {
                this.localesPath = file.getPath();
                return;
            }
        }

        this.localesPath = null;
    }

    @Override
    public ChangeApplier prepareChange(@NotNull List<? extends @NotNull VFileEvent> events) {
        return new ChangeApplier() {
            @Override
            public void afterVfsChange() {
                if(localesPath != null) {
                    events.forEach((e) -> {
                        if(e.getPath().contains(localesPath)) { // Perform reload
                            logger.debug("Detected file change. Reloading instance...");
                            InstanceManager.get(project).reload();
                        }
                    });
                }
            }
        };
    }
}