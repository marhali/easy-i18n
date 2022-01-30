package de.marhali.easyi18n.model;

/**
 * Represents all supported folder strategies.
 * @author marhali
 */
public enum FolderStrategy {
    SINGLE,
    MODULARIZED_LOCALE,
    MODULARIZED_NAMESPACE;

    public int toIndex() {
        int index = 0;

        for(FolderStrategy strategy : values()) {
            if(strategy == this) {
                return index;
            }

            index++;
        }

        throw new NullPointerException();
    }

    public static FolderStrategy fromIndex(int index) {
        return values()[index];
    }
}
