package de.marhali.easyi18n.next_domain;

import de.marhali.easyi18n.config.project.ProjectConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Factory that picks the desired {@link Map} implementation based on whether the keys should be sorted or not.
 *
 * @author marhali
 */
public record MapImplFactory(boolean sort) {
    public MapImplFactory(ProjectConfig config) {
        this(config.isSorting());
    }

    public <K extends Comparable<K>, V> Map<K, V> get() {
        return sort ? new TreeMap<>() : new LinkedHashMap<>();
    }
}
