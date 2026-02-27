package de.marhali.easyi18n.idea.toolwindow.viewmodel;

import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * View related listener for translations tool window panels.
 *
 * @author marhali
 */
public interface ViewListener {
    /**
     * Sets the rendered view for this panel.
     * @param moduleView Module view
     * @param key Optional {@link I18nKey} that has been affected
     */
    void onViewUpdated(@NotNull ModuleView moduleView, @Nullable I18nKey key);

    /**
     * Indicates that this panel is now visible.
     */
    void onFocusView();

    /**
     * Invalidates the rendered view for this panel.
     */
    void onViewInvalidated();
}
