package de.marhali.easyi18n.idea.config.component.module.rules;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.Function;
import com.intellij.util.ui.AbstractTableCellEditor;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EnumSet;
import java.util.EventObject;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Table cell editor for {@link EnumSet} with multi value selection using checkboxes.
 *
 * @param <E> Enum class
 *
 * @author marhali
 */
public final class EnumSetCellEditor<E extends Enum<E>> extends AbstractTableCellEditor {
    private final @NotNull Class<E> enumType;
    private final @NotNull Function<E, String> labelProvider;
    private final @NotNull String labelDelimiter;
    private final @NotNull JPanel editor = new JPanel(new BorderLayout());
    private final @NotNull JLabel label = new JLabel();

    private @NotNull EnumSet<E> value;

    public EnumSetCellEditor(
        @NotNull Class<E> enumType,
        @NotNull Function<E, String> labelProvider,
        @NotNull String labelDelimiter
    ) {
        this.enumType = enumType;
        this.labelProvider = labelProvider;
        this.labelDelimiter = labelDelimiter;
        this.value = EnumSet.noneOf(enumType);

        editor.add(label, BorderLayout.CENTER);
        editor.setBorder(JBUI.Borders.empty(0, 6));
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent me) {
            // Require double-click for mouse events
            return me.getClickCount() >= 2;
        }

        // Or F2 as an alternative
        return !(e instanceof KeyEvent) || ((KeyEvent) e).getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    public Object getCellEditorValue() {
        return copyOf(value);
    }

    @Override
    public Component getTableCellEditorComponent(
        JTable table, Object rawValue, boolean isSelected, int row, int column
    ) {
        value = toEnumSet(rawValue);

        label.setText(renderText(value));
        editor.setBackground(table.getSelectionBackground());
        label.setForeground(table.getSelectionForeground());

        // Wait before open the popup
        SwingUtilities.invokeLater(() -> openPopup(table, row, column));

        return editor;
    }

    private String renderText(@NotNull Set<E> set) {
        return set.stream()
            .map(labelProvider)
            .collect(Collectors.joining(labelDelimiter));
    }

    private void openPopup(JTable table, int row, int column) {
        CheckBoxList<E> list = new CheckBoxList<>();
        for (E e : enumType.getEnumConstants()) {
            list.addItem(e, labelProvider.apply(e), value.contains(e));
        }

        JBPopup popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(new JBScrollPane(list), list)
            .setRequestFocus(true)
            .setCancelOnClickOutside(true)
            .setCancelOnWindowDeactivation(true)
            .setFocusable(true)
            .createPopup();

        popup.addListener(new JBPopupListener() {
            @Override
            public void beforeShown(@NotNull LightweightWindowEvent event) {
            }

            @Override
            public void onClosed(@NotNull LightweightWindowEvent event) {
                EnumSet<E> newValue = EnumSet.noneOf(enumType);
                newValue.addAll(list.getCheckedItems());
                value = newValue;
                stopCellEditing();
            }
        });

        Rectangle cell = table.getCellRect(row, column, true);
        popup.show(new RelativePoint(table, new Point(cell.x, cell.y + cell.height)));
    }

    @SuppressWarnings("unchecked")
    private EnumSet<E> toEnumSet(Object cellValue) {
        EnumSet<E> result = EnumSet.noneOf(enumType);
        if (cellValue instanceof Set<?> set) {
            for (Object o : set) {
                if (o != null) {
                    result.add((E)o);
                }
            }
        }
        return result;
    }

    private EnumSet<E> copyOf(Set<E> source) {
        if (source == null || source.isEmpty()) {
            return EnumSet.noneOf(enumType);
        }
        return EnumSet.copyOf(source);
    }
}
