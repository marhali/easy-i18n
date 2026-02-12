package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.MapImplProvider;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provider that picks the desired {@link Map} implementation based on whether the keys should be sorted or not.
 *
 * @author marhali
 */
public record SortableMapImplProvider(
    boolean sort
) implements MapImplProvider {

    public SortableMapImplProvider(@NotNull ProjectConfigPort projectConfigPort) {
        this(projectConfigPort.read().sorting());
    }

    @Override
    public @NotNull <K extends Comparable<K>, V> Map<K, V> get() {
        return sort ? new TreeMap<>() : new LinkedHashMap<>();
    }
}
