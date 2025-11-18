package de.marhali.easyi18n.next_io;

import com.intellij.openapi.vfs.VirtualFile;
import de.marhali.easyi18n.config.project.ProjectConfigModule;

import java.util.Map;

/**
 * @author marhali
 */
public class I18nFile {
    private final ProjectConfigModule module;
    private final VirtualFile file;
    private final Map<String, String> params;

    public I18nFile(ProjectConfigModule module, VirtualFile file, Map<String, String> params) {
        this.module = module;
        this.file = file;
        this.params = params;
    }

    public ProjectConfigModule getModule() {
        return module;
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
            "module=" + module +
            ", file=" + file +
            ", params=" + params +
            '}';
    }
}
