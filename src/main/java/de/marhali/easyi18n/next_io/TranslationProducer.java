package de.marhali.easyi18n.next_io;

import de.marhali.easyi18n.next_domain.I18nKey;
import de.marhali.easyi18n.next_domain.I18nParams;
import de.marhali.easyi18n.next_domain.I18nParamsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * Helper class for tracking the hierarchical creation of translations.

 * @author marhali
 */
public record TranslationProducer(
    @NotNull I18nParams params,
    @NotNull Integer level
) {
    public static @NotNull TranslationProducer from(@NotNull I18nParamsBuilder paramsBuilder, @NotNull Integer level) {
        return new TranslationProducer(paramsBuilder.build(), level);
    }

    public @NotNull I18nParamsBuilder paramsBuilder() {
        return params.toBuilder();
    }

    public @NotNull TranslationProducer children(
        @NotNull Function<@NotNull I18nParamsBuilder, @NotNull I18nParamsBuilder> paramsFunction,
        @NotNull Function<Integer, Integer> levelFunction
    ) {
        var newParams = paramsFunction.apply(paramsBuilder()).build();
        var newLevel = levelFunction.apply(level);
        return new TranslationProducer(newParams, newLevel);
    }

    public @NotNull String locale() {
        // We may validate that locales consist only of the same locale identifier
        return params.getOrThrowSingleton(I18nBuiltinParam.LOCALE.getParamName());
    }

    public @NotNull I18nKey toKey(@NotNull ModuleTemplate template) {
        return template.key().build(params);
    }
}
