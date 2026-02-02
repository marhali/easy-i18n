package de.marhali.easyi18n.idea.action;

import com.intellij.util.Producer;
import de.marhali.easyi18n.idea.icons.PluginIcon;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Toggle action to toggle presentation mode.
 *
 * @author marhali
 */
public final class ShowAsTreeToggleAction extends SimpleToggleAction {

    public ShowAsTreeToggleAction(
        @NotNull Producer<Boolean> queryToggleState,
        @NotNull Consumer<Boolean> onToggleAction
    ) {
        super(
            PluginBundle.message("action.show.tree.label"),
            null,
            PluginIcon.SHOW_AS_TREE_ICON,
            queryToggleState,
            onToggleAction
        );
    }
}
