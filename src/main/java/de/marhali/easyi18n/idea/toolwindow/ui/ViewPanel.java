package de.marhali.easyi18n.idea.toolwindow.ui;

import de.marhali.easyi18n.core.domain.model.I18nKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * View panel representing a translations module rendered inside a translations tool window tab.
 *
 * @param <View> View model
 *
 * @author marhali
 */
public interface ViewPanel<View> {
    /**
     * @return View panel component to render
     */
    @NotNull JComponent getComponent();

    /**
     * Updates the underlying view model
     * @param view New view model
     * @param key Optional {@link I18nKey} which has been affected
     */
    void setView(@NotNull View view, @Nullable I18nKey key);

    /**
     * @return Toolbar to render for the view or {@code null} if no toolbar should be shown
     */
    @Nullable JComponent getToolbar();
}
