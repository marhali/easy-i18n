package de.marhali.easyi18n.core.adapters;

import de.marhali.easyi18n.core.ports.FileSystemPort;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author marhali
 */
public class InMemoryFileSystemAdapter implements FileSystemPort {

    private final @NotNull Map<@NotNull String, @NotNull String> contents;

    public InMemoryFileSystemAdapter() {
        this.contents = new HashMap<>();
    }

    @Override
    public @NotNull String read(@NotNull String path) throws IOException {
        return contents.get(path);
    }

    @Override
    public void write(@NotNull String path, @NotNull String content) throws IOException {
        put(path, content);
    }

    @Override
    public void bulkDelete(@NotNull Set<@NotNull String> paths) throws IOException {
        for (String path : paths) {
            contents.remove(path);
        }
    }

    public void put(@NotNull String path, @NotNull String content) {
        contents.put(path, content);
    }

    public void clear() {
        contents.clear();
    }
}
