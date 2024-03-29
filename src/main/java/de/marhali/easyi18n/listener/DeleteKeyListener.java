package de.marhali.easyi18n.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Delete (DEL) keystroke listener.
 * @author marhali
 */
public class DeleteKeyListener implements KeyListener {

    private final Runnable onActivate;

    public DeleteKeyListener(Runnable onActivate) {
        this.onActivate = onActivate;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == KeyEvent.VK_DELETE) {
            this.onActivate.run();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}