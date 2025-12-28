package de.marhali.easyi18n.next_io;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import de.marhali.easyi18n.next_domain.I18nParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @param path Represented file path.
 * @param params Associated path params.
 */
public record I18nPath(
    @NotNull String path,
    @NotNull I18nParams params
) {

    public static I18nPath from(@NotNull String path, @NotNull I18nParams params) {
        return new I18nPath(path, params);
    }

    public @NotNull VirtualFile asVirtualFile() {
        // TODO: what about create mode?
        return LocalFileSystem.getInstance().findFileByPath(path);
    }

    @Override
    public @NotNull String toString() {
        return "I18nPath{" +
            "path='" + path + '\'' +
            ", params=" + params +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        I18nPath i18nPath = (I18nPath) o;
        return Objects.equals(path, i18nPath.path) && Objects.equals(params, i18nPath.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, params);
    }
}
