package de.marhali.easyi18n.idea.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.event.ProjectConfigChanged;
import de.marhali.easyi18n.core.domain.event.ProjectReloaded;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.event.PluginTopics;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple modification tracker that increments when state changing domain-level events occur.
 *
 * @author marhali
 */
@Service(Service.Level.PROJECT)
public final class ModuleModificationTracker implements Disposable {

    private final @NotNull Map<@NotNull ModuleId, @NotNull SimpleModificationTracker> trackers;

    public ModuleModificationTracker(@NotNull Project project) {
        this.trackers = new ConcurrentHashMap<>();

        project.getMessageBus().connect(this).subscribe(PluginTopics.DOMAIN_EVENTS,
            (PluginTopics.DomainListener) event -> {
                switch (event) {
                    case ProjectConfigChanged ignored -> {
                        // If the configuration changes, some modules might also disappear
                        trackers.forEach((_moduleId, tracker) -> tracker.incModificationCount());
                        trackers.clear();
                    }
                    case ProjectReloaded ignored -> trackers.forEach((_moduleId, tracker) -> tracker.incModificationCount());
                    case ModuleChanged moduleChanged -> get(moduleChanged.moduleId()).incModificationCount();
                    default -> {} // We do not need to handle every event here
                }
        });
    }

    @Override
    public void dispose() {
        // Needed to connect to message bus
    }

    public @NotNull SimpleModificationTracker get(@NotNull ModuleId moduleId) {
        return trackers.computeIfAbsent(moduleId, (_moduleId) -> new SimpleModificationTracker());
    }
}
