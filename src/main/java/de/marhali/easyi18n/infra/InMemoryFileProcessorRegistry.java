package de.marhali.easyi18n.infra;

import de.marhali.easyi18n.core.domain.config.FileCodec;
import de.marhali.easyi18n.core.ports.FileProcessorPort;
import de.marhali.easyi18n.core.ports.FileProcessorRegistryPort;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * In memory file processor registry with lazy init.
 *
 * @author marhali
 */
public class InMemoryFileProcessorRegistry implements FileProcessorRegistryPort {

    private final @NotNull Map<@NotNull FileCodec, @NotNull Supplier<? extends FileProcessorPort>> registry;
    private final @NotNull Map<@NotNull FileCodec,  FileProcessorPort> registryCache;

    public InMemoryFileProcessorRegistry(@NotNull Map<@NotNull FileCodec, @NotNull Supplier<? extends FileProcessorPort>> registry) {
        this.registry = registry;
        this.registryCache = new ConcurrentHashMap<>();
    }

    @Override
    public @NotNull FileProcessorPort get(@NotNull FileCodec fileCodec) {
        return registryCache.computeIfAbsent(fileCodec, k -> {
            var port = registry.get(k);

            if (port == null) {
                throw new IllegalArgumentException("Missing file processor for file codec: " + fileCodec);
            }

            return port.get();
        });
    }
}
