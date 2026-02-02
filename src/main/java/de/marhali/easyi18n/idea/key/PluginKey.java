package de.marhali.easyi18n.idea.key;

import com.intellij.openapi.util.Key;
import de.marhali.easyi18n.core.domain.model.ModuleId;

/**
 * Plugin specific IntelliJ keys to interact with.
 *
 * @author marhali
 */
public final class PluginKey {

    private PluginKey() {}

    /**
     * Tracks a {@link ModuleId} association.
     */
    public static final Key<ModuleId> MODULE_ID = Key.create("de.marhali.easyi18n.core.domain.moduleId");
}
