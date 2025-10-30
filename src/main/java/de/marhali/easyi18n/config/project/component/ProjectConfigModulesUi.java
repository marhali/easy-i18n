package de.marhali.easyi18n.config.project.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TitledBorderWithMnemonic;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import de.marhali.easyi18n.config.ConfigComponent;
import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.config.project.component.module.ProjectConfigModuleUi;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Mandatory for rendering all {@link ProjectConfigModuleUi module} elements.
 * @author marhali
 */
public class ProjectConfigModulesUi extends BaseProjectConfigUi{

    private final List<ProjectConfigModuleUi> modules;
    private JPanel modulesPanel;

    private JBLabel emptyModulesLabel;

    protected ProjectConfigModulesUi(Project project) {
        super(project);

        this.modules = new ArrayList<>();
    }

    @Override
    public void buildComponent(FormBuilder formBuilder) {
        // Title
        formBuilder.addComponent(new TitledSeparator(i18n.getString("config.project.modules.title")));

        // Add new module
        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.add.label"), buildAddModulePanel(), 1, false);

        formBuilder.addVerticalGap(6);

        // Empty hint
        emptyModulesLabel = new JBLabel(i18n.getString("config.project.modules.empty"));
        emptyModulesLabel.setFont(JBFont.regular().asItalic());
        emptyModulesLabel.setForeground(UIUtil.getErrorForeground());
        formBuilder.addComponent(emptyModulesLabel);

        // Modules enumeration (items are rendered via #applyStateToComponent)
        modulesPanel = new JPanel();
        modulesPanel.setLayout(new BoxLayout(modulesPanel, BoxLayout.Y_AXIS));

        formBuilder.addComponent(modulesPanel, 1);

        // Keep Last
        updateUI();
    }

    @Override
    public boolean isModified() {
        return modules.size() != state.getModules().size() || modules.stream().anyMatch(ConfigComponent::isModified);
    }

    @Override
    public void applyChangesToState() {
        var modulesState = state.getModules();
        modulesState.clear(); // Erase previous modules state

        for (ProjectConfigModuleUi moduleUi : modules) {
            moduleUi.applyChangesToState();
            modulesState.add(moduleUi.state);
            System.out.println("persist state: " + moduleUi.state);
        }
    }

    @Override
    public void applyStateToComponent(ProjectConfig state) {
        super.applyStateToComponent(state);

        // Erase UI state
        modules.clear();
        modulesPanel.removeAll();

        for (ProjectConfigModule moduleState : state.getModules()) {
            var moduleUi = new ProjectConfigModuleUi(project, moduleState);
            renderModule(moduleUi);
            moduleUi.applyStateToComponent(moduleState);
        }

        updateUI();
    }

    private JPanel buildAddModulePanel() {
        NonOpaquePanel panel = new NonOpaquePanel(new HorizontalLayout(JBUI.scale(8)));

        // Module name
        JBTextField newModuleName = new JBTextField(15);
        newModuleName.setToolTipText(i18n.getString("config.project.modules.add.tooltip"));
        newModuleName.getEmptyText().setText(i18n.getString("config.project.modules.add.placeholder"));

        panel.add(newModuleName);

        // Add button
        JButton addButton = new JButton(null, AllIcons.General.Add);
        addButton.setEnabled(false);

        panel.add(addButton);

        // Listener

        // Toggles addButton enabled state upon if newModuleName is filled out
        newModuleName.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                var moduleName = newModuleName.getText();
                var moduleNameExists = modules.stream().anyMatch(module -> module.state.getName().equals(moduleName));
                addButton.setEnabled(!moduleName.isEmpty() && !moduleNameExists);
            }
        });

        // Add button has been clicked -> Submit new module
        addButton.addActionListener(e -> {
            handleAddModule(newModuleName.getText());
            newModuleName.setText(""); // Reset new module name
        });

        return panel;
    }

    private void handleAddModule(String moduleName) {
        ProjectConfigModule moduleState = ProjectConfigModule.fromDefaultPreset();
        moduleState.setName(moduleName);

        ProjectConfigModuleUi moduleUi = new ProjectConfigModuleUi(project, moduleState);

        renderModule(moduleUi);
        moduleUi.applyStateToComponent(moduleState);
        updateUI();
    }

    private void handleRemoveModule(String moduleName) {
        var moduleUi = modules.stream().filter(module -> module.state.getName().equals(moduleName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find module with name: " + moduleName));

        var index = modules.indexOf(moduleUi);

        modules.remove(moduleUi);
        modulesPanel.remove(index + 1); // Don't forget to remove the spacer
        modulesPanel.remove(index);
        updateUI();
    }

    private void renderModule(ProjectConfigModuleUi moduleUi) {
        FormBuilder builder = new FormBuilder();
        moduleUi.buildComponent(builder);

        // Remove button
        JButton removeButton = new JButton(AllIcons.General.Delete);
        removeButton.setToolTipText(i18n.getString("config.project.modules.remove.tooltip"));

        builder.addComponentToRightColumn(removeButton, 1);

        // Titled border
        var border = BorderFactory.createCompoundBorder(
            new TitledBorderWithMnemonic(moduleUi.state.getName()),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        builder.getPanel().setBorder(border);

        // Listener

        // Remove button clicked -> Remove module
        removeButton.addActionListener(e -> {
            handleRemoveModule(moduleUi.state.getName());
        });

        // Keep last
        modules.add(moduleUi);
        modulesPanel.add(builder.getPanel());
        modulesPanel.add(Box.createVerticalStrut(12)); // Spacer between modules
    }

    private void updateUI() {
        emptyModulesLabel.setVisible(modules.isEmpty());
        modulesPanel.updateUI();
    }
}
