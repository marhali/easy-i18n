package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Editor element rule engine to match against compiled rules.
 *
 * @author marhali
 */
public final class I18nRuleEngine {

    private final @NotNull CompiledRules compiledRules;

    public I18nRuleEngine(@NotNull CompiledRules compiledRules) {
        this.compiledRules = compiledRules;
    }

    /**
     * Matches the given editor element against the compiled rules.
     *
     * @param editorElement Editor element
     * @return {@link RuleMatch}
     */
    public @NotNull RuleMatch match(@NotNull EditorElement editorElement) {
        List<EditorRule> candidates = compiledRules.candidatesFor(editorElement);

        if (candidates.isEmpty()) {
            return RuleMatch.noMatch("No candidate rules");
        }

        EditorRule bestInclude = null;
        for (EditorRule rule : candidates) {
            if (!matchesRule(editorElement, rule)) {
                continue;
            }
            if (rule.excludeRule()) {
                return RuleMatch.excluded(rule);
            }
            bestInclude = rule;
            break;
        }

        return bestInclude != null
            ? RuleMatch.included(bestInclude)
            : RuleMatch.noMatch("No rule matched");
    }

    private boolean matchesRule(@NotNull EditorElement editorElement, @NotNull EditorRule rule) {
        if (!rule.languages().isEmpty() && !rule.languages().contains(editorElement.language())) {
            return false;
        }
        if (rule.triggerKind() != TriggerKind.UNKNOWN && rule.triggerKind() != editorElement.triggerKind()) {
            return false;
        }

        for (EditorRuleConstraint constraint : rule.constraints()) {
            if (!matchesConstraint(editorElement, constraint)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesConstraint(@NotNull EditorElement editorElement, @NotNull EditorRuleConstraint constraint) {
        boolean rawMatch = switch (constraint.type()) {
            case LANGUAGE -> matchesText(editorElement.language().name(), constraint);
            case LITERAL_KIND -> matchesText(editorElement.literalKind().name(), constraint);
            case STATIC_ONLY -> matchesBoolean(editorElement.staticallyKnown(), constraint);
            case CALLABLE_NAME -> matchesText(editorElement.callableName(), constraint);
            case CALLABLE_FQN -> matchesText(editorElement.callableFqn(), constraint);
            case RECEIVER_TYPE_FQN -> matchesText(editorElement.receiverTypeFqn(), constraint);
            case ARGUMENT_INDEX -> matchesInteger(editorElement.argumentIndex(), constraint);
            case ARGUMENT_NAME -> matchesText(editorElement.argumentName(), constraint);
            case DECLARATION_NAME -> matchesText(editorElement.declarationName(), constraint);
            case DECLARATION_MARKER -> matchesAny(editorElement.declarationMarkers(), constraint);
            case PROPERTY_NAME -> matchesText(editorElement.propertyName(), constraint);
            case PROPERTY_PATH -> matchesText(editorElement.propertyPath(), constraint);
            case IMPORT_SOURCE -> matchesAny(editorElement.importSources(), constraint);
            case FILE_PATH -> matchesText(editorElement.filePath().canonical(), constraint);
            case IN_TEST_SOURCES -> matchesBoolean(editorElement.inTestSources(), constraint);
            case TEXT_PATTERN, EXCLUDE -> matchesText(editorElement.literalValue(), constraint);
        };

        return constraint.negated() != rawMatch;
    }

    private boolean matchesBoolean(boolean value, @NotNull EditorRuleConstraint constraint) {
        return matchesText(Boolean.toString(value), constraint);
    }

    private boolean matchesInteger(@Nullable Integer value, @NotNull EditorRuleConstraint constraint) {
        return value != null && matchesText(Integer.toString(value), constraint);
    }

    private boolean matchesAny(@NotNull Set<String> values, @NotNull EditorRuleConstraint constraint) {
        if (values.isEmpty()) {
            return false;
        }
        for (String value : values) {
            if (matchesText(value, constraint)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesText(@Nullable String actual, @NotNull EditorRuleConstraint constraint) {
        if (actual == null) {
            return false;
        }

        return switch (constraint.matchMode()) {
            case EXACT -> actual.equals(constraint.value());
            case PREFIX -> actual.startsWith(constraint.value());
            case SUFFIX -> actual.endsWith(constraint.value());
            case CONTAINS -> actual.contains(constraint.value());
            case REGEX -> regexMatch(actual, constraint.value());
        };
    }

    private boolean regexMatch(@NotNull String actual, @NotNull String expression) {
        try {
            return Pattern.compile(expression).matcher(actual).matches();
        } catch (PatternSyntaxException ignored) {
            return false;
        }
    }
}
