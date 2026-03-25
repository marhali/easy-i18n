package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Editor rule compiler.
 *
 * @author marhali
 */
public final class RuleCompiler {

    /**
     * Compiles the given editor rules.
     * @param rules List of editor rules
     * @return {@link CompiledRules}
     */
    public @NotNull CompiledRules compile(@NotNull List<EditorRule> rules) {
        Map<EditorLanguage, Map<TriggerKind, List<EditorRule>>> exactBuckets = new EnumMap<>(EditorLanguage.class);
        List<EditorRule> globalFallbackRules = new ArrayList<>();

        for (EditorRule rule : rules) {
            if (rule.languages().isEmpty()) {
                globalFallbackRules.add(rule);
                continue;
            }

            for (EditorLanguage language : rule.languages()) {
                exactBuckets
                    .computeIfAbsent(language, __ -> new EnumMap<>(TriggerKind.class))
                    .computeIfAbsent(rule.triggerKind(), __ -> new ArrayList<>())
                    .add(rule);
            }
        }

        for (Map<TriggerKind, List<EditorRule>> byTrigger : exactBuckets.values()) {
            for (List<EditorRule> bucket : byTrigger.values()) {
                bucket.sort(Comparator.comparingInt(EditorRule::priority).reversed());
            }
        }
        globalFallbackRules.sort(Comparator.comparingInt(EditorRule::priority).reversed());

        return new CompiledRules(exactBuckets, globalFallbackRules);
    }
}
