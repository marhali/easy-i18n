package de.marhali.easyi18n.util;

import java.util.*;

/**
 * Applies sorting to {@link Properties} files.
 * @author marhali
 */
@Deprecated
public class SortedProperties extends Properties {

    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(new TreeSet<>(super.keySet()));
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        TreeMap<Object, Object> sorted = new TreeMap<>();

        for(Object key : super.keySet()) {
            sorted.put(key, get(key));
        }

        return sorted.entrySet();
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<>(super.keySet()));
    }
}