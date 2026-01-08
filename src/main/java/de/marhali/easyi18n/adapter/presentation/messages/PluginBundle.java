package de.marhali.easyi18n.adapter.presentation.messages;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Supplier;

/**
 * Plugin-specific i18n messages.
 *
 * @author marhali
 */
public final class PluginBundle {

    @NonNls
    private static final String BUNDLE = "messages.PluginBundle";
    private static final DynamicBundle INSTANCE =
        new DynamicBundle(PluginBundle.class, BUNDLE);

    private PluginBundle() {}

    public static @NotNull @Nls String message(
        @NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
        Object @NotNull ... params
    ) {
        return INSTANCE.getMessage(key, params);
    }

    public static Supplier<@Nls String> lazyMessage(
        @NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
        Object @NotNull ... params
    ) {
        return INSTANCE.getLazyMessage(key, params);
    }
}
