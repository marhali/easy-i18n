package de.marhali.easyi18n.idea.config.state;

import de.marhali.easyi18n.core.domain.rules.EditorRuleConstraint;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Mapper between {@link EditorRuleConstraint} and {@link EditorRuleConstraintState}.
 *
 * @author marhali
 */
public final class EditorRuleConstraintStateMapper {

    private static final @NotNull EditorRuleConstraint DEFAULTS = EditorRuleConstraint.fromDefaultPreset();

    private EditorRuleConstraintStateMapper() {}

    public static @NotNull EditorRuleConstraint toDomain(@NotNull EditorRuleConstraintState state) {
        return new EditorRuleConstraint(
            Optional.ofNullable(state.type).orElse(DEFAULTS.type()),
            Optional.ofNullable(state.value).orElse(DEFAULTS.value()),
            Optional.ofNullable(state.matchMode).orElse(DEFAULTS.matchMode()),
            Optional.ofNullable(state.negated).orElse(DEFAULTS.negated())
        );
    }

    public static @NotNull EditorRuleConstraintState fromDomain(@NotNull EditorRuleConstraint domain) {
        return new EditorRuleConstraintState(
            domain.type(),
            domain.value(),
            domain.matchMode(),
            domain.negated()
        );
    }
}
