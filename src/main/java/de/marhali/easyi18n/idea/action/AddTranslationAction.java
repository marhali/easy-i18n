package de.marhali.easyi18n.idea.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.Producer;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.idea.dialog.TranslationDialog;
import de.marhali.easyi18n.idea.dialog.TranslationDialogFactory;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Action to trigger a translation dialog in add mode.
 *
 * @author marhali
 */
public class AddTranslationAction extends AnAction {

    private final @NotNull Producer<@NotNull ModuleId> onResolveModuleId;

    public AddTranslationAction(@NotNull Producer<ModuleId> onResolveModuleId) {
        super(PluginBundle.message("action.translation.add"), null, AllIcons.General.Add);

        this.onResolveModuleId = onResolveModuleId;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = Objects.requireNonNull(e.getProject(), "project must not be null");
        var moduleId = onResolveModuleId.produce();
        TranslationDialog dialog = TranslationDialogFactory.createAddDialog(project, moduleId);
        dialog.show();
    }
}
