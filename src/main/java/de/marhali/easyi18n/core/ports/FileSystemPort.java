package de.marhali.easyi18n.core.ports;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

/**
 * Port for the underlying file system.
 *
 * @author marhali
 */
public interface FileSystemPort {
    /**
     * Reads the contents from the specified file.
     * @param path File path
     * @return Text content of the file
     * @throws IOException Error whilst reading the file
     */
    @NotNull String read(@NotNull String path) throws IOException;

    /**
     * Writes the given contents to the specified file.
     * @param path File path
     * @param content Text content of the file
     * @throws IOException Error whilst writing the file
     */
    void write(@NotNull String path, @NotNull String content) throws IOException;

    /**
     * Removes many files by provided paths.
     * @param paths File paths to remove
     * @throws IOException Error whilst removing the file
     */
    void bulkDelete(@NotNull Set<@NotNull String> paths) throws IOException;
}
