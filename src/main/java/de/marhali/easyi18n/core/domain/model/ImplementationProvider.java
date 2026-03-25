package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Provider for retrieving the desired {@link Map} or {@link Set} implementations.
 *
 * @author marhali
 */
public interface ImplementationProvider {
    /**
     * @return New {@link Map} instance.
     */
    @NotNull <K extends Comparable<K>, V> Map<K, V> getMap();

    /**
     * @return New {@link Map} instance with initial capacity.
     */
    @NotNull <K extends Comparable<K>, V> Map<K, V> getMap(int initialCapacity);

    /**
     * @return New {@link Map} instance with initial mappings.
     */
    @NotNull <K extends Comparable<K>, V> Map<K, V> getMap(Map<K, V> initialMappings);

    /**
     * @return New {@link Set} instance.
     */
    @NotNull <E extends Comparable<E>> Set<E> getSet();

    /**
     * @return New {@link Set} instance with initial capacity.
     */
    @NotNull <E extends Comparable<E>> Set<E> getSet(int initialCapacity);

    /**
     * @return New {@link Set} instance with initial values.
     */
    @NotNull <E extends Comparable<E>> Set<E> getSet(Set<E> initialValues);

}
