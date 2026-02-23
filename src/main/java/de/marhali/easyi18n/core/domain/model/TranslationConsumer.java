package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Record tracking the hierarchical serialization (writing) of translations.
 *
 * @param level File level
 * @param keyParams Translation key parameters
 * @param keyParamsIndex Tracked key params indexes
 * @param localeId Translation target locale
 * @param value Translation target value
 * @param comment Optional comment
 *
 * @author marhali
 */
public record TranslationConsumer(
    @NotNull Integer level,
    @NotNull I18nParams keyParams,
    @NotNull Map<String, Integer> keyParamsIndex,
    @NotNull LocaleId localeId,
    @NotNull I18nValue value,
    @Nullable String comment
    ) {
    /**
     * Shorthand to construct a new consumer
     * @param keyParams Translation key params
     * @param localeId Translation target locale
     * @param value Translation target value
     * @param comment Optional comment
     * @return {@link TranslationConsumer}
     */
    public static @NotNull TranslationConsumer fromNew(
        @NotNull I18nParams keyParams,
        @NotNull LocaleId localeId,
        @NotNull I18nValue value,
        @Nullable String comment) {
        return new TranslationConsumer(0, keyParams, new HashMap<>(), localeId, value, comment);
    }

    /**
     * Creates a children {@link TranslationConsumer} for the next translation file level.
     * @param paramNamesToIncrement Parameter names whose key param indexes should be incremented by 1
     * @return {@link TranslationConsumer}
     */
    public @NotNull TranslationConsumer withChildren(
        @NotNull Set<@NotNull String> paramNamesToIncrement
        ) {
        var newLevel = level + 1;
        var newKeyParamsIndex = new HashMap<>(keyParamsIndex);

        for (String paramName : paramNamesToIncrement) {
            newKeyParamsIndex.compute(paramName,
                (_k, previousLevel) -> previousLevel == null ? 1 : previousLevel + 1);
        }

        return new TranslationConsumer(newLevel, keyParams, newKeyParamsIndex, localeId, value, comment);
    }

    /**
     * Checks whether this consumer is fully indexed or not.
     * @return {@code true} if all params are consumed, otherwise {@code false}
     */
    public boolean isIndexed() {
        return keyParamsIndex.entrySet().stream()
            .allMatch(entry ->
                entry.getValue() == Objects.requireNonNull(keyParams.get(entry.getKey())).size());
    }
}
