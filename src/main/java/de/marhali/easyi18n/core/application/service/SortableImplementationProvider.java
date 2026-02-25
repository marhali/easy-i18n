package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.ImplementationProvider;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Provider that picks the desired {@link Map} implementation based on whether the keys should be sorted or not.
 *
 * @author marhali
 */
public record SortableImplementationProvider(
    @NotNull ProjectConfigPort projectConfigPort
) implements ImplementationProvider {

    private boolean sort() {
        return projectConfigPort.read().sorting();
    }

    @Override
    public @NotNull <K extends Comparable<K>, V> Map<K, V> getMap() {
        return sort() ? new TreeMap<>() : new LinkedHashMap<>();
    }

    @Override
    public @NotNull <K extends Comparable<K>, V> Map<K, V> getMap(int initialCapacity) {
        // TreeMap does not support initialCapacity
        return sort() ? new TreeMap<>() : new LinkedHashMap<>(initialCapacity);
    }

    @Override
    public @NotNull <K extends Comparable<K>, V> Map<K, V> getMap(Map<K, V> initialMappings) {
        return sort() ? new TreeMap<>(initialMappings) : new LinkedHashMap<>(initialMappings);
    }

    @Override
    public @NotNull <E extends Comparable<E>> Set<E> getSet() {
        return sort() ? new TreeSet<>() : new LinkedHashSet<>();
    }

    @Override
    public @NotNull <E extends Comparable<E>> Set<E> getSet(int initialCapacity) {
        // TreeSet does not support initialCapacity
        return sort() ? new TreeSet<>() : new LinkedHashSet<>(initialCapacity);
    }

    @Override
    public @NotNull <E extends Comparable<E>> Set<E> getSet(Set<E> initialValues) {
        return sort() ? new TreeSet<>(initialValues) : new LinkedHashSet<>(initialValues);
    }
}
