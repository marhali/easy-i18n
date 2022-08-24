package de.marhali.easyi18n.tabs.renderer;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Similar to {@link DefaultTableCellRenderer} but will mark the first column red if any column is empty.
 * @author marhali
 */
public class TableRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Always reset color
        component.setForeground(null);

        if(column != 0) {
            return component;
        }

        if(missesValues(row, table)) {
            component.setForeground(JBColor.RED);
        } else if(hasDuplicates(row, table)) {
            component.setForeground(JBColor.ORANGE);
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

    private boolean hasDuplicates(int checkRow, JTable table) {
        int columns = table.getColumnCount();
        int rows = table.getRowCount();

        Set<String> contents = new HashSet<>();
        for(int column = 1; column < columns; column++) {
            contents.add(String.valueOf(table.getValueAt(checkRow, column)));
        }

        for(int row = 1; row < rows; row++) {
            if(row == checkRow) {
                continue;
            }

            for(int column = 1; column < columns; column++) {
                if(contents.contains(String.valueOf(table.getValueAt(row, column)))) {
                    return true;
                }
            }
        }

        return false;
    }
}