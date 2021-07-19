package de.marhali.easyi18n.service;

import com.intellij.openapi.wm.ToolWindow;

import de.marhali.easyi18n.tabs.TableView;
import de.marhali.easyi18n.tabs.TreeView;

public class WindowManager {

    private static WindowManager INSTANCE;

    private ToolWindow toolWindow;
    private TreeView treeView;
    private TableView tableView;

    public static WindowManager getInstance() {
        return INSTANCE == null ? INSTANCE = new WindowManager() : INSTANCE;
    }

    private WindowManager() {}

    public void initialize(ToolWindow toolWindow, TreeView treeView, TableView tableView) {
        this.toolWindow = toolWindow;
        this.treeView = treeView;
        this.tableView = tableView;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public TreeView getTreeView() {
        return treeView;
    }

    public TableView getTableView() {
        return tableView;
    }
}