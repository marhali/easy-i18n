package de.marhali.easyi18n.model;

import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an existing translation file in a context a specific folder strategy.
 * @author marhali
 */
public class TranslationFile {

    private final @NotNull VirtualFile virtualFile;
    private final @NotNull String locale;
    private final @Nullable KeyPath namespace;

    public TranslationFile(@NotNull VirtualFile virtualFile, @NotNull String locale, @Nullable KeyPath namespace) {
        this.virtualFile = virtualFile;
        this.locale = locale;
        this.namespace = namespace;
    }

    public @NotNull VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public @NotNull String getLocale() {
        return locale;
    }

    public @Nullable KeyPath getNamespace() {
        return namespace;
    }

    @Override
    public String toString() {
        return "TranslationFile{" +
                "virtualFile=" + virtualFile +
                ", locale='" + locale + '\'' +
                ", namespace='" + namespace + '\'' +
                '}';
    }
}
