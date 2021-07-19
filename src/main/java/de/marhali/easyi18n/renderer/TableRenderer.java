package de.marhali.easyi18n.renderer;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Similar to {@link DefaultTableCellRenderer} but will mark the first column red if any column is empty.
 * @author marhali
 */
public class TableRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if(column == 0 && missesValues(row, table)) {
            component.setForeground(JBColor.RED);
        } else { // Reset color
            component.setForeground(null);
        }

        return component;
    }

    private boolean missesValues(int row, JTable table) {
        int columns = table.getColumnCount();

        for(int i = 1; i < columns; i++) {
            Object value = table.getValueAt(row, i);

            if(value == null || value.toString().isEmpty()) {
                return true;
            }
        }

        return false;
    }
}