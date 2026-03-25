package de.marhali.easyi18n.core.ports;

import de.marhali.easyi18n.core.domain.config.FileCodec;
import org.jetbrains.annotations.NotNull;

/**
 * Port for a file processor registry.
 *
 * @author marhali
 */
public interface FileProcessorRegistryPort {
    /**
     * Retrieves the {@link FileProcessorPort} for the specified file codec.
     * @param fileCodec File codec
     * @return {@link FileProcessorPort}
     */
    @NotNull FileProcessorPort get(@NotNull FileCodec fileCodec);
}
