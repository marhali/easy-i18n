package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Module-specific translations container.
 *
 * @param locales Set including every used language identifier for this module.
 * @param translations Map including every managed translation for this module.
 */
public record I18nModule(
    @NotNull Set<@NotNull LocaleId> locales,
    @NotNull Map<@NotNull I18nKey, @NotNull I18nContent> translations
    ) {
    public static @NotNull I18nModule empty() {
        return new I18nModule(Set.of(), Map.of());
    }

    public boolean hasLocale(@NotNull LocaleId localeId) {
        return locales.contains(localeId);
    }

    public boolean hasTranslation(@NotNull I18nKey key) {
        return translations.containsKey(key);
    }

    public @Nullable I18nContent getTranslation(@NotNull I18nKey key) {
        return translations.get(key);
    }
}
