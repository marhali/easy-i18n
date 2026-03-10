package de.marhali.easyi18n.idea.config.component.module.rules;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.core.domain.rules.EditorRule;
import de.marhali.easyi18n.core.domain.rules.EditorRuleConstraint;
import de.marhali.easyi18n.core.domain.rules.TriggerKind;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author marhali
 */
public class EditorRuleTableModel extends AbstractTableModel {

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_LANGUAGES = 1;
    public static final int COLUMN_TRIGGER_KIND = 2;
    public static final int COLUMN_PRIORITY = 3;
    public static final int COLUMN_EXCLUDE_RULE = 4;

    private @NotNull List<@NotNull EditorRule> rules;

    public EditorRuleTableModel() {
        this(List.of());
    }

    public EditorRuleTableModel(@NotNull List<EditorRule> rules) {
        setRules(rules);
    }

    public void setRules(@NotNull List<@NotNull EditorRule> rules) {
        this.rules = new ArrayList<>(rules);
        fireTableDataChanged();
    }

    public @NotNull List<@NotNull EditorRule> getRules() {
        return rules;
    }

    public void addRule(@NotNull EditorRule rule) {
        rules.add(rule);
        fireTableDataChanged();
    }

    public @NotNull EditorRule getRuleAtArow(int row) {
        return rules.get(row);
    }

    private void updateRuleAtRow(int row, @NotNull EditorRule rule) {
        rules.set(row, rule);
        fireTableDataChanged();
    }

    public void addConstraintToRow(int row, @NotNull EditorRuleConstraint constraint) {
        var rule = rules.get(row);
        rules.set(row, rule.withAddConstraint(constraint));
    }

    public void setRuleConstraintsAtRow(int row, @NotNull List<@NotNull EditorRuleConstraint> constraints) {
        var rule = rules.get(row);
        rules.set(row, rule.withConstraints(constraints));
    }

    public void removeRuleAtRow(int selectedRow) {
        rules.remove(selectedRow);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return rules.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case COLUMN_ID -> PluginBundle.message("config.project.modules.item.editor.rules.column.id");
            case COLUMN_LANGUAGES -> PluginBundle.message("config.project.modules.item.editor.rules.column.languages");
            case COLUMN_TRIGGER_KIND -> PluginBundle.message("config.project.modules.item.editor.rules.column.triggerKind");
            case COLUMN_PRIORITY-> PluginBundle.message("config.project.modules.item.editor.rules.column.priority");
            case COLUMN_EXCLUDE_RULE -> PluginBundle.message("config.project.modules.item.editor.rules.column.excludeRule");
            default -> throw new IllegalArgumentException("Unknown column index: " + column);
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case COLUMN_ID -> String.class;
            case COLUMN_LANGUAGES -> EnumSet.class;
            case COLUMN_TRIGGER_KIND -> TriggerKind.class;
            case COLUMN_PRIORITY -> Integer.class;
            case COLUMN_EXCLUDE_RULE -> Boolean.class;
            default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EditorRule rule = rules.get(rowIndex);
        return switch (columnIndex) {
            case COLUMN_ID -> rule.id();
            case COLUMN_LANGUAGES -> rule.languages();
            case COLUMN_TRIGGER_KIND -> rule.triggerKind();
            case COLUMN_PRIORITY -> rule.priority();
            case COLUMN_EXCLUDE_RULE -> rule.excludeRule();
            default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        EditorRule rule = rules.get(rowIndex);
        switch (columnIndex) {
            case 0 -> updateRuleAtRow(rowIndex, rule.withId((String) value));
            case COLUMN_LANGUAGES -> updateRuleAtRow(rowIndex, rule.withLanguages((Set<EditorLanguage>) value));
            case COLUMN_TRIGGER_KIND -> updateRuleAtRow(rowIndex, rule.withTriggerKind((TriggerKind) value));
            case COLUMN_PRIORITY -> updateRuleAtRow(rowIndex, rule.withPriority((Integer) value));
            case COLUMN_EXCLUDE_RULE -> updateRuleAtRow(rowIndex, rule.withExcludeRule((Boolean) value));
            default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        }
    }
}
