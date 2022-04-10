package de.marhali.easyi18n.model;

import de.marhali.easyi18n.io.folder.FolderStrategy;
import de.marhali.easyi18n.io.folder.ModularLocaleFolderStrategy;
import de.marhali.easyi18n.io.folder.ModularNamespaceFolderStrategy;
import de.marhali.easyi18n.io.folder.SingleFolderStrategy;

/**
 * Represents all supported folder strategies.
 * @author marhali
 */
public enum FolderStrategyType {
    SINGLE(SingleFolderStrategy.class, false),
    MODULARIZED_LOCALE(ModularLocaleFolderStrategy.class, true),
    MODULARIZED_NAMESPACE(ModularNamespaceFolderStrategy.class, true);

    private final Class<? extends FolderStrategy> strategy;
    private final boolean namespaceMode;

    /**
     * @param strategy Strategy implementation
     * @param namespaceMode Does this strategy use namespaces?
     */
    FolderStrategyType(Class<? extends FolderStrategy> strategy, boolean namespaceMode) {
        this.strategy = strategy;
        this.namespaceMode = namespaceMode;
    }

    public Class<? extends FolderStrategy> getStrategy() {
        return strategy;
    }

    public int toIndex() {
        int index = 0;

        for(FolderStrategyType strategy : values()) {
            if(strategy == this) {
                return index;
            }

            index++;
        }

        throw new NullPointerException();
    }

    public boolean isNamespaceMode() {
        return namespaceMode;
    }

    public static FolderStrategyType fromIndex(int index) {
        return values()[index];
    }
}
