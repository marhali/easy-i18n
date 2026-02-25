package de.marhali.easyi18n.idea.toolwindow.listener;

import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Enter key typed key listener.
 *
 * @author marhali
 */
public class EnterKeyListener implements KeyListener {

    private final @NotNull Runnable onHandleKeyEvent;

    public EnterKeyListener(@NotNull Runnable onHandleKeyEvent) {
        this.onHandleKeyEvent = onHandleKeyEvent;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            onHandleKeyEvent.run();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Not relevant
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not relevant
    }
}
