package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Editor element rule match details.
 *
 * @param matched Whether the rule has been matched or not
 * @param excluded Whether the rule is excluded or not
 * @param rule Matched editor rule
 * @param reason Description of the match
 *
 * @author marhali
 */
public record RuleMatch(
    boolean matched,
    boolean excluded,
    @Nullable EditorRule rule,
    @NotNull String reason
) {
    public static @NotNull RuleMatch noMatch(@NotNull String reason) {
        return new RuleMatch(false, false, null, reason);
    }

    public static @NotNull RuleMatch included(@NotNull EditorRule rule) {
        return new RuleMatch(true, false, rule, "matched rule " + rule.id());
    }

    public static @NotNull RuleMatch excluded(@NotNull EditorRule rule) {
        return new RuleMatch(false, true, rule, "excluded by rule " + rule.id());
    }
}
