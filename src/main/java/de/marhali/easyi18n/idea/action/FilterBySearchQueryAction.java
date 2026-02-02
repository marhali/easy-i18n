package de.marhali.easyi18n.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.components.JBTextField;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Search all translations action.
 *
 * @author marhali
 */
public final class FilterBySearchQueryAction extends DumbAwareAction implements CustomComponentAction {

    private final @NotNull Consumer<String> onSearchQuery;
    private final @NotNull JBTextField component;

    public FilterBySearchQueryAction(@NotNull Consumer<String> onSearchQuery) {
        super(PluginBundle.message("action.filter.search.label"));

        this.onSearchQuery = onSearchQuery;
        this.component = new JBTextField(16);
        this.component.getEmptyText().setText(PluginBundle.message("action.filter.search.placeholder"));
        this.component.setToolTipText(PluginBundle.message("action.filter.search.tooltip"));
        this.component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if  (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    perform();
                }
            }
        });
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
