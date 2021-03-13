package de.marhali.easyi18n.data;

import de.marhali.easyi18n.util.TranslationsUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Translations {

    private List<String> locales;
    private LocalizedNode nodes;

    public Translations(List<String> locales, LocalizedNode nodes) {
        this.locales = locales;
        this.nodes = nodes;
    }

    public List<String> getLocales() {
        return locales;
    }

    public LocalizedNode getNodes() {
        return nodes;
    }

    public @Nullable LocalizedNode getNode(@NotNull String fullPath) {
        List<String> sections = TranslationsUtil.getSections(fullPath);

        LocalizedNode node = nodes;

        for(String section : sections) {
            if(node == null) {
                return null;
            }
            node = node.getChildren(section);
        }

        return node;
    }

    public @NotNull LocalizedNode getOrCreateNode(@NotNull String fullPath) {
        List<String> sections = TranslationsUtil.getSections(fullPath);

        LocalizedNode node = nodes;

        for(String section : sections) {
            LocalizedNode subNode = node.getChildren(section);

            if(subNode == null) {
                subNode = new LocalizedNode(section, new ArrayList<>());
                node.addChildren(subNode);
            }

            node = subNode;
        }

        return node;
    }

    public List<String> getFullKeys() {
        List<String> keys = new ArrayList<>();

        if(nodes.isLeaf()) { // Root has no children
            return keys;
        }

        for(LocalizedNode children : nodes.getChildren()) {
            keys.addAll(getFullKeys("", children));
        }

        return keys;
    }

    public List<String> getFullKeys(String parentFullPath, LocalizedNode localizedNode) {
        List<String> keys = new ArrayList<>();

        if(localizedNode.isLeaf()) {
            keys.add(parentFullPath + (parentFullPath.isEmpty() ? "" : ".") + localizedNode.getKey());
            return keys;
        }

        for(LocalizedNode children : localizedNode.getChildren()) {
            String childrenPath = parentFullPath + (parentFullPath.isEmpty() ? "" : ".") + localizedNode.getKey();
            keys.addAll(getFullKeys(childrenPath, children));
        }

        return keys;
    }
}