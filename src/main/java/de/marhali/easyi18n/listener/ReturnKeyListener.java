package de.marhali.easyi18n.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Return (\n) keystroke listener.
 * @author marhali
 */
public class ReturnKeyListener implements KeyListener {

    private final Runnable onActivate;

    public ReturnKeyListener(Runnable onActivate) {
        this.onActivate = onActivate;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == KeyEvent.VK_ENTER) {
            this.onActivate.run();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}