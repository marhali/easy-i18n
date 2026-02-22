package de.marhali.easyi18n.idea.toolwindow.ui;

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
    @NotNull JComponent getComponent();

    void setView(@NotNull View view);

    @Nullable JComponent getToolbar();
}
