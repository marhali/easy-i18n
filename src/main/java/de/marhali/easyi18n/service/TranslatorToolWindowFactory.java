package de.marhali.easyi18n.service;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import de.marhali.easyi18n.service.LegacyDataStore;
import de.marhali.easyi18n.service.WindowManager;
import de.marhali.easyi18n.action.*;
import de.marhali.easyi18n.tabs.TableView;
import de.marhali.easyi18n.tabs.TreeView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Tool window factory which will represent the entire ui for this plugin.
 * @author marhali
 */
public class TranslatorToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        // Translations tree view
        TreeView treeView = new TreeView(project);
        Content treeContent = contentFactory.createContent(treeView.getRootPanel(),
                ResourceBundle.getBundle("messages").getString("view.tree.title"), false);

        toolWindow.getContentManager().addContent(treeContent);

        // Translations table view
        TableView tableView = new TableView(project);
        Content tableContent = contentFactory.createContent(tableView.getRootPanel(),
                ResourceBundle.getBundle("messages").getString("view.table.title"), false);

        toolWindow.getContentManager().addContent(tableContent);

        // ToolWindow Actions (Can be used for every view)
        List<AnAction> actions = new ArrayList<>();
        actions.add(new AddAction());
        actions.add(new ReloadAction());
        actions.add(new SettingsAction());
        actions.add(new SearchAction((searchString) -> LegacyDataStore.getInstance(project).searchBeyKey(searchString)));
        toolWindow.setTitleActions(actions);

        // Initialize Window Manager
        WindowManager.getInstance().initialize(toolWindow, treeView, tableView);

        // Synchronize ui with underlying data
        LegacyDataStore store = LegacyDataStore.getInstance(project);
        store.addSynchronizer(treeView);
        store.addSynchronizer(tableView);
        store.synchronize(null, null);
    }
}