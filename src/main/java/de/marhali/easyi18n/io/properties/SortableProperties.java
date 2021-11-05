package de.marhali.easyi18n.io.properties;

import java.util.*;

/**
 * Extends {@link Properties} class to support sorted or non-sorted keys.
 * @author marhali
 */
public class SortableProperties extends Properties {

    private final transient Map<Object, Object> properties;

    public SortableProperties(boolean sort) {
        this.properties = sort ? new TreeMap<>() : new LinkedHashMap<>();
    }

    public Map<Object, Object> getProperties() {
        return this.properties;
    }

    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(new TreeSet<>(super.keySet()));
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return this.properties.entrySet();
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        return this.properties.put(key, value);
    }
}