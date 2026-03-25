package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Record tracking the hierarchical creation (parsing) of translations.
 *
 * @param params Tracked params
 * @param level Tracked hierarchy
 *
 * @author marhali
 */
public record TranslationProducer(
    @NotNull I18nParams params,
    @NotNull Integer level
) {
    public static @NotNull TranslationProducer from(@NotNull I18nParamsBuilder paramsBuilder, @NotNull Integer level) {
        return new TranslationProducer(paramsBuilder.build(), level);
    }

    public @NotNull TranslationProducer withChildren(
        @NotNull Function<@NotNull I18nParamsBuilder, @NotNull I18nParams> paramsFunction,
        @NotNull Function<Integer, Integer> levelFunction
        ) {
        var newParams = paramsFunction.apply(params.toBuilder());
        var newLevel = levelFunction.apply(level);
        return new TranslationProducer(newParams, newLevel);
    }
}
