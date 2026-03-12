package de.marhali.easyi18n.idea.icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Plugin specific icons.
 *
 * @author marhali
 */
public interface PluginIcon {
    Icon TRANSLATE_ICON = IconLoader.getIcon("/icons/translate13.svg", PluginIcon.class);
    Icon SHOW_AS_TREE_ICON = IconLoader.getIcon("/icons/showAsTree.svg", PluginIcon.class);
}
