package de.marhali.easyi18n.idea.config.state;

import de.marhali.easyi18n.core.domain.rules.EditorRuleConstraint;
import de.marhali.easyi18n.core.domain.rules.RuleConstraintType;
import de.marhali.easyi18n.core.domain.rules.TextMatchMode;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents {@link EditorRuleConstraint}.
 *
 * @author marhali
 */
public class EditorRuleConstraintState {
    public @Nullable RuleConstraintType type;
    public @Nullable String value;
    public @Nullable TextMatchMode matchMode;
    public @Nullable Boolean negated;

    @Deprecated
    public EditorRuleConstraintState() {}

    public EditorRuleConstraintState(
        @Nullable RuleConstraintType type,
        @Nullable String value,
        @Nullable TextMatchMode matchMode,
        @Nullable Boolean negated
    ) {
        this.type = type;
        this.value = value;
        this.matchMode = matchMode;
        this.negated = negated;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EditorRuleConstraintState that = (EditorRuleConstraintState) o;
        return negated == that.negated && type == that.type && Objects.equals(value, that.value) && matchMode == that.matchMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value, matchMode, negated);
    }

    @Override
    public String toString() {
        return "RuleConstraintState{" +
            "type=" + type +
            ", value='" + value + '\'' +
            ", matchMode=" + matchMode +
            ", negated=" + negated +
            '}';
    }
}
