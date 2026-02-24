package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

/**
 * Record tracking the hierarchical serialization (writing) of translations.
 *
 * @param level File level
 * @param indexedParams Indexed i18n params
 * @param value Translation target value
 * @param comment Optional comment
 *
 * @author marhali
 */
public record TranslationConsumer(
    @NotNull Integer level,
    @NotNull IndexedI18nParams indexedParams,
    @NotNull I18nValue value,
    @Nullable String comment
    ) {

    /**
     * Shorthand to construct a new consumer
     * @param params I18n params
     * @param value Translation target value
     * @param comment Optional comment
     * @return {@link TranslationConsumer}
     */
    public static @NotNull TranslationConsumer fromNew(
        @NotNull I18nParams params,
        @NotNull I18nValue value,
        @Nullable String comment
    ) {
        return new TranslationConsumer(0, new IndexedI18nParams(params, new HashMap<>()), value, comment);
    }

    /**
     * Creates a children {@link TranslationConsumer} for the next translation file level.
     * @param parameterNamesToIncrement Parameter names whose key param indexes should be incremented by 1
     * @return {@link TranslationConsumer}
     */
    public @NotNull TranslationConsumer withChildren(
        @NotNull Set<@NotNull String> parameterNamesToIncrement
        ) {
        return new TranslationConsumer(
            level + 1,
            indexedParams.withIncrementParameters(parameterNamesToIncrement),
            value,
            comment
        );
    }

    /**
     * Checks whether this consumer is fully indexed or not.
     * @return {@code true} if all params are consumed, otherwise {@code false}
     */
    public boolean isFullyIndexed() {
        return indexedParams.isFullyIndexed();
    }
}
