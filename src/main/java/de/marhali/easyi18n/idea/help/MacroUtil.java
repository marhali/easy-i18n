package de.marhali.easyi18n.idea.help;

import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author marhali
 */
public final class MacroUtil {

    private MacroUtil() {}

    public static @Nullable String collapsePath(@NotNull Project project, @Nullable String expandedPath) {
        return expandedPath != null
            ? PathMacroManager.getInstance(project).collapsePath(expandedPath)
            : null;
    }
}
