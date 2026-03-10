package de.marhali.easyi18n.idea.config.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * Represents {@link de.marhali.easyi18n.core.domain.config.ProjectConfig}.
 *
 * @author marhali
 */
public class ProjectConfigState {
    public @Nullable Boolean keyComment;
    public @Nullable Boolean sorting;
    public @Nullable String previewLocale;
    public @Nullable Map<@NotNull String, @NotNull ProjectConfigModuleState> modules;

    @Deprecated
    public ProjectConfigState() {}

    public ProjectConfigState(
        @Nullable Boolean keyComment,
        @Nullable Boolean sorting,
        @Nullable String previewLocale,
        @Nullable Map<@NotNull String, @NotNull ProjectConfigModuleState> modules
    ) {
        this.keyComment = keyComment;
        this.sorting = sorting;
        this.previewLocale = previewLocale;
        this.modules = modules;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectConfigState that = (ProjectConfigState) o;
        return Objects.equals(keyComment, that.keyComment) && Objects.equals(sorting, that.sorting) && Objects.equals(previewLocale, that.previewLocale) && Objects.equals(modules, that.modules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyComment, sorting, previewLocale, modules);
    }

    @Override
    public String toString() {
        return "ProjectConfigState{" +
            "keyComment=" + keyComment +
            ", sorting=" + sorting +
            ", previewLocale='" + previewLocale + '\'' +
            ", modules=" + modules +
            '}';
    }
}
