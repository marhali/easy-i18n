package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Compiled editor rules.
 *
 * @author marhali
 */
public final class CompiledRules {
    private final @NotNull Map<EditorLanguage, Map<TriggerKind, List<EditorRule>>> exactBuckets;
    private final @NotNull List<EditorRule> globalFallbackRules;

    CompiledRules(
        @NotNull Map<EditorLanguage, Map<TriggerKind, List<EditorRule>>> exactBuckets,
        @NotNull List<EditorRule> globalFallbackRules
    ) {
        this.exactBuckets = exactBuckets;
        this.globalFallbackRules = globalFallbackRules;
    }

    public @NotNull List<EditorRule> candidatesFor(@NotNull EditorElement editorElement) {
        List<EditorRule> result = new ArrayList<>();

        Map<TriggerKind, List<EditorRule>> byTrigger = exactBuckets.get(editorElement.language());
        if (byTrigger != null) {
            result.addAll(byTrigger.getOrDefault(editorElement.triggerKind(), Collections.emptyList()));
        }

        result.addAll(globalFallbackRules);
        result.sort(Comparator.comparingInt(EditorRule::priority).reversed());
        return result;
    }
}
