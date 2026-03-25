package de.marhali.easyi18n.idea.config.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigBuilder;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.config.component.module.ProjectConfigModuleUi;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import de.marhali.easyi18n.idea.service.ModuleIdFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author marhali
 */
public class ProjectConfigModulesUi extends ConfigComponent<FormBuilder, ProjectConfig, ProjectConfigBuilder> {

    private @Nullable List<ProjectConfigModuleUi> modules;
    private @Nullable JPanel modulesPanel;
    private @Nullable JBLabel emptyModulesLabel;

    protected ProjectConfigModulesUi(@NotNull Project project) {
        super(project);
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        // Title
        builder.addComponent(new TitledSeparator(PluginBundle.message("config.project.modules.title")));

        // Add new module
        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.add.label"),
            buildAddModulePanel(), 1, false);

        builder.addVerticalGap(6);

        // Empty hint
        emptyModulesLabel = new JBLabel(PluginBundle.message("config.project.modules.empty"));
        emptyModulesLabel.setFont(JBFont.regular().asItalic());
        emptyModulesLabel.setForeground(UIUtil.getErrorForeground());
        builder.addComponent(emptyModulesLabel);

        // Modules enumeration (items are rendered via #writeStateToComponent)
        modules = new ArrayList<>();
        modulesPanel = new JPanel();
        modulesPanel.setLayout(new BoxLayout(modulesPanel, BoxLayout.Y_AXIS));
        builder.addComponent(modulesPanel, 1);

        // Keep last
        updateUI();
    }

    @Override
    public boolean isModified(@NotNull ProjectConfig originState) {
        Objects.requireNonNull(modules, "Modules cannot be null");

        return modules.size() != originState.modules().size()
            || modules.stream().anyMatch(module ->
            module.isModified(originState.modules().get(module.getModuleId())));
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfig state) {
        Objects.requireNonNull(modules, "modules cannot be null");
        Objects.requireNonNull(modulesPanel, "modulesPanel cannot be null");

        // Erase UI state
        modules.clear();
        modulesPanel.removeAll();

        // Render each module as component and write state
        for (ModuleId moduleId : state.modules().keySet()) {
            var moduleUi = new ProjectConfigModuleUi(project, moduleId);
            renderModule(moduleUi);
            moduleUi.writeStateToComponent(state.modules().get(moduleId));
        }

        // Keep last
        updateUI();
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigBuilder builder) {
        Objects.requireNonNull(modules,  "modules cannot be null");

        builder.modules(); // Erase existing modules state

        for (ProjectConfigModuleUi moduleUi : modules) {
            builder.module((moduleBuilder) -> {
                moduleUi.readStateFromComponent(moduleBuilder);
                return moduleBuilder.build();
            });
        }
    }

    private @NotNull JPanel buildAddModulePanel() {
        NonOpaquePanel panel = new NonOpaquePanel(new HorizontalLayout(JBUI.scale(8)));

        // Module name
        JBTextField newModuleName = new JBTextField(15);
        newModuleName.setToolTipText(PluginBundle.message("config.project.modules.add.tooltip"));
        newModuleName.getEmptyText().setText(PluginBundle.message("config.project.modules.add.placeholder"));
        panel.add(newModuleName);

        // Add button
        JButton addButton = new JButton(null, AllIcons.General.Add);
        addButton.setEnabled(false);
        panel.add(addButton);

        // Listener

        // Toggles addButton enabled state upon if newModuleName is filled out and unique
        newModuleName.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                Objects.requireNonNull(modules, "modules cannot be null");

                var moduleId = ModuleIdFactory.fromInput(newModuleName.getText());
                var moduleIdExists = modules.stream()
                    .anyMatch(module -> module.getModuleId().equals(moduleId));

                addButton.setEnabled(!moduleId.name().isEmpty() && !moduleIdExists);
            }
        });

        // addButton has been clicked -> Submit new module
        addButton.addActionListener(e -> {
            handleAddModule(ModuleIdFactory.fromInput(newModuleName.getText()));
            newModuleName.setText(""); // Reset newModuleName
        });

        return panel;
    }

    private void handleAddModule(@NotNull ModuleId moduleId) {
        ProjectConfigModule moduleState = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(moduleId)
            .build();

        ProjectConfigModuleUi moduleUi = new ProjectConfigModuleUi(project, moduleId);

        renderModule(moduleUi);
        moduleUi.writeStateToComponent(moduleState);
        updateUI();
    }

    private void handleRemoveModule(@NotNull ModuleId moduleId) {
        Objects.requireNonNull(modules, "modules cannot be null");
        Objects.requireNonNull(modulesPanel, "modulesPanel cannot be null");

        var moduleUi = modules.stream()
            .filter(module -> module.getModuleId().equals(moduleId)).
            findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown moduleId to remove: " + moduleId));

        var index = modules.indexOf(moduleUi);

        modules.remove(moduleUi);
        modulesPanel.remove(index + 1); // Don't forget to remove the spacer
        modulesPanel.remove(index);
        updateUI();
    }

    private void renderModule(@NotNull ProjectConfigModuleUi moduleUi) {
        Objects.requireNonNull(modules, "modules cannot be null");
        Objects.requireNonNull(modulesPanel, "modulesPanel cannot be null");

        FormBuilder builder = new FormBuilder();
        moduleUi.buildComponent(builder);

        // Remove button
        JButton removeButton = new JButton(AllIcons.General.Delete);
        removeButton.setToolTipText(PluginBundle.message("config.project.modules.remove.tooltip"));
        builder.addComponentToRightColumn(removeButton, 1);

        // Titled border
        var border = BorderFactory.createCompoundBorder(
            new TitledBorder(moduleUi.getModuleId().name()),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
        builder.getPanel().setBorder(border);

        // Listener

        // removeButton clicked -> Remove module
        removeButton.addActionListener(e -> {
            handleRemoveModule(moduleUi.getModuleId());
        });

        modules.add(moduleUi);
        modulesPanel.add(builder.getPanel());
        modulesPanel.add(Box.createVerticalStrut(12)); // Spacer between modules
    }

    private void updateUI() {
        Objects.requireNonNull(emptyModulesLabel, "emptyModulesLabel cannot be null");
        Objects.requireNonNull(modules, "modulesPanel cannot be null");
        Objects.requireNonNull(modulesPanel, "modulesPanel cannot be null");

        emptyModulesLabel.setVisible(modules.isEmpty());
        modulesPanel.updateUI();
    }
}
