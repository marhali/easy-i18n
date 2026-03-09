package de.marhali.easyi18n.idea.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.StatusText;
import de.marhali.easyi18n.idea.config.ShowProjectConfigUtil;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Tool window panel to indicate empty state.
 *
 * @author marhali
 */
public final class I18nToolWindowEmptyPanel extends SimpleToolWindowPanel {

    public I18nToolWindowEmptyPanel(@NotNull Project project) {
        super(false, true);

        StatusText statusText = getEmptyText();

        // Reason
        statusText.appendLine(PluginBundle.message("toolwindow.empty.reason"));

        // Action
        statusText.appendLine(PluginBundle.message("toolwindow.empty.action"),
            SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES,
            (e) -> ShowProjectConfigUtil.open(project));

        // Divider
        statusText.appendLine("");

        // Help topic
        statusText.appendLine(AllIcons.General.ContextHelp, PluginBundle.message("toolwindow.empty.help"),
            SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES,
            (e) -> BrowserUtil.browse(PluginBundle.message("url.documentation.configuration")));
    }
}
