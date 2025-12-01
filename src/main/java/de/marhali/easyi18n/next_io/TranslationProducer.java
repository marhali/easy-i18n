package de.marhali.easyi18n.next_io;

import de.marhali.easyi18n.next_domain.I18nKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class for tracking the hierarchical creation of translations.
 *
 * @author marhali
 */
public record TranslationProducer(
    @NotNull Map<String, List<String>> params,
    int depth
) {
    public static TranslationProducer of(@NotNull Map<String, String> singleParams, int depth) {
        var mappedEntries = singleParams.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, (entry) -> List.of(entry.getValue())));

        return new TranslationProducer(mappedEntries, depth);
    }

    public TranslationProducer putParameter(@NotNull String key, @NotNull String value) {
        var newParams = new HashMap<>(params);
        newParams.computeIfAbsent(key, (_key) -> new ArrayList<>()).add(value);

        return new TranslationProducer(newParams, depth);
    }

    public TranslationProducer increaseDepth() {
        return new TranslationProducer(params, depth + 1);
    }

    public @NotNull String locale() {
        var locales = this.params.getOrDefault(I18nBuiltinParam.LOCALE.getParamName(), List.of());

        if (locales.isEmpty()) {
            throw new NoSuchElementException("Missing locale parameter in params: " + params);
        }

        // We may validate that locales consist only of the same locale identifier
        return locales.getFirst();
    }

    public @NotNull I18nKey toKey(@NotNull ModuleTemplate template) {
        return template.key(). build(params);
    }
}
