package de.marhali.easyi18n.idea.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.SimpleTextAttributes;
import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.application.query.view.ModuleViewType;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import de.marhali.easyi18n.idea.toolwindow.ui.table.I18nTableViewPanel;
import de.marhali.easyi18n.idea.toolwindow.ui.tree.I18nTreeViewPanel;
import de.marhali.easyi18n.idea.toolwindow.viewmodel.ToolWindowViewModel;
import de.marhali.easyi18n.idea.toolwindow.viewmodel.ViewListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Content panel for a translations tool window tab. Each panel represents a module within the project.
 * Manages the underlying {@link I18nTableViewPanel} and {@link I18nTreeViewPanel}.
 *
 * @author marhali
 */
public class I18nToolWindowPanel extends SimpleToolWindowPanel implements ViewListener, Disposable {

    private static final @NotNull String CARD_TABLE = "table";
    private static final @NotNull String CARD_TREE = "tree";

    private final @NotNull ModuleId moduleId;
    private final @NotNull ToolWindowViewModel vm;

    private final @NotNull JPanel cardsPanel;
    private final @NotNull I18nTableViewPanel tablePanel;
    private final @NotNull I18nTreeViewPanel treePanel;

    private boolean viewInitialized;

    public I18nToolWindowPanel(@NotNull Project project, @NotNull ModuleId moduleId, @NotNull ToolWindowViewModel vm) {
        super(false, true);

        this.moduleId = moduleId;
        this.vm = vm;

        this.tablePanel = new I18nTableViewPanel(project, moduleId, vm::handleCommandAsync);
        this.treePanel = new I18nTreeViewPanel(project, moduleId, vm::handleCommandAsync);

        this.cardsPanel = new JPanel(new CardLayout());
        this.cardsPanel.add(this.tablePanel.getComponent(), CARD_TABLE);
        this.cardsPanel.add(this.treePanel.getComponent(), CARD_TREE);

        vm.registerListener(moduleId, this);

        setDefaultEmptyText();

        // Initialization state
        viewInitialized = false;
    }

    @Override
    public void onViewUpdated(@NotNull ModuleView moduleView, @Nullable I18nKey key) {
        switch (moduleView) {
            case ModuleView.Table tableView -> {
                tablePanel.setView(tableView, key);
                showViewPanel(ModuleViewType.TABLE);
            }
            case ModuleView.Tree treeView -> {
                treePanel.setView(treeView, key);
                showViewPanel(ModuleViewType.TREE);
            }
            default -> throw new IllegalArgumentException("Unknown module view: " + moduleView.getClass().getSimpleName());
        }

        viewInitialized = true;
        this.cardsPanel.setVisible(true);

        if (getContent() == null) {
            // First & only time to set content when a new module view arrives
            setContent(this.cardsPanel);
        }
    }

    @Override
    public void onViewError(Throwable throwable) {
        getEmptyText().clear();
        getEmptyText().appendText(PluginBundle.message("toolwindow.panel.empty.error.reason"));
        getEmptyText().appendLine(PluginBundle.message("toolwindow.panel.empty.error.action"),
            SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES, e -> vm.reloadModule(moduleId, null));
        getEmptyText().appendLine("");
        getEmptyText().appendLine(AllIcons.General.ContextHelp, PluginBundle.message("toolwindow.panel.empty.error.help"),
            SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES,
            (e) -> BrowserUtil.browse(PluginBundle.message("url.documentation.configuration")));
        this.cardsPanel.setVisible(false);
    }

    @Override
    public void onViewInvalidated() {
        viewInitialized = false;
    }

    @Override
    public void onFocusView() {
        if (!viewInitialized) {
            vm.reloadModule(moduleId, null);
        }
    }

    @Override
    public void dispose() {
        // There is currently no element to dispose here
    }

    private void showViewPanel(@NotNull ModuleViewType type) {
        CardLayout layout = (CardLayout) cardsPanel.getLayout();

        switch (type) {
            case TABLE -> {
                layout.show(cardsPanel, CARD_TABLE);
                setToolbar(tablePanel.getToolbar());
            }
            case TREE -> {
                layout.show(cardsPanel, CARD_TREE);
                setToolbar(treePanel.getToolbar());
            }
            default -> throw new IllegalArgumentException("Unknown view panel type: " + type);
        }
    }

    private void setDefaultEmptyText() {
        getEmptyText().clear();
        getEmptyText().appendText(PluginBundle.message("toolwindow.panel.empty.loading"));
    }
}
