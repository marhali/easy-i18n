package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.I18nContent;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Index to track (and find) duplicate values.
 *
 * @author marhali
 */
public final class DuplicateValueIndex {

    public record Origin(
        @NotNull I18nKey key,
        @NotNull LocaleId localeId
        ) {}

    private final @NotNull Map<@NotNull I18nValue, Set<Origin>> index;

    public DuplicateValueIndex() {
        this.index = new HashMap<>();
    }

    public boolean hasDuplicates(@NotNull I18nValue value) {
        var origins = index.get(value);
        return origins != null && origins.size() > 1;
    }

    public @NotNull Set<@NotNull Origin> getOrigins(@NotNull I18nValue value) {
        return index.getOrDefault(value, new HashSet<>());
    }

    public void rebuild(@NotNull Map<@NotNull I18nKey, @NotNull I18nContent> translations) {
        this.index.clear();

        for (Map.Entry<@NotNull I18nKey, @NotNull I18nContent> entry : translations.entrySet()) {
            for (Map.Entry<@NotNull LocaleId, @NotNull I18nValue> valueEntry : entry.getValue().values().entrySet()) {
                index.computeIfAbsent(valueEntry.getValue(), (_value) -> new HashSet<>())
                    .add(new Origin(entry.getKey(), valueEntry.getKey()));
            }
        }
    }
}
