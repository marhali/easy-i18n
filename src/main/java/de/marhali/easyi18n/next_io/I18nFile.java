package de.marhali.easyi18n.next_io;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.Map;

/**
 * @author marhali
 */
public class I18nFile {
    private final VirtualFile file;
    private final Map<String, String> params;

    public I18nFile(VirtualFile file, Map<String, String> params) {
        this.file = file;
        this.params = params;
    }

    public VirtualFile getFile() {
        return file;
    }

    public Map<String, String> getParams() {
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
