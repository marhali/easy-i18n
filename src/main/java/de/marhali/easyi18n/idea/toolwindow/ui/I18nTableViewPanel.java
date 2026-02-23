package de.marhali.easyi18n.idea.toolwindow.ui;

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import de.marhali.easyi18n.core.application.command.RemoveI18nValueCommand;
import de.marhali.easyi18n.core.application.command.UpdateI18nKeyCommand;
import de.marhali.easyi18n.core.application.command.UpdateI18nValueCommand;
import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.dialog.TranslationDialogFactory;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.PluginExecutorService;
import de.marhali.easyi18n.idea.toolwindow.listener.PopupClickListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Table view panel for the translations tool window content tab.
 *
 * @author marhali
 */
public class I18nTableViewPanel implements ViewPanel<ModuleView.Table> {

    private final @NotNull Project project;
    private final @NotNull ModuleId moduleId;

    private final @NotNull JBTable table;
    private final @NotNull JBScrollPane tableScrollPane;

    public I18nTableViewPanel(@NotNull Project project, @NotNull ModuleId moduleId) {
        this.project = project;
        this.moduleId = moduleId;

        this.table = new JBTable();
        this.tableScrollPane = new JBScrollPane(table);

        table.setShowGrid(false);
        table.setIntercellSpacing(JBUI.emptySize());
        table.getTableHeader().setFont(JBUI.Fonts.label().asBold());
        table.setRowHeight(JBUI.scale(28));
        table.setDefaultRenderer(Object.class, new TableCellRenderer());
        table.addMouseListener(new PopupClickListener((_e) -> showRowAsDialog(table.getSelectedRow())));
    }

    public @NotNull JComponent getComponent() {
        return this.tableScrollPane;
    }

    @Override
    public void setView(@NotNull ModuleView.Table view) {
        table.setModel(new I18nTableViewModel(view, this::handleValueUpdate));
    }

    @Override
    public @Nullable JComponent getToolbar() {
        return null;
    }

    private @NotNull I18nTableViewModel getModel() {
        return (I18nTableViewModel) table.getModel();
    }

    private void showRowAsDialog(int row) {
        if (row < 0) {
            // Invalid row
            return;
        }

        I18nKey key = getModel().getKeyAtRow(row);

        TranslationDialogFactory.createEditDialog(
            project,
            moduleId,
            key,
            DialogWrapper::show
        );
    }

    private void handleValueUpdate(I18nTableViewModel.@NotNull ValueUpdate update) {
        var key = getModel().getKeyAtRow(update.row());

        if (update.column() == 0) { // Key cell
            if (!update.value().isBlank()) {
                handleValueUpdateAsync(new UpdateI18nKeyCommand(moduleId, key, I18nKey.of(update.value())));
            }
        } else {
            var localeId = getModel().getLocaleAtColumn(update.column());

            if (update.value().isBlank()) { // Remove value
                handleValueUpdateAsync(new RemoveI18nValueCommand(moduleId, key, localeId));
            } else { // Update value
                handleValueUpdateAsync(new UpdateI18nValueCommand(moduleId, key, localeId, I18nValue.fromInputString(update.value())));
            }
        }
    }

    private void handleValueUpdateAsync(@NotNull Command command) {
        I18nProjectService projectService = project.getService(I18nProjectService.class);
        PluginExecutorService executorService = project.getService(PluginExecutorService.class);

        executorService.runAsync(
            () -> {
                projectService.command(command);
                return null;
            },
            (_void) -> {}, // We expect happy path here
            (throwable) -> throwable.printStackTrace(), // TODO: ex handling
            ModalityState.stateForComponent(table),
            project.getDisposed()
        );
    }
}
