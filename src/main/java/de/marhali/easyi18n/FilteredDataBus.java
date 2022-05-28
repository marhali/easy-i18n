package de.marhali.easyi18n;

import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.model.*;
import de.marhali.easyi18n.model.bus.BusListener;
import de.marhali.easyi18n.model.bus.ExpandAllListener;
import de.marhali.easyi18n.model.bus.FilteredBusListener;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.settings.ProjectSettingsState;
import de.marhali.easyi18n.util.TranslationUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * UI related eventbus. Uses the {@link BusListener} by {@link DataBus} under the hood.
 * User-Interface components (e.g. tabs) use this component by implementing {@link FilteredBusListener}.
 * @author marhali
 */
public class FilteredDataBus implements BusListener {

    private final Project project;
    private final Set<FilteredBusListener> listener;

    private TranslationData data;
    private ProjectSettingsState settings;
    private boolean filterDuplicate;
    private boolean filterIncomplete;
    private String searchQuery;
    private KeyPath focusKey;

    /**
     * Constructs a new project specific UI eventbus.
     * @param project Associated project
     */
    public FilteredDataBus(@NotNull Project project) {
        this.project = project;
        this.listener = new HashSet<>();
    }

    /**
     * Adds a participant to the event bus. Every participant needs to be added manually.
     * @param listener Bus listener
     */
    public void addListener(FilteredBusListener listener) {
        this.listener.add(listener);
    }

    @Override
    public void onFilterDuplicate(boolean filter) {
        this.filterDuplicate = filter;
        this.processAndPropagate();
        fire(ExpandAllListener::onExpandAll);
    }

    @Override
    public void onFilterIncomplete(boolean filter) {
        this.filterIncomplete = filter;
        this.processAndPropagate();
        fire(ExpandAllListener::onExpandAll);
    }

    @Override
    public void onFocusKey(@NotNull KeyPath key) {
        this.focusKey = key;
        fire(li -> li.onFocusKey(key));
    }

    @Override
    public void onSearchQuery(@Nullable String query) {
        this.searchQuery = query == null ? null : query.toLowerCase();
        this.processAndPropagate();
        fire(ExpandAllListener::onExpandAll);
    }

    @Override
    public void onUpdateData(@NotNull TranslationData data) {
        this.data = data;
        this.settings = ProjectSettingsService.get(this.project).getState();
        processAndPropagate();
    }

    /**
     * Filter translations based on supplied filters and propagate changes
     * to all registered participants.
     * Internally creates a shallow copy of the cached translations and
     * removes any that does not apply with the configured filters.
     */
    private void processAndPropagate() {
        TranslationData shadow = new TranslationData(
                this.data.getLocales(), new TranslationNode(this.data.isSorting()));

        for (KeyPath key : this.data.getFullKeys()) {
            TranslationValue value = this.data.getTranslation(key);
            assert value != null;

            // We create a shallow copy of the current translation instance
            // and remove every translation that does not conform with the configured filter/s
            shadow.setTranslation(key, value);

            // filter incomplete translations
            if(filterIncomplete) {
                if(!TranslationUtil.isIncomplete(value, this.data)) {
                    shadow.setTranslation(key, null);
                    continue;
                }
            }

            // filter duplicate values
            if(filterDuplicate) {
                if(!TranslationUtil.hasDuplicates(new Translation(key, value), this.data)) {
                    shadow.setTranslation(key, null);
                    continue;
                }
            }

            // full-text-search
            if(searchQuery != null) {
                if(!TranslationUtil.isSearched(settings, new Translation(key, value), searchQuery)) {
                    shadow.setTranslation(key, null);
                    //continue;
                }
            }
        }

        fire(li -> {
            li.onUpdateData(shadow);

            if(focusKey != null) {
                li.onFocusKey(focusKey);
            }
        });
    }

    /**
     * @param li Notify all registered participants about an fired event.
     */
    private void fire(@NotNull Consumer<FilteredBusListener> li) {
        listener.forEach(li);
    }
}