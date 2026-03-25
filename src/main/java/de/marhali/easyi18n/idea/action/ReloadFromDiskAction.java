package de.marhali.easyi18n.idea.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import de.marhali.easyi18n.core.application.command.ReloadCommand;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.PluginExecutorService;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Action to reload from disk.
 *
 * @author marhali
 */
public final class ReloadFromDiskAction extends DumbAwareAction {

    private static final @NotNull Logger LOGGER = Logger.getInstance(ReloadFromDiskAction.class);

    public ReloadFromDiskAction() {
        super(PluginBundle.message("action.reload.label"), null, AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = Objects.requireNonNull(e.getProject(), "Project must not be null");

        I18nProjectService projectService = project.getService(I18nProjectService.class);
        PluginExecutorService executorService = project.getService(PluginExecutorService.class);

        executorService.runAsync(
            () -> {
                projectService.command(ReloadCommand.reloadAll());
                return null;
            },
            (_void) -> {}, // We expect happy path here
            LOGGER::error,
            ModalityState.any(),
            (o) -> project.isDisposed()
        );
    }
}
