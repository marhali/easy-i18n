package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @param id Rule name
 * @param languages Languages that this rule targets
 * @param triggerKind Defines when this rule is triggered
 * @param constraints Constraints to apply for this rule
 * @param priority Priority of this rule. Higher is better
 * @param excludeRule Whether this rule should be excluded
 *
 * @author marhali
 */
public record EditorRule(
    @NotNull String id,
    @NotNull Set<@NotNull EditorLanguage> languages,
    @NotNull TriggerKind triggerKind,
    @NotNull List<@NotNull EditorRuleConstraint> constraints,
    int priority,
    boolean excludeRule
) {
    public static @NotNull EditorRule fromDefaultPreset() {
        // Not really a preset here but consistency is key
        return new EditorRule(
            "rule-01",
            Set.of(),
            TriggerKind.UNKNOWN,
            List.of(),
            0,
            false
        );
    }

    public @NotNull EditorRule withId(@NotNull String id) {
        return new EditorRule(id, languages, triggerKind, constraints, priority, excludeRule);
    }

    public @NotNull EditorRule withLanguages(@NotNull Set<EditorLanguage> languages) {
        return new EditorRule(id, languages, triggerKind, constraints, priority, excludeRule);
    }

    public @NotNull EditorRule withTriggerKind(@NotNull TriggerKind triggerKind) {
        return new EditorRule(id, languages, triggerKind, constraints, priority, excludeRule);
    }

    public @NotNull EditorRule withConstraints(@NotNull List<EditorRuleConstraint> constraints) {
        return new EditorRule(id, languages, triggerKind, constraints, priority, excludeRule);
    }

    public @NotNull EditorRule withAddConstraint(@NotNull EditorRuleConstraint constraint) {
        var newConstraints = new ArrayList<EditorRuleConstraint>(constraints);
        newConstraints.add(constraint);
        return new EditorRule(id, languages, triggerKind, newConstraints, priority, excludeRule);
    }

    public @NotNull EditorRule withPriority(int priority) {
        return new EditorRule(id, languages, triggerKind, constraints, priority, excludeRule);
    }

    public @NotNull EditorRule withExcludeRule(boolean excludeRule) {
        return new EditorRule(id, languages, triggerKind, constraints, priority, excludeRule);
    }
}
