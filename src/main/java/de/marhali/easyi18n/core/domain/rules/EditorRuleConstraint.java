package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a single constraint for an {@link EditorRule}.
 *
 * @param type Rule constraint type
 * @param value Constraint value
 * @param matchMode Match mode against given value
 * @param negated Whether the result should be inverted or not
 * @see EditorRule
 *
 * @author marhali
 */
public record EditorRuleConstraint(
    @NotNull RuleConstraintType type,
    @NotNull String value,
    @NotNull TextMatchMode matchMode,
    boolean negated
) {
    public static @NotNull EditorRuleConstraint fromDefaultPreset() {
        // Not really a preset here but consistency is key
        return new EditorRuleConstraint(
            RuleConstraintType.STATIC_ONLY,
            "default",
            TextMatchMode.EXACT,
            true
        );
    }

    public static @NotNull EditorRuleConstraint exact(@NotNull RuleConstraintType type, @NotNull String value) {
        return new EditorRuleConstraint(type, value, TextMatchMode.EXACT, false);
    }

    public static @NotNull EditorRuleConstraint match(@NotNull RuleConstraintType type,
                                                      @NotNull String value,
                                                      @NotNull TextMatchMode mode) {
        return new EditorRuleConstraint(type, value, mode, false);
    }

    public static @NotNull EditorRuleConstraint negated(@NotNull RuleConstraintType type,
                                                        @NotNull String value,
                                                        @NotNull TextMatchMode mode) {
        return new EditorRuleConstraint(type, value, mode, true);
    }

    public @NotNull EditorRuleConstraint withType(@NotNull RuleConstraintType type) {
        return new EditorRuleConstraint(type, value, matchMode, negated);
    }

    public @NotNull EditorRuleConstraint withValue(@NotNull String value) {
        return new EditorRuleConstraint(type, value, matchMode, negated);
    }

    public @NotNull EditorRuleConstraint withMatchMode(@NotNull TextMatchMode matchMode) {
        return new EditorRuleConstraint(type, value, matchMode, negated);
    }

    public @NotNull EditorRuleConstraint withNegated(boolean negated) {
        return new EditorRuleConstraint(type, value, matchMode, negated);
    }
}
