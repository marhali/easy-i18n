package de.marhali.easyi18n.idea.action;

import com.intellij.icons.AllIcons;
import com.intellij.util.Producer;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;

/**
 * Action which toggles translation filter on missing values.
 *
 * @author marhali
 */
public final class FilterByMissingValuesAction extends SimpleToggleAction {
    public FilterByMissingValuesAction(@NotNull Producer<Boolean> queryToggleState, @NotNull Consumer<Boolean> onToggleAction) {
        super(
            PluginBundle.message("action.filter.missing.label"),
            null,
            AllIcons.Actions.Words,
            queryToggleState,
            onToggleAction
        );
    }
}
