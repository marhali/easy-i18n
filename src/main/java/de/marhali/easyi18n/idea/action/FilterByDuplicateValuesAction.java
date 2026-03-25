package de.marhali.easyi18n.idea.action;

import com.intellij.icons.AllIcons;
import com.intellij.util.Producer;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;

/**
 * Action to toggle duplicate translation values filter.
 *
 * @author marhali
 */
public final class FilterByDuplicateValuesAction extends SimpleToggleAction {
    public FilterByDuplicateValuesAction(@NotNull Producer<Boolean> queryToggleState, @NotNull Consumer<Boolean> onToggleAction) {
        super(
            PluginBundle.message("action.filter.duplicate.label"),
            null,
            AllIcons.Actions.PreserveCase,
            queryToggleState,
            onToggleAction
        );
    }
}
