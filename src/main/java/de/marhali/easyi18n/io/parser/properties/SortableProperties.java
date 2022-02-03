package de.marhali.easyi18n.io.parser.properties;

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
    public Object get(Object key) {
        return this.properties.get(key);
    }

    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(this.properties.keySet());
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return this.properties.entrySet();
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        return this.properties.put(key, value);
    }

    @Override
    public String toString() {
        return this.properties.toString();
    }
}