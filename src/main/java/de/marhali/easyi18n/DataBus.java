package de.marhali.easyi18n;

import de.marhali.easyi18n.model.bus.BusListener;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.KeyPath;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Eventbus which is mandatory to distribute changes to the participating components.
 * For user interface related components the {@link FilteredDataBus} has a builtin solution to apply all relevant filters.
 * @author marhali
 */
public class DataBus {

    private final Set<BusListener> listener;

    protected DataBus() {
        this.listener = new HashSet<>();
    }

    /**
     * Adds a participant to the event bus. Every participant needs to be added manually.
     * @param listener Bus listener
     */
    public void addListener(BusListener listener) {
        this.listener.add(listener);
    }

    /**
     * Fires the called events on the returned prototype.
     * The event will be distributed to all participants which were registered at execution time.
     * @return Listener prototype
     */
    public BusListener propagate() {
        return new BusListener() {
            @Override
            public void onFilterDuplicate(boolean filter) {
                listener.forEach(li -> li.onFilterDuplicate(filter));
            }

            @Override
            public void onFilterIncomplete(boolean filter) {
                listener.forEach(li -> li.onFilterIncomplete(filter));
            }

            @Override
            public void onFocusKey(@NotNull KeyPath key) {
                listener.forEach(li -> li.onFocusKey(key));
            }

            @Override
            public void onSearchQuery(@Nullable String query) {
                listener.forEach(li -> li.onSearchQuery(query));
            }

            @Override
            public void onUpdateData(@NotNull TranslationData data) {
                listener.forEach(li -> li.onUpdateData(data));
            }
        };
    }
}