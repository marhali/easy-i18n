package thito.nodeflow.config;

import java.util.*;
import java.util.stream.*;

public class ListSection extends ArrayList<Object> implements Section {
    private Section parent;
    private String name;

    public ListSection() {
        super();
    }

    public ListSection(Collection<?> c) {
        super();
        addAll(c);
    }

    public void setParent(Section parent, String name) {
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
                        .findAny().map(Map.Entry::getKey).orElse(null);
            }
        }
        return name;
    }

    public Optional<Object> getObject(int index) {
        return index >= 0 && index < size() ? Optional.ofNullable(get(index)) : Optional.empty();
    }

    @Override
    public Section getParent() {
        return parent;
    }

    @Override
    public Set<String> getKeys() {
        return IntStream.range(0, size()).mapToObj(String::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Optional<?> getInScope(String key) {
        try {
            return Optional.ofNullable(get(Integer.parseInt(key)));
        } catch (Throwable t) {
        }
        return Optional.empty();
    }

    @Override
    public void setInScope(String key, Object value) {
        try {
            set(Integer.parseInt(key), value);
        } catch (Throwable t) {
        }
    }

    @Override
    public Object set(int index, Object element) {
        element = Section.wrap(element);
        if (element instanceof Section) element = Section.wrapParent(this, null, (Section) element);
        return super.set(index, element);
    }

    @Override
    public boolean add(Object o) {
        o = Section.wrap(o);
        if (o instanceof Section) o = Section.wrapParent(this, null, (Section) o);
        return super.add(o);
    }

    @Override
    public void add(int index, Object element) {
        element = Section.wrap(element);
        if (element instanceof Section) element = Section.wrapParent(this, null, (Section) element);
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        c.forEach(o -> add(o));
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        List<Object> wrapped = new ArrayList<>();
        c.forEach(obj -> {
            Object o = Section.wrap(obj);
            if (o instanceof Section) o = Section.wrapParent(this, null, (Section) o);
            wrapped.add(o);
        });
        return super.addAll(index, wrapped);
    }

    @Override
    public String toString() {
        return Section.toString(this);
    }

    public <T extends Enum<T>> Optional<T> getEnum(int index, Class<T> clz) {
        return getObject(index).map(o -> {
            try {
                return Enum.valueOf(clz, String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }

    public Optional<String> getString(int index) {
        return getObject(index).map(String::valueOf);
    }

    public Optional<Integer> getInteger(int index) {
        return getObject(index).map(o -> {
            try {
                return Integer.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }

    public Optional<Double> getDouble(int index) {
        return getObject(index).map(o -> {
            try {
                return Double.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }

    public Optional<Long> getLong(int index) {
        return getObject(index).map(o -> {
            try {
                return Long.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }

    public Optional<Float> getFloat(int index) {
        return getObject(index).map(o -> {
            try {
                return Float.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }

    public Optional<Short> getShort(int index) {
        return getObject(index).map(o -> {
            try {
                return Short.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }

    public Optional<Byte> getByte(int index) {
        return getObject(index).map(o -> {
            try {
                return Byte.valueOf(String.valueOf(o));
            } catch (Throwable t) {
                return null;
            }
        });
    }

    public Optional<Character> getCharacter(int index) {
        return getObject(index).map(o -> {
            String text = String.valueOf(o);
            return text.isEmpty() ? null : text.charAt(0);
        });
    }

    public Optional<Boolean> getBoolean(int index) {
        return getObject(index).map(o -> {
            String text = String.valueOf(o);
            return text.equals("true") ? Boolean.TRUE : text.equals("false") ? Boolean.FALSE : null;
        });
    }

    public Optional<MapSection> getMap(int index) {
        return getObject(index).map(o -> {
            if (o instanceof Map) {
                if (o instanceof MapSection) return (MapSection) o;
                MapSection mapSection = new MapSection((Map<?, ?>) o);
                mapSection.setParent(this, null);
                return mapSection;
            }
            return null;
        });
    }

    public Optional<ListSection> getList(int index) {
        return getObject(index).map(o -> {
            if (o instanceof List) {
                if (o instanceof ListSection) {
                    return (ListSection) o;
                }
                ListSection list = new ListSection((List<?>) o);
                list.setParent(this, null);
                return list;
            }
            ListSection list = new ListSection(Collections.singleton(o));
            list.setParent(this, null);
            return list;
        });
    }
}
