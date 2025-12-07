package de.marhali.easyi18n.next_io;

import com.intellij.openapi.vfs.VirtualFile;
import de.marhali.easyi18n.next_domain.I18nParams;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class I18nFile {
    private final @NotNull VirtualFile file;
    private final @NotNull I18nParams params;

    public I18nFile(@NotNull VirtualFile file, @NotNull I18nParams params) {
        this.file = file;
        this.params = params;
    }

    public @NotNull VirtualFile getFile() {
        return file;
    }

    public @NotNull I18nParams getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "I18nFile{" +
            "file=" + file +
            ", params=" + params +
            '}';
    }
}
