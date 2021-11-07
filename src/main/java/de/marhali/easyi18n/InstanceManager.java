package de.marhali.easyi18n;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.model.TranslationUpdate;

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

    public DataBus bus() {
        return this.bus;
    }

    public void processUpdate(TranslationUpdate update) {
        if(update.isDeletion() || update.isKeyChange()) { // Remove origin translation
            this.store.getData().setTranslation(update.getOrigin().getKey(), null);
        }

        if(!update.isDeletion()) { // Create or re-create translation with changed data
            this.store.getData().setTranslation(update.getChange().getKey(), update.getChange().getTranslation());
        }

        this.store.saveToPersistenceLayer(success -> {
            if(success) {
                this.bus.propagate().onUpdateData(this.store.getData());

                if(!update.isDeletion()) { // TODO: maybe focus parent if key was deleted
                    this.bus.propagate().onFocusKey(update.getChange().getKey());
                }
            }
        });
    }
}