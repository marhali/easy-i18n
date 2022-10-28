package de.marhali.easyi18n;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.model.action.TranslationUpdate;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Central singleton component for managing an easy-i18n instance for a specific project.
 * @author marhali
 */
public class InstanceManager {

    private static final Map<Project, InstanceManager> INSTANCES = new WeakHashMap<>();

    private final DataStore store;
    private final DataBus bus;
    private final FilteredDataBus uiBus;

    public static InstanceManager get(@NotNull Project project) {
        InstanceManager instance = INSTANCES.get(project);

        if(instance == null){
            instance = new InstanceManager(project);
            INSTANCES.put(project, instance);
        }

        return instance;
    }

    private InstanceManager(@NotNull Project project) {
        this.store = new DataStore(project);
        this.bus = new DataBus();
        this.uiBus = new FilteredDataBus(project);

        // Register ui eventbus on top of the normal eventbus
        this.bus.addListener(this.uiBus);

        // Load data after first initialization
        ApplicationManager.getApplication().invokeLater(() -> {
            this.store.loadFromPersistenceLayer((success) -> {
                this.bus.propagate().onUpdateData(this.store.getData());
            });
        });
    }

    public DataStore store() {
        return this.store;
    }

    /**
     * Primary eventbus.
     */
    public DataBus bus() {
        return this.bus;
    }

    /**
     * UI optimized eventbus with builtin filter logic.
     */
    public FilteredDataBus uiBus() {
        return this.uiBus;
    }

    /**
     * Reloads the plugin instance. Unsaved cached data will be deleted.
     * Fetches data from persistence layer and notifies all endpoints via {@link DataBus}.
     */
    public void reload() {
        ApplicationManager.getApplication().saveAll(); // Save opened files (required if new locales were added)
        store.loadFromPersistenceLayer((success) ->
                bus.propagate().onUpdateData(store.getData()));
    }

    public void processUpdate(TranslationUpdate update) {
        if(update.isDeletion() || update.isKeyChange()) { // Remove origin translation
            this.store.getData().setTranslation(update.getOrigin().getKey(), null);
        }

        if(!update.isDeletion()) { // Create or re-create translation with changed data
            this.store.getData().setTranslation(update.getChange().getKey(), update.getChange().getValue());
        }

        this.store.saveToPersistenceLayer(success -> {
            if(success) {
                this.bus.propagate().onUpdateData(this.store.getData());

                if(!update.isDeletion()) {
                    this.bus.propagate().onFocusKey(update.getChange().getKey());
                } else {
                    this.bus.propagate().onFocusKey(update.getOrigin().getKey());
                }
            }
        });
    }
}
