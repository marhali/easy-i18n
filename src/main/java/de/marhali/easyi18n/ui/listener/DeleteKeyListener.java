package de.marhali.easyi18n.ui.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Delete (DEL) keystroke listener.
 * @author marhali
 */
public class DeleteKeyListener implements KeyListener {

    private final Runnable deleteRunnable;

    public DeleteKeyListener(Runnable deleteRunnable) {
        this.deleteRunnable = deleteRunnable;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == KeyEvent.VK_DELETE) {
            deleteRunnable.run();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}