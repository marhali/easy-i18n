package de.marhali.easyi18n.core.ports;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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
}
