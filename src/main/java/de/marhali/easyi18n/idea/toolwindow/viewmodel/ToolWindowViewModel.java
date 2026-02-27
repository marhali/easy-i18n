package de.marhali.easyi18n.idea.toolwindow.viewmodel;

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.Consumer;
import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.application.query.ConfiguredModulesQuery;
import de.marhali.easyi18n.core.application.query.ModuleViewQuery;
import de.marhali.easyi18n.core.domain.event.DomainEvent;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.event.ProjectConfigChanged;
import de.marhali.easyi18n.core.domain.event.ProjectReloaded;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.event.PluginTopics;
import de.marhali.easyi18n.idea.key.PluginKey;
import de.marhali.easyi18n.idea.notification.ToolWindowNotificationHelper;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.PluginExecutorService;
import de.marhali.easyi18n.idea.toolwindow.I18nToolWindowPanel;
import de.marhali.easyi18n.idea.toolwindow.I18nToolWindowState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Translations tool window view model.
 *
 * @author marhali
 */
public final class ToolWindowViewModel implements PluginTopics.DomainListener {

    private static final @NotNull Logger LOGGER = Logger.getInstance(ToolWindowViewModel.class);

    private final @NotNull ToolWindow toolWindow;
    private final @NotNull Project project;
    private final @NotNull I18nProjectService projectService;
    private final @NotNull PluginExecutorService executorService;
    private final @NotNull ContentManager contentManager;
    private final @NotNull I18nToolWindowState state;
    private final @NotNull Map<ModuleId, ViewListener> listeners;

    public ToolWindowViewModel(
        @NotNull ToolWindow toolWindow,
        @NotNull Project project,
        @NotNull I18nProjectService projectService,
        @NotNull PluginExecutorService executorService,
        @NotNull ContentManager contentManager
    ) {
        this.toolWindow = toolWindow;
        this.project = project;
        this.projectService = projectService;
        this.executorService = executorService;
        this.contentManager = contentManager;
        this.state = I18nToolWindowState.fromDefaultState();
        this.listeners = new HashMap<>();
    }

    public void registerListener(@NotNull ModuleId moduleId, @NotNull ViewListener listener) {
        listeners.put(moduleId, listener);
    }

    public @Nullable ModuleId getSelectedModuleId() {
        return state.selectedModuleId;
    }

    public void setSelectedModuleId(@NotNull ModuleId moduleId) {
        mutateState(state -> state.selectedModuleId = moduleId);
        dispatchEvent(moduleId, ViewListener::onFocusView);
    }

    public boolean isFilterByMissingValues() {
        return Boolean.TRUE.equals(state.filterByMissingValues);
    }

    public void setFilterByMissingValues(@NotNull Boolean filterByMissingValues) {
        mutateStateAndInvalidate(state -> state.filterByMissingValues = filterByMissingValues);
    }

    public boolean isFilterByDuplicateValues() {
        return Boolean.TRUE.equals(state.filterByDuplicateValues);
    }

    public void setFilterByDuplicateValues(@NotNull Boolean filterByDuplicateValues) {
        mutateStateAndInvalidate(state -> state.filterByDuplicateValues = filterByDuplicateValues);
    }

    public boolean isShowAsTree() {
        return Boolean.TRUE.equals(state.showAsTree);
    }

    public void setShowAsTree(boolean showAsTree) {
        mutateStateAndInvalidate(state -> state.showAsTree = showAsTree);
    }

    public void setFilterBySearchQuery(@NotNull String filterBySearchQuery) {
        mutateStateAndInvalidate(state -> state.filterBySearchQuery = filterBySearchQuery);
    }

    public void reloadModules() {
        executorService.runAsync(
            () -> projectService.query(new ConfiguredModulesQuery()),
            this::rebuildModuleTabs,
            this::handleThrowable,
            ModalityState.any(),
            (o) -> contentManager.isDisposed()
        );
    }

    public void reloadModule(@NotNull ModuleId moduleId, @Nullable I18nKey key) {
        var options = state.toModuleViewOptions();
        var query = new ModuleViewQuery(moduleId, options);

        executorService.runAsync(
            () -> projectService.query(query),
            (moduleView) ->
                dispatchEvent(moduleId, listener -> listener.onViewUpdated(moduleView, key)),
            this::handleThrowable,
            ModalityState.any(),
            (o) -> contentManager.isDisposed()
        );
    }

    public void handleCommandAsync(@NotNull Command command) {
        I18nProjectService projectService = project.getService(I18nProjectService.class);
        PluginExecutorService executorService = project.getService(PluginExecutorService.class);

        executorService.runAsync(
            () -> {
                projectService.command(command);
                return null;
            },
            (_void) -> {}, // We expect happy path here
            this::handleThrowable,
            ModalityState.any(),
            (o) -> contentManager.isDisposed()
        );
    }

    private void rebuildModuleTabs(@NotNull Set<ModuleId> moduleIds) {
        // Cleanup everything
        contentManager.removeAllContents(true);
        listeners.clear();
        mutateState(state -> state.selectedModuleId = null);

        // Register a tool window panel for every module
        for (ModuleId moduleId : moduleIds) {
            I18nToolWindowPanel modulePanel = new I18nToolWindowPanel(project, moduleId, this);
            Content moduleContent = ContentFactory.getInstance()
                .createContent(modulePanel.getComponent(), moduleId.name(), false);

            moduleContent.putUserData(PluginKey.MODULE_ID, moduleId);
            moduleContent.setPreferredFocusableComponent(modulePanel);
            moduleContent.setDisposer(modulePanel);
            contentManager.addContent(moduleContent);
        }
    }

    @Override
    public void onDomainEvent(@NotNull DomainEvent event) {
        switch (event) {
            case ProjectConfigChanged() -> reloadModules();
            case ModuleChanged(ModuleId moduleId, I18nKey key) -> reloadModule(moduleId, key);
            case ProjectReloaded() -> reloadModules();
            default -> {} // We do not need to handle every event here
        }
    }

    private void dispatchEvent(@NotNull ModuleId moduleId, @NotNull Consumer<@NotNull ViewListener> consumer) {
        consumer.accept(listeners.get(moduleId));
    }

    private void mutateState(@NotNull Consumer<I18nToolWindowState> consumer) {
        consumer.accept(state);
    }

    private void mutateStateAndInvalidate(@NotNull Consumer<I18nToolWindowState> consumer) {
        mutateState(consumer);

        if (state.selectedModuleId != null) {
            reloadModule(state.selectedModuleId, null);
        }

        // Invalidate all other modules
        listeners.entrySet().stream()
            .filter(entry -> entry.getKey() != state.selectedModuleId)
            .forEach(entry -> entry.getValue().onViewInvalidated());
    }

    private void handleThrowable(@NotNull Throwable throwable) {
        ToolWindowNotificationHelper.showNotificationForThrowable(project, toolWindow.getId(), throwable);
    }
}
