package de.marhali.easyi18n.model;

import de.marhali.easyi18n.util.MapUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents structured tree view for translated messages.
 * @author marhali
 */
@Deprecated
public class LocalizedNode {

    public static final String ROOT_KEY = "root";

    @NotNull
    private final String key;

    @NotNull
    private TreeMap<String, LocalizedNode> children;

    @NotNull
    private Map<String, String> value;

    public LocalizedNode(@NotNull String key, @NotNull List<LocalizedNode> children) {
        this.key = key;
        this.children = MapUtil.convertToTreeMap(children);
        this.value = new HashMap<>();
    }

    public LocalizedNode(@NotNull String key, @NotNull Map<String, String> value) {
        this.key = key;
        this.children = new TreeMap<>();
        this.value = value;
    }

    public @NotNull String getKey() {
        return key;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public @NotNull Collection<LocalizedNode> getChildren() {
        return children.values();
    }

    public @Nullable LocalizedNode getChildren(@NotNull String key) {
        return children.get(key);
    }

    public void setChildren(@NotNull LocalizedNode... children) {
        this.value.clear();
        this.children = MapUtil.convertToTreeMap(Arrays.asList(children));
    }

    public void addChildren(@NotNull LocalizedNode... children) {
        this.value.clear();
        Arrays.stream(children).forEach(e -> this.children.put(e.getKey(), e));
    }

    public void removeChildren(@NotNull String key) {
        this.children.remove(key);
    }

    public @NotNull Map<String, String> getValue() {
        return value;
    }

    public void setValue(@NotNull Map<String, String> value) {
        this.children.clear();
        this.value = value;
    }
}