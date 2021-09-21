package thito.nodeflow.config;

import java.util.*;

public class MapSection extends HashMap<String, Object> implements Section {
    private Section parent;
    private String name;

    public MapSection() {
        super();
    }

    public MapSection(Map<?, ?> m) {
        super();
        m.forEach((key, value) -> put(String.valueOf(key), value));
    }

    protected void setParent(Section parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    @Override
    public String getName() {
        if (name == null && parent != null) {
            if (parent instanceof ListSection) {
                return String.valueOf(((ListSection) parent).indexOf(this));
            }
            if (parent instanceof MapSection) {
                return ((MapSection) parent).entrySet().stream().filter(e -> Objects.equals(e.getValue(), this))
                        .findAny().map(Entry::getKey).orElse(null);
            }
        }
        return name;
    }

    @Override
    public Section getParent() {
        return parent;
    }

    @Override
    public void setInScope(String key, Object value) {
        put(key, value);
    }

    @Override
    public Object put(String key, Object value) {
        value = Section.wrap(value);
        if (value instanceof Section) value = Section.wrapParent(this, key, (Section) value);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        m.forEach(this::put);
    }

    @Override
    public Set<String> getKeys() {
        return keySet();
    }

    @Override
    public Optional<?> getInScope(String key) {
        return Optional.ofNullable(get(key));
    }

    @Override
    public String toString() {
        return Section.toString(this);
    }
}
