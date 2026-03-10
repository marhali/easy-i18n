package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Editor element support decision details.
 *
 * @param supported Whether the element is supported or not
 * @param element Editor element
 * @param match Matched rule
 *
 * @author marhali
 */
public record SupportDecision(
    boolean supported,
    @Nullable EditorElement element,
    @NotNull RuleMatch match
) {
    /**
     * Shorthand to construct an unsupported decision.
     * @param match Rule match
     * @return {@link SupportDecision}
     */
    public static @NotNull SupportDecision unsupported(@NotNull RuleMatch match) {
        return new SupportDecision(false, null, match);
    }

    /**
     * Shorthand to construct a supported decision.
     * @param editorElement Editor element
     * @param match Rule match
     * @return {@link SupportDecision}
     */
    public static @NotNull SupportDecision supported(@NotNull EditorElement editorElement, @NotNull RuleMatch match) {
        return new SupportDecision(true, editorElement, match);
    }
}
