package de.marhali.easyi18n.idea.toolwindow.ui.table;

import com.intellij.ui.JBColor;
import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Table model for rendering {@link ModuleView.Table}.
 *
 * @author marhali
 */
public class I18nTableViewModel extends AbstractTableModel {

    public record ValueUpdate(
        @NotNull String value,
        @NotNull Integer row,
        @NotNull Integer column
    ) {}

    private final @NotNull List<@NotNull LocaleId> localeIds;
    private final @NotNull List<ModuleView.Table.@NotNull Row> rows;
    private final @NotNull Consumer<@NotNull ValueUpdate> onHandleUpdate;

    public I18nTableViewModel(@NotNull ModuleView.Table view, @NotNull Consumer<@NotNull ValueUpdate> onHandleUpdate) {
        this.localeIds = view.locales();
        this.rows = view.rows();
        this.onHandleUpdate = onHandleUpdate;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return localeIds.size() + 1; // Number of locales + 1 (I18nKey column)
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Translation key";
        }
        return localeIds.get(column - 1).tag();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; // Everything except the header is editable
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var row = rows.get(rowIndex);

        if (columnIndex == 0) {
            return row.key().canonical();
        }

        LocaleId localeId = localeIds.get(columnIndex - 1);

        if (row.cells().containsKey(localeId)) {
            var cell = row.cells().get(localeId);
            return cell.value() != null ? cell.value().toInputString() : null;
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // Only process update if value has been actually changed in comparison to previous value
        if (!Objects.equals(getValueAt(rowIndex, columnIndex), aValue)) {
            onHandleUpdate.accept(new ValueUpdate((String) aValue, rowIndex, columnIndex));
        }
    }

    public @Nullable Color getForeground(int rowIndex, int columnIndex) {
        ModuleView.Table.Row row = rows.get(rowIndex);
        if (columnIndex == 0) { // Key columns
            return row.missingValues() ? JBColor.RED : null;
        } else { // Value by localeId column
            var localeId = localeIds.get(columnIndex - 1);
            return row.cells().get(localeId).duplicate() ? JBColor.ORANGE : JBColor.foreground();
        }
    }

    public @NotNull I18nKey getKeyAtRow(int row) {
        return rows.get(row).key();
    }

    public @NotNull LocaleId getLocaleAtColumn(int column) {
        return localeIds.get(column - 1);
    }
}
