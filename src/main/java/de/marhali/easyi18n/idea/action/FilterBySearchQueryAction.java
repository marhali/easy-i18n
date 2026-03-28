package de.marhali.easyi18n.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBTextField;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

import com.intellij.util.Alarm;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.util.function.Consumer;

/**
 * Search all translations action.
 *
 * @author marhali
 */
public final class FilterBySearchQueryAction extends DumbAwareAction implements CustomComponentAction {

    private static final int DEBOUNCE_MS = 400;

    private final @NotNull Consumer<String> onSearchQuery;
    private final @NotNull Alarm debounceAlarm;
    private final @NotNull JBTextField component;

    public FilterBySearchQueryAction(@NotNull Consumer<String> onSearchQuery) {
        super(PluginBundle.message("action.filter.search.label"));

        this.onSearchQuery = onSearchQuery;
        this.debounceAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD);
        this.component = new JBTextField(16);
        this.component.getEmptyText().setText(PluginBundle.message("action.filter.search.placeholder"));
        this.component.setToolTipText(PluginBundle.message("action.filter.search.tooltip"));
        this.component.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                schedulePerform();
            }
        });
    }

    private void schedulePerform() {
        debounceAlarm.cancelAllRequests();
        debounceAlarm.addRequest(this::perform, DEBOUNCE_MS);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        this.perform();
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return component;
    }

    public void perform() {
        this.onSearchQuery.accept(this.component.getText());
    }
}
