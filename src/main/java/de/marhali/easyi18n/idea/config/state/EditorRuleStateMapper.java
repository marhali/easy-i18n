package de.marhali.easyi18n.idea.config.state;

import de.marhali.easyi18n.core.domain.rules.EditorRule;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Mapper between {@link EditorRule} and {@link EditorRuleState}.
 *
 * @author marhali
 */
public final class EditorRuleStateMapper {

    private static final @NotNull EditorRule DEFAULTS = EditorRule.fromDefaultPreset();

    private EditorRuleStateMapper() {}

    public static @NotNull EditorRule toDomain(@NotNull EditorRuleState state) {
        return new EditorRule(
            Optional.ofNullable(state.id).orElse(DEFAULTS.id()),
            Optional.ofNullable(state.languages).orElse(DEFAULTS.languages()),
            Optional.ofNullable(state.triggerKind).orElse(DEFAULTS.triggerKind()),
            state.constraints != null
                ? state.constraints.stream().map(EditorRuleConstraintStateMapper::toDomain).toList()
                : DEFAULTS.constraints(),
            Optional.ofNullable(state.priority).orElse(DEFAULTS.priority()),
            Optional.ofNullable(state.excludeRule).orElse(DEFAULTS.excludeRule())
        );
    }

    public static @NotNull EditorRuleState fromDomain(@NotNull EditorRule domain) {
        return new EditorRuleState(
            domain.id(),
            domain.languages(),
            domain.triggerKind(),
            domain.priority(),
            domain.excludeRule(),
            domain.constraints().stream().map(EditorRuleConstraintStateMapper::fromDomain).toList()
        );
    }
}
