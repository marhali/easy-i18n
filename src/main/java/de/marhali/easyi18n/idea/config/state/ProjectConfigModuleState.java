package de.marhali.easyi18n.idea.config.state;

import de.marhali.easyi18n.core.domain.config.FileCodec;
import de.marhali.easyi18n.core.domain.config.KeyNamingConvention;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents {@link de.marhali.easyi18n.core.domain.config.ProjectConfigModule}.
 *
 * @author marhali
 */
public class ProjectConfigModuleState {
    public @Nullable String id;
    public @Nullable String pathTemplate;
    public @Nullable FileCodec fileCodec;
    public @Nullable String fileTemplate;
    public @Nullable String keyTemplate;
    public @Nullable String rootDirectory;
    public @Nullable Set<@NotNull String> defaultKeyPrefixes;
    public @Nullable String i18nTemplate;
    public @Nullable KeyNamingConvention keyNamingConvention;
    public @Nullable List<@NotNull EditorRuleState> editorRules;

    @Deprecated
    public ProjectConfigModuleState() {}

    public ProjectConfigModuleState(
        @Nullable String id,
        @Nullable String pathTemplate,
        @Nullable FileCodec fileCodec,
        @Nullable String fileTemplate,
        @Nullable String keyTemplate,
        @Nullable String rootDirectory,
        @Nullable Set<@NotNull String> defaultKeyPrefixes,
        @Nullable String i18nTemplate,
        @Nullable KeyNamingConvention keyNamingConvention,
        @Nullable List<@NotNull EditorRuleState> editorRules
    ) {
        this.id = id;
        this.pathTemplate = pathTemplate;
        this.fileCodec = fileCodec;
        this.fileTemplate = fileTemplate;
        this.keyTemplate = keyTemplate;
        this.rootDirectory = rootDirectory;
        this.defaultKeyPrefixes = defaultKeyPrefixes;
        this.i18nTemplate = i18nTemplate;
        this.keyNamingConvention = keyNamingConvention;
        this.editorRules = editorRules;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectConfigModuleState that = (ProjectConfigModuleState) o;
        return Objects.equals(id, that.id)
            && Objects.equals(pathTemplate, that.pathTemplate)
            && fileCodec == that.fileCodec
            && Objects.equals(fileTemplate, that.fileTemplate)
            && Objects.equals(keyTemplate, that.keyTemplate)
            && Objects.equals(rootDirectory, that.rootDirectory)
            && Objects.equals(defaultKeyPrefixes, that.defaultKeyPrefixes)
            && Objects.equals(i18nTemplate, that.i18nTemplate)
            && keyNamingConvention == that.keyNamingConvention
            && Objects.equals(editorRules, that.editorRules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id, pathTemplate, fileCodec, fileTemplate,
            keyTemplate, rootDirectory, defaultKeyPrefixes,
            i18nTemplate, keyNamingConvention, editorRules
        );
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
            ", editorRules=" + editorRules +
            '}';
    }
}
