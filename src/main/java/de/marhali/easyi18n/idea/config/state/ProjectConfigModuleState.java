package de.marhali.easyi18n.idea.config.state;

import de.marhali.easyi18n.core.domain.config.FileCodec;
import de.marhali.easyi18n.core.domain.config.KeyNamingConvention;

import java.util.Objects;

/**
 * Represents {@link de.marhali.easyi18n.core.domain.config.ProjectConfigModule}.
 *
 * @author marhali
 */
public class ProjectConfigModuleState {
    public String id;
    public String pathTemplate;
    public FileCodec fileCodec;
    public String fileTemplate;
    public String keyTemplate;
    public String rootDirectory;
    public String defaultNamespace;
    public String i18nTemplate;
    public KeyNamingConvention keyNamingConvention;

    public ProjectConfigModuleState() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectConfigModuleState that = (ProjectConfigModuleState) o;
        return Objects.equals(id, that.id) && Objects.equals(pathTemplate, that.pathTemplate) && fileCodec == that.fileCodec && Objects.equals(fileTemplate, that.fileTemplate) && Objects.equals(keyTemplate, that.keyTemplate) && Objects.equals(rootDirectory, that.rootDirectory) && Objects.equals(defaultNamespace, that.defaultNamespace) && Objects.equals(i18nTemplate, that.i18nTemplate) && keyNamingConvention == that.keyNamingConvention;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pathTemplate, fileCodec, fileTemplate, keyTemplate, rootDirectory, defaultNamespace, i18nTemplate, keyNamingConvention);
    }

    @Override
    public String toString() {
        return "ProjectConfigModuleState{" +
            "id='" + id + '\'' +
            ", pathTemplate='" + pathTemplate + '\'' +
            ", fileCodec=" + fileCodec +
            ", fileTemplate='" + fileTemplate + '\'' +
            ", keyTemplate='" + keyTemplate + '\'' +
            ", rootDirectory='" + rootDirectory + '\'' +
            ", defaultKeyPrefixes=" + defaultKeyPrefixes +
            ", i18nTemplate='" + i18nTemplate + '\'' +
            ", keyNamingConvention=" + keyNamingConvention +
            '}';
    }
}
