package de.marhali.easyi18n.tabs;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import de.marhali.easyi18n.service.LegacyDataStore;
import de.marhali.easyi18n.model.LocalizedNode;
import de.marhali.easyi18n.model.DataSynchronizer;
import de.marhali.easyi18n.model.Translations;
import de.marhali.easyi18n.model.KeyedTranslation;
import de.marhali.easyi18n.model.TranslationDelete;
import de.marhali.easyi18n.model.table.TableModelTranslator;
import de.marhali.easyi18n.dialog.EditDialog;
import de.marhali.easyi18n.listener.DeleteKeyListener;
import de.marhali.easyi18n.listener.PopupClickListener;
import de.marhali.easyi18n.renderer.TableRenderer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

/**
 * Shows translation state as table.
 * @author marhali
 */
public class TableView implements DataSynchronizer {

    private final Project project;

    private JPanel rootPanel;
    private JPanel containerPanel;

    private JBTable table;

    public TableView(Project project) {
        this.project = project;

        table = new JBTable();
        table.getEmptyText().setText(ResourceBundle.getBundle("messages").getString("view.empty"));
        table.addMouseListener(new PopupClickListener(this::handlePopup));
        table.addKeyListener(new DeleteKeyListener(handleDeleteKey()));
        table.setDefaultRenderer(String.class, new TableRenderer());

        containerPanel.add(new JBScrollPane(table));
    }

    private void handlePopup(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());

        if(row >= 0) {
            String fullPath = String.valueOf(table.getValueAt(row, 0));
            LocalizedNode node = LegacyDataStore.getInstance(project).getTranslations().getNode(fullPath);

            if(node != null) {
                new EditDialog(project, new KeyedTranslation(fullPath, node.getValue())).showAndHandle();
            }
        }
    }

    private Runnable handleDeleteKey() {
        return () -> {
            for (int selectedRow : table.getSelectedRows()) {
                String fullPath = String.valueOf(table.getValueAt(selectedRow, 0));

                LegacyDataStore.getInstance(project).processUpdate(
                        new TranslationDelete(new KeyedTranslation(fullPath, null)));
            }
        };
    }

    @Override
    public void synchronize(@NotNull Translations translations,
                            @Nullable String searchQuery, @Nullable String scrollTo) {

        table.setModel(new TableModelTranslator(translations, searchQuery, update ->
                LegacyDataStore.getInstance(project).processUpdate(update)));

        if(scrollTo != null) {
            int row = -1;

            for (int i = 0; i < table.getRowCount(); i++) {
                if (String.valueOf(table.getValueAt(i, 0)).equals(scrollTo)) {
                    row = i;
                }
            }

            if (row > -1) { // Matched @scrollTo
                table.scrollRectToVisible(
                        new Rectangle(0, (row * table.getRowHeight()) + table.getHeight(), 0, 0));
            }
        }
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JBTable getTable() {
        return table;
    }
}