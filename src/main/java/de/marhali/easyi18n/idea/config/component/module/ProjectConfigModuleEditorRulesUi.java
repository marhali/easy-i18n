package de.marhali.easyi18n.idea.config.component.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModuleBuilder;
import de.marhali.easyi18n.core.domain.rules.*;
import de.marhali.easyi18n.idea.config.component.ConfigComponent;
import de.marhali.easyi18n.idea.config.component.module.rules.*;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.Objects;

/**
 * @author marhali
 */
public class ProjectConfigModuleEditorRulesUi
    extends ConfigComponent<FormBuilder, ProjectConfigModule, ProjectConfigModuleBuilder> {

    private @Nullable EditorRuleTableModel editorRuleTableModel;
    private @Nullable EditorRuleConstraintTableModel editorRuleConstraintTableModel;
    private @Nullable JBTable rulesTable;
    private @Nullable JBTable ruleConstraintsTable;

    protected ProjectConfigModuleEditorRulesUi(@NotNull Project project) {
        super(project);
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        // Table models
        editorRuleTableModel = new EditorRuleTableModel();
        editorRuleConstraintTableModel = new EditorRuleConstraintTableModel(
            (change) -> editorRuleTableModel.setRuleConstraintsAtRow(change.ruleRowIndex(), change.constraints())
        );

        // Rules Table setup
        rulesTable = createTable(editorRuleTableModel);
        installRulesTableStatusText(rulesTable,
            () -> addNewRule(rulesTable, editorRuleTableModel, EditorRule.fromDefaultPreset()));
        installRulesTableColumnModel(rulesTable);

        // Rule constraints Table setup
        ruleConstraintsTable = createTable(editorRuleConstraintTableModel);
        installRuleConstraintsUnselectedStatusText(ruleConstraintsTable);
        installRuleConstraintsColumnModel(ruleConstraintsTable);

        // Switch constraints when a new rule is selected (1 rule -> N constraints)
        rulesTable.getSelectionModel().addListSelectionListener((event) -> {
            if (!event.getValueIsAdjusting()) {
                var selectedRow = rulesTable.getSelectedRow();
                if (selectedRow > -1) {
                    var selectedRule = editorRuleTableModel.getRuleAtArow(selectedRow);
                    editorRuleConstraintTableModel.setConstraints(selectedRow, selectedRule.constraints());
                    installRuleConstraintsEmptyStatusText(ruleConstraintsTable,
                        () -> addConstraintForSelectedRule(rulesTable, editorRuleTableModel,
                            editorRuleConstraintTableModel, EditorRuleConstraint.fromDefaultPreset()));
                } else {
                    editorRuleConstraintTableModel.clearConstraints();
                    installRuleConstraintsUnselectedStatusText(ruleConstraintsTable);
                }
            }
        });

        // Rules Panel with Toolbar Actions
        JPanel rulesPanel = ToolbarDecorator.createDecorator(rulesTable)
            .setAddAction(anActionButton ->
                addNewRule(rulesTable, editorRuleTableModel, EditorRule.fromDefaultPreset()))
            .setRemoveAction(anActionButton ->
                removeSelectedRule(rulesTable, editorRuleTableModel, editorRuleConstraintTableModel))
            .createPanel();

        // Rule Constraints Panel with Toolbar Actions
        JPanel constraintsPanel = ToolbarDecorator.createDecorator(ruleConstraintsTable)
            .setAddAction(anActionButton ->
                addConstraintForSelectedRule(rulesTable, editorRuleTableModel, editorRuleConstraintTableModel,
                    EditorRuleConstraint.fromDefaultPreset()))
            .setRemoveAction(anActionButton ->
                removeSelectedConstraint(ruleConstraintsTable, editorRuleConstraintTableModel))
            .createPanel();

        // Place tables in a split view
        JBSplitter splitter = new JBSplitter();
        splitter.setFirstComponent(rulesPanel);
        splitter.setSecondComponent(constraintsPanel);
        splitter.setBorder(JBUI.Borders.emptyTop(4));

        builder.addLabeledComponent(PluginBundle.message("config.project.modules.item.editor.rules.label"), splitter, true);
    }

    @Override
    public boolean isModified(@NotNull ProjectConfigModule originState) {
        Objects.requireNonNull(editorRuleTableModel, "editorRuleTableModel must not be null");

        return !originState.editorRules().equals(editorRuleTableModel.getRules());
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfigModule state) {
        Objects.requireNonNull(editorRuleTableModel, "editorRuleTableModel must not be null");
        Objects.requireNonNull(editorRuleConstraintTableModel, "editorRuleConstraintTableModel must not be null");

        editorRuleTableModel.setRules(state.editorRules());
        editorRuleConstraintTableModel.clearConstraints(); // Reset constraints as well
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigModuleBuilder builder) {
        Objects.requireNonNull(editorRuleTableModel, "editorRuleTableModel must not be null");

        builder.editorRules(editorRuleTableModel.getRules());
    }

    private @NotNull JBTable createTable(@NotNull TableModel tableModel) {
        var table = new JBTable(tableModel);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(JBUI.emptySize());
        table.getTableHeader().setFont(JBUI.Fonts.label().asBold());
        table.setRowHeight(JBUI.scale(28));

        return table;
    }

    private static void removeSelectedConstraint(@NotNull JBTable table, @NotNull EditorRuleConstraintTableModel constraintTableModel) {
        var selectedRow = table.getSelectedRow();
        if (selectedRow > -1) {
            constraintTableModel.removeConstraintAtRow(selectedRow);
        }
    }

    private static void addConstraintForSelectedRule(
        @NotNull JBTable table, @NotNull EditorRuleTableModel ruleTableModel,
        @NotNull EditorRuleConstraintTableModel constraintTableModel, @NotNull EditorRuleConstraint newConstraint) {
        var selectedRuleRow = table.getSelectedRow();
        ruleTableModel.addConstraintToRow(selectedRuleRow, newConstraint);
        constraintTableModel.setConstraints(selectedRuleRow, ruleTableModel.getRuleAtArow(selectedRuleRow).constraints());
    }

    private static void addNewRule(@NotNull JBTable table, @NotNull EditorRuleTableModel tableModel,
                                   @NotNull EditorRule newRule) {
        tableModel.addRule(newRule);
        var newRowIndex = tableModel.getRowCount() - 1;
        table.getSelectionModel().setSelectionInterval(newRowIndex, newRowIndex);
    }

    private static void removeSelectedRule(@NotNull JBTable table, @NotNull EditorRuleTableModel ruleTableModel,
                                           @NotNull EditorRuleConstraintTableModel constraintTableModel) {
        var selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            ruleTableModel.removeRuleAtRow(selectedRow);
            constraintTableModel.clearConstraints();
        }
    }

    private static void installRulesTableColumnModel(@NotNull JBTable table) {
        // EditorLanguage renderer
        EnumSetCellRenderer<EditorLanguage> editorLanguageRenderer = new EnumSetCellRenderer<>(Enum::toString, ", ");
        table.getColumnModel().getColumn(EditorRuleTableModel.COLUMN_LANGUAGES).setCellRenderer(editorLanguageRenderer);

        // EditorLanguage editor
        EnumSetCellEditor<EditorLanguage> editorLanguagePopupEditor = new EnumSetCellEditor<>(EditorLanguage.class, Enum::toString, ", ");
        table.getColumnModel().getColumn(EditorRuleTableModel.COLUMN_LANGUAGES).setCellEditor(editorLanguagePopupEditor);

        // TriggerKind editor
        ComboBox<TriggerKind> triggerKindComboBox = new ComboBox<>(TriggerKind.values());
        DefaultCellEditor triggerKindComboBoxEditor = new DefaultCellEditor(triggerKindComboBox);
        triggerKindComboBoxEditor.setClickCountToStart(2);
        table.getColumnModel().getColumn(EditorRuleTableModel.COLUMN_TRIGGER_KIND).setCellEditor(triggerKindComboBoxEditor);

        // Rule exclude editor
        DefaultCellEditor excludeRuleCheckBoxEditor = new DefaultCellEditor(new JBCheckBox());
        excludeRuleCheckBoxEditor.setClickCountToStart(2);
        table.getColumnModel().getColumn(EditorRuleTableModel.COLUMN_EXCLUDE_RULE).setCellEditor(excludeRuleCheckBoxEditor);
    }

    private static void installRuleConstraintsColumnModel(@NotNull JBTable table) {
        // RuleConstraintType cell editor
        ComboBox<RuleConstraintType> ruleConstraintTypeComboBox = new ComboBox<>(RuleConstraintType.values());
        DefaultCellEditor ruleConstraintTypeComboBoxEditor = new DefaultCellEditor(ruleConstraintTypeComboBox);
        ruleConstraintTypeComboBoxEditor.setClickCountToStart(2);
        table.getColumnModel().getColumn(EditorRuleConstraintTableModel.COLUMN_TYPE).setCellEditor(ruleConstraintTypeComboBoxEditor);

        // TextMatchMode cell editor
        ComboBox<TextMatchMode> textMatchModeComboBox = new ComboBox<>(TextMatchMode.values());
        DefaultCellEditor textMatchModeComboBoxEditor = new DefaultCellEditor(textMatchModeComboBox);
        textMatchModeComboBoxEditor.setClickCountToStart(2);
        table.getColumnModel().getColumn(EditorRuleConstraintTableModel.COLUMN_MATCH_MODE).setCellEditor(textMatchModeComboBoxEditor);

        // Negated cell editor
        DefaultCellEditor negateCheckBoxEditor = new DefaultCellEditor(new JBCheckBox());
        negateCheckBoxEditor.setClickCountToStart(2);
        table.getColumnModel().getColumn(EditorRuleConstraintTableModel.COLUMN_NEGATE).setCellEditor(negateCheckBoxEditor);
    }

    private static void installRulesTableStatusText(@NotNull JBTable table, @NotNull Runnable onAddRule) {
        table.getEmptyText().appendLine(PluginBundle.message("config.project.modules.item.editor.rules.empty.reason"));
        table.getEmptyText().appendLine(PluginBundle.message("config.project.modules.item.editor.rules.empty.action"),
            SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES, (e) -> onAddRule.run());
    }

    private static void installRuleConstraintsUnselectedStatusText(@NotNull JBTable table) {
        table.getEmptyText().clear();
        table.getEmptyText().appendLine(PluginBundle.message("config.project.modules.item.editor.constraints.empty.unselected.reason"));
        table.getEmptyText().appendLine(PluginBundle.message("config.project.modules.item.editor.constraints.empty.unselected.action",
            SimpleTextAttributes.REGULAR_ITALIC_ATTRIBUTES));
    }

    private static void installRuleConstraintsEmptyStatusText(@NotNull JBTable table, @NotNull Runnable onAddConstraint) {
        table.getEmptyText().clear();
        table.getEmptyText().appendLine(PluginBundle.message("config.project.modules.item.editor.constraints.empty.reason"));
        table.getEmptyText().appendLine(PluginBundle.message("config.project.modules.item.editor.constraints.empty.action"),
            SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES, (e) -> onAddConstraint.run());
    }
}
