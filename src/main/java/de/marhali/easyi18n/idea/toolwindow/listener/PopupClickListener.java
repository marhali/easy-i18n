package de.marhali.easyi18n.idea.toolwindow.listener;

import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

/**
 * Popup click mouse listener.
 *
 * @author marhali
 */
public final class PopupClickListener implements MouseListener {

    private final @NotNull Consumer<@NotNull MouseEvent> onHandleMouseEvent;

    public PopupClickListener(@NotNull Consumer<@NotNull MouseEvent> onHandleMouseEvent) {
        this.onHandleMouseEvent = onHandleMouseEvent;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not relevant
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            this.onHandleMouseEvent.accept(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            this.onHandleMouseEvent.accept(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not relevant
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not relevant
    }
}
