package de.marhali.easyi18n.tabs;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.listener.ReturnKeyListener;
import de.marhali.easyi18n.model.*;
import de.marhali.easyi18n.dialog.EditDialog;
import de.marhali.easyi18n.listener.DeleteKeyListener;
import de.marhali.easyi18n.listener.PopupClickListener;
import de.marhali.easyi18n.model.bus.BusListener;
import de.marhali.easyi18n.renderer.TableRenderer;
import de.marhali.easyi18n.tabs.mapper.TableModelMapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Shows translation state as table.
 *
 * @author marhali
 */
public class TableView implements BusListener {

    private final JBTable table;

    private final Project project;

    private TableModelMapper currentMapper;
    private KeyPathConverter converter;

    private JPanel rootPanel;
    private JPanel containerPanel;

    public TableView(Project project) {
        this.project = project;

        table = new JBTable();
        table.getEmptyText().setText(ResourceBundle.getBundle("messages").getString("view.empty"));
        table.addMouseListener(new PopupClickListener(e -> showEditPopup(table.rowAtPoint(e.getPoint()))));
        table.addKeyListener(new ReturnKeyListener(() -> showEditPopup(table.getSelectedRow())));
        table.addKeyListener(new DeleteKeyListener(this::deleteSelectedRows));
        table.setDefaultRenderer(String.class, new TableRenderer());

        containerPanel.add(new JBScrollPane(table));
    }

    private void showEditPopup(int row) {
        if (row < 0) {
            return;
        }

        KeyPath fullPath = this.converter.split(String.valueOf(this.table.getValueAt(row, 0)));
        Translation translation = InstanceManager.get(project).store().getData().getTranslation(fullPath);

        if (translation != null) {
            new EditDialog(project, new KeyedTranslation(fullPath, translation)).showAndHandle();
        }
    }

    private void deleteSelectedRows() {
        for (int selectedRow : table.getSelectedRows()) {
            KeyPath fullPath = this.converter.split(String.valueOf(table.getValueAt(selectedRow, 0)));

            InstanceManager.get(project).processUpdate(
                    new TranslationDelete(new KeyedTranslation(fullPath, null))
            );
        }
    }

    @Override
    public void onUpdateData(@NotNull TranslationData data) {
        this.converter = new KeyPathConverter(project);

        table.setModel(this.currentMapper = new TableModelMapper(data, this.converter, update ->
                InstanceManager.get(project).processUpdate(update)));
    }

    @Override
    public void onFocusKey(@NotNull KeyPath key) {
        String concatKey = this.converter.concat(key);
        int row = -1;

        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 0).equals(concatKey)) {
                row = i;
            }
        }

        if (row > -1) { // Matched @key
            table.getSelectionModel().setSelectionInterval(row, row);
            table.scrollRectToVisible(new Rectangle(table.getCellRect(row, 0, true)));
        }
    }

    @Override
    public void onSearchQuery(@Nullable String query) {
        if (this.currentMapper != null) {
            this.currentMapper.onSearchQuery(query);
            this.table.updateUI();
        }
    }

    @Override
    public void onFilterMissingTranslations(boolean filter) {
        if(this.currentMapper != null) {
            this.currentMapper.onFilterMissingTranslations(filter);
            this.table.updateUI();
        }
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JBTable getTable() {
        return table;
    }
}