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
    SINGLE(SingleFolderStrategy.class),
    MODULARIZED_LOCALE(ModularLocaleFolderStrategy.class),
    MODULARIZED_NAMESPACE(ModularNamespaceFolderStrategy.class);

    private final Class<? extends FolderStrategy> strategy;

    FolderStrategyType(Class<? extends FolderStrategy> strategy) {
        this.strategy = strategy;
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

    public static FolderStrategyType fromIndex(int index) {
        return values()[index];
    }
}
