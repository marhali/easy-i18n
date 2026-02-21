package de.marhali.easyi18n.idea.toolwindow.ui;

import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Table cell renderer with coloration support.
 *
 * @author marhalu
 */
public class TableCellRenderer extends DefaultTableCellRenderer {

    private static final @Nullable Border DEFAULT_BORDER = JBUI.Borders.empty(8);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        var component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Always reset custom colors to prevent weird color rendering
        setForeground(null);

        if (!isSelected && !hasFocus) {
            var foreground = ((I18nTableViewModel) table.getModel()).getForeground(row, column);

            if (foreground != null) {
                setForeground(foreground);
            }
        }

        setBorder(DEFAULT_BORDER);

        return component;
    }
}
