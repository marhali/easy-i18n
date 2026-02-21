package de.marhali.easyi18n.idea.toolwindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.*;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.action.*;
import de.marhali.easyi18n.idea.event.PluginTopics;
import de.marhali.easyi18n.idea.key.PluginKey;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.PluginExecutorService;
import de.marhali.easyi18n.idea.toolwindow.viewmodel.ToolWindowViewModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Factory to create the translations tool window.
 *
 * @author marhali
 */
public final class I18nToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        I18nProjectService projectService = project.getService(I18nProjectService.class);
        PluginExecutorService executorService = project.getService(PluginExecutorService.class);
        ContentManager contentManager = toolWindow.getContentManager();
        ToolWindowViewModel vm = new ToolWindowViewModel(project, projectService, executorService, contentManager);

        // Place all tool window actions
        toolWindow.setTitleActions(List.of(
            new AddTranslationAction(vm::getSelectedModuleId),
            new FilterByMissingValuesAction(vm::isFilterByMissingValues, vm::setFilterByMissingValues),
            new FilterByDuplicateValuesAction(vm::isFilterByDuplicateValues, vm::setFilterByDuplicateValues),
            new ShowAsTreeToggleAction(vm::isShowAsTree, vm::setShowAsTree),
            new ReloadFromDiskAction(),
            new OpenProjectConfigAction(true),
            new FilterBySearchQueryAction(vm::setFilterBySearchQuery)
        ));

        //
        contentManager.addContentManagerListener(new ContentManagerListener() {
            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                Content content = event.getContent();
                ModuleId moduleId = content.getUserData(PluginKey.MODULE_ID);

                if (moduleId != null && !moduleId.equals(vm.getSelectedModuleId())) {
                    vm.setSelectedModuleId(moduleId);
                }
            }
        });

        // Connect to domain event publisher
        project.getMessageBus().connect(contentManager).subscribe(PluginTopics.DOMAIN_EVENTS, vm);

        // Initial request to load all content tabs (modules)
        vm.reloadModules();
    }
}
