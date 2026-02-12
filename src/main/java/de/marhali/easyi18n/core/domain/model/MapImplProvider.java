package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Provider for retrieving the desired {@link Map} implementation.
 *
 * @author marhali
 */
public interface MapImplProvider {
    @NotNull <K extends Comparable<K>, V> Map<K, V> get();
}
