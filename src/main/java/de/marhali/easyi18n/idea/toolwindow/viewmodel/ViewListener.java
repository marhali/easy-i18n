package de.marhali.easyi18n.idea.toolwindow.viewmodel;

import de.marhali.easyi18n.core.application.query.view.ModuleView;
import org.jetbrains.annotations.NotNull;

/**
 * View related listener for translations tool window panels.
 *
 * @author marhali
 */
public interface ViewListener {
    /**
     * Sets the rendered view for this panel.
     * @param moduleView Module view
     */
    void onViewUpdated(@NotNull ModuleView moduleView);

    /**
     * Indicates that this panel is now visible.
     */
    void onFocusView();

    /**
     * Invalidates the rendered view for this panel.
     */
    void onViewInvalidated();
}
