package de.marhali.easyi18n.idea.config.state;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.core.domain.rules.TriggerKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents {@link de.marhali.easyi18n.core.domain.rules.EditorRule}.
 *
 * @author marhali
 */
public class EditorRuleState {
    public @Nullable String id;
    public @Nullable Set<@NotNull EditorLanguage> languages;
    public @Nullable TriggerKind triggerKind;
    public @Nullable Integer priority;
    public @Nullable Boolean excludeRule;
    public @Nullable List<@NotNull EditorRuleConstraintState> constraints;

    @Deprecated
    public EditorRuleState() {}

    public EditorRuleState(
        @Nullable String id,
        @Nullable Set<@NotNull EditorLanguage> languages,
        @Nullable TriggerKind triggerKind,
        @Nullable Integer priority,
        @Nullable Boolean excludeRule,
        @Nullable List<@NotNull EditorRuleConstraintState> constraints
    ) {
        this.id = id;
        this.languages = languages;
        this.triggerKind = triggerKind;
        this.priority = priority;
        this.excludeRule = excludeRule;
        this.constraints = constraints;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EditorRuleState that = (EditorRuleState) o;
        return Objects.equals(priority, that.priority) && excludeRule == that.excludeRule && Objects.equals(id, that.id) && Objects.equals(languages, that.languages) && triggerKind == that.triggerKind && Objects.equals(constraints, that.constraints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, languages, triggerKind, priority, excludeRule, constraints);
    }

    @Override
    public String toString() {
        return "EditorRuleState{" +
            "id='" + id + '\'' +
            ", languages=" + languages +
            ", triggerKind=" + triggerKind +
            ", priority=" + priority +
            ", excludeRule=" + excludeRule +
            ", constraints=" + constraints +
            '}';
    }
}
