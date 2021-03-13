package de.marhali.easyi18n.ui.table;

import org.jetbrains.annotations.Nls;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class CustomTableModel implements TableModel {


    @Override
    public int getRowCount() {
        return 2;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Nls
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "<html><b>key</b></html>";
            case 1:
                return "de";
            case 2:
                return "en";
        }

        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnIndex == 0 ? "key" : "val";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }
}
