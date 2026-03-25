package de.marhali.easyi18n.idea.config.component.module.rules;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Table cell renderer for {@link java.util.EnumSet} values.
 *
 * @param <E> Enum class
 *
 * @author marhali
 */
public class EnumSetCellRenderer<E extends Enum<E>> extends DefaultTableCellRenderer {

    private final @NotNull Function<E, String> labelProvider;
    private final @NotNull String delimiter;

    public EnumSetCellRenderer(@NotNull Function<E, String> labelProvider, @NotNull String delimiter) {
        this.labelProvider = labelProvider;
        this.delimiter = delimiter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String text = "";
        if (value instanceof Set<?> set && !set.isEmpty()) {
            text = ((Set<E>) set).stream().map(labelProvider).collect(Collectors.joining(delimiter));
        }
        super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        return this;
    }
}
