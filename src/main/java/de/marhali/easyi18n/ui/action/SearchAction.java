package de.marhali.easyi18n.ui.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;

import org.jdesktop.swingx.prompt.PromptSupport;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Search translations by key action.
 * @author marhali
 */
public class SearchAction extends AnAction implements CustomComponentAction {

    private final Consumer<String> searchCallback;
    private JBTextField textField;

    public SearchAction(@NotNull Consumer<String> searchCallback) {
        super(ResourceBundle.getBundle("messages").getString("action.search"));
        this.searchCallback = searchCallback;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {} // Should never be called

    public void actionPerformed() {
        searchCallback.accept(textField == null ? "" : textField.getText());
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        textField = new JBTextField();
        textField.setPreferredSize(new Dimension(160, 25));
        PromptSupport.setPrompt(ResourceBundle.getBundle("messages").getString("action.search"), textField);

        textField.addKeyListener(handleKeyListener());
        textField.setBorder(JBUI.Borders.empty());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    private KeyAdapter handleKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    actionPerformed();
                }
            }
        };
    }
}
