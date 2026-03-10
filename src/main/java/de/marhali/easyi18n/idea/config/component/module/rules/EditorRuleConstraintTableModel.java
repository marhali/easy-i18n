package de.marhali.easyi18n.idea.config.component.module.rules;

import de.marhali.easyi18n.core.domain.rules.EditorRuleConstraint;
import de.marhali.easyi18n.core.domain.rules.RuleConstraintType;
import de.marhali.easyi18n.core.domain.rules.TextMatchMode;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author marhali
 */
public class EditorRuleConstraintTableModel extends AbstractTableModel {

    public static final int COLUMN_TYPE = 0;
    public static final int COLUMN_VALUE = 1;
    public static final int COLUMN_MATCH_MODE = 2;
    public static final int COLUMN_NEGATE = 3;

    public record ConstraintsChanged(
        int ruleRowIndex,
        @NotNull List<@NotNull EditorRuleConstraint> constraints
    ) {}

    private final @NotNull Consumer<ConstraintsChanged> onChange;

    private @NotNull Integer ruleRowIndex;
    private @NotNull List<@NotNull EditorRuleConstraint> constraints;

    public EditorRuleConstraintTableModel(@NotNull Consumer<ConstraintsChanged> onChange) {
        this(onChange, List.of());
    }

    public EditorRuleConstraintTableModel(@NotNull Consumer<ConstraintsChanged> onChange, @NotNull List<EditorRuleConstraint> constraints) {
        this.onChange = onChange;

        this.ruleRowIndex = -1;
        this.constraints = new ArrayList<>(constraints);
    }

    public void setConstraints(int ruleRowIndex, @NotNull List<@NotNull EditorRuleConstraint> constraints) {
        this.ruleRowIndex = ruleRowIndex;
        this.constraints = new ArrayList<>(constraints);
        fireTableDataChanged();
    }

    public void clearConstraints() {
        this.ruleRowIndex = -1;
        this.constraints = new ArrayList<>();
    }

    public void removeConstraintAtRow(int row) {
        constraints.remove(row);
        fireTableDataChanged();
        onChange.accept(new ConstraintsChanged(ruleRowIndex, constraints));
    }

    @Override
    public int getRowCount() {
        return constraints.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case COLUMN_TYPE -> PluginBundle.message("config.project.modules.item.editor.constraints.column.type");
            case COLUMN_VALUE -> PluginBundle.message("config.project.modules.item.editor.constraints.column.value");
            case COLUMN_MATCH_MODE -> PluginBundle.message("config.project.modules.item.editor.constraints.column.match-mode");
            case COLUMN_NEGATE -> PluginBundle.message("config.project.modules.item.editor.constraints.column.negated");
            default -> throw new IllegalArgumentException("Unknown column index: " + column);
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case COLUMN_TYPE -> RuleConstraintType.class;
            case COLUMN_VALUE -> String.class;
            case COLUMN_MATCH_MODE -> TextMatchMode.class;
            case COLUMN_NEGATE -> Boolean.class;
            default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EditorRuleConstraint constraint = constraints.get(rowIndex);
        return switch (columnIndex) {
            case COLUMN_TYPE -> constraint.type();
            case COLUMN_VALUE -> constraint.value();
            case COLUMN_MATCH_MODE -> constraint.matchMode();
            case COLUMN_NEGATE -> constraint.negated();
            default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        EditorRuleConstraint constraint = constraints.get(rowIndex);
        switch (columnIndex) {
            case COLUMN_TYPE -> updateConstraintAtRow(rowIndex, constraint.withType((RuleConstraintType) value));
            case COLUMN_VALUE -> updateConstraintAtRow(rowIndex, constraint.withValue((String) value));
            case COLUMN_MATCH_MODE -> updateConstraintAtRow(rowIndex, constraint.withMatchMode((TextMatchMode) value));
            case COLUMN_NEGATE -> updateConstraintAtRow(rowIndex, constraint.withNegated((Boolean) value));
            default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        }
    }

    private void updateConstraintAtRow(int row, @NotNull EditorRuleConstraint constraint) {
        constraints.set(row, constraint);
        fireTableDataChanged();
        onChange.accept(new ConstraintsChanged(ruleRowIndex, constraints));
    }
}
