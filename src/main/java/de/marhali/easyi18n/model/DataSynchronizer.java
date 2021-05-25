package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface to communicate data changes between data store and ui components.
 * @author marhali
 */
public interface DataSynchronizer {

    /**
     * Propagates data changes to implementation classes.
     * @param translations Updated translations model
     * @param searchQuery Can be used to filter visible data. Like a search function for the full key path
     * @param scrollToKey Focus specific translation. Can be null to disable this function
     */
    void synchronize(@NotNull Translations translations, @Nullable String searchQuery, @Nullable String scrollToKey);
}