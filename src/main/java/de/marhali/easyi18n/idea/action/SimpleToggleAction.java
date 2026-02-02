package de.marhali.easyi18n.idea.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.util.Producer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * Toggle action with external state provider
 *
 * @author marhali
 */
abstract class SimpleToggleAction extends ToggleAction implements DumbAware {

    private final @NotNull Producer<Boolean> queryToggleState;
    private final @NotNull Consumer<Boolean> onToggleAction;

    protected SimpleToggleAction(
        @NotNull @Nls String text,
        @Nullable @Nls String description,
        @Nullable Icon icon,
        @NotNull Producer<Boolean> queryToggleState,
        @NotNull Consumer<Boolean> onToggleAction
    ) {
        super(text, description, icon);

        this.queryToggleState = queryToggleState;
        this.onToggleAction = onToggleAction;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
        return queryToggleState.get();
    }

    @Override
    public void setSelected(@NotNull AnActionEvent anActionEvent, boolean state) {
        onToggleAction.accept(state);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
