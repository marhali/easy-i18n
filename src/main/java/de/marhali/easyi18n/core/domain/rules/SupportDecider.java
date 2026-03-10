package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;

/**
 * Editor element support decider.
 *
 * @param <T> Underlying editor element class
 *
 * @author marhali
 */
public class SupportDecider<T> {

    private final @NotNull I18nRuleEngine ruleEngine;
    private final @NotNull EditorElementExtractor<T> extractor;

    public SupportDecider(@NotNull I18nRuleEngine ruleEngine, @NotNull EditorElementExtractor<T> extractor) {
        this.ruleEngine = ruleEngine;
        this.extractor = extractor;
    }

    /**
     * Decide whether the provided element is supported by the defined rules or not.
     * @param value Underlying editor element
     * @return {@link SupportDecision}
     */
    public @NotNull SupportDecision decide(@NotNull T value) {
        EditorElement editorElement = extractor.extract(value);

        if (editorElement == null) {
            return SupportDecision.unsupported(RuleMatch.noMatch("No extracted editor element"));
        }

        RuleMatch match = ruleEngine.match(editorElement);

        if (!match.matched()) {
            return SupportDecision.unsupported(match);
        }

        return SupportDecision.supported(editorElement, match);
    }
}
