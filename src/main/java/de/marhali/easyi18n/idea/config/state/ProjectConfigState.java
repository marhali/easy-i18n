package de.marhali.easyi18n.idea.config.state;

import java.util.Map;
import java.util.Objects;

/**
 * Represents {@link de.marhali.easyi18n.core.domain.config.ProjectConfig}.
 *
 * @author marhali
 */
public class ProjectConfigState {
    public Boolean keyComment;
    public Boolean editorAssistance;
    public Boolean sorting;
    public String previewLocale;
    public Map<String, ProjectConfigModuleState> modules;

    public ProjectConfigState() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectConfigState that = (ProjectConfigState) o;
        return Objects.equals(keyComment, that.keyComment) && Objects.equals(editorAssistance, that.editorAssistance) && Objects.equals(sorting, that.sorting) && Objects.equals(previewLocale, that.previewLocale) && Objects.equals(modules, that.modules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyComment, editorAssistance, sorting, previewLocale, modules);
    }

    @Override
    public String toString() {
        return "ProjectConfigState{" +
            "keyComment=" + keyComment +
            ", editorAssistance=" + editorAssistance +
            ", sorting=" + sorting +
            ", previewLocale='" + previewLocale + '\'' +
            ", modules=" + modules +
            '}';
    }
}
