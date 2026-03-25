package de.marhali.easyi18n.idea.config;

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.core.application.command.InvalidateProjectConfigCommand;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.idea.config.component.ProjectConfigUi;
import de.marhali.easyi18n.idea.help.PluginWebHelpProvider;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.PluginExecutorService;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * IntelliJ settings configurable for {@link ProjectConfig}.
 *
 * @author marhali
 */
public class ProjectConfigConfigurable implements SearchableConfigurable {

    private static final @NotNull Logger LOGGER = Logger.getInstance(ProjectConfigConfigurable.class);

    private final @NotNull Project project;

    private ProjectConfigUi component;

    public ProjectConfigConfigurable(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public @NotNull @NonNls String getId() {
        return "de.marhali.easyi18n.config.project";
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return null;
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new ProjectConfigUi(project);

        FormBuilder builder = FormBuilder.createFormBuilder();
        component.buildComponent(builder);
        builder.addComponentFillVertically(new JPanel(), 0);

        return builder.getPanel();
    }

    @Override
    public boolean isModified() {
        Objects.requireNonNull(component, "component is null");

        var state = project.getService(ProjectConfigService.class).getDomainState();
        return component.isModified(state);
    }

    @Override
    public void apply() throws ConfigurationException {
        Objects.requireNonNull(component, "component is null");

        var builder = ProjectConfig.builder();
        component.readStateFromComponent(builder);
        var nextState = builder.build();
        project.getService(ProjectConfigService.class).loadDomainState(nextState);

        component.writeStateToComponent(nextState); // Sync UI again (e.g. update preset selection)

        project.getService(PluginExecutorService.class).runAsync(
            () -> {
                project.getService(I18nProjectService.class).command(new InvalidateProjectConfigCommand());
                return null;
            },
            (_r) -> {}, // We expect happy path here
            LOGGER::error,
            ModalityState.any(),
            (o) -> project.isDisposed()
        );
    }

    @Override
    public void reset() {
        Objects.requireNonNull(component, "component is null");

        var state = project.getService(ProjectConfigService.class).getDomainState();
        component.writeStateToComponent(state);
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }

    @Override
    public @Nullable @NonNls String getHelpTopic() {
        return PluginWebHelpProvider.Topic.DOCS.helpTopicId();
    }
}
