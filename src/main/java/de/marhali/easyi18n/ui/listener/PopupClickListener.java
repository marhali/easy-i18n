package de.marhali.easyi18n.ui.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

/**
 * Popup click listener for awt {@link MouseListener}.
 * Emits consumer defined in constructor on popup open action.
 * @author marhali
 */
public class PopupClickListener implements MouseListener {

    private final Consumer<MouseEvent> callback;

    public PopupClickListener(Consumer<MouseEvent> callback) {
        this.callback = callback;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.isPopupTrigger()) {
            this.callback.accept(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.isPopupTrigger()) {
            this.callback.accept(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}