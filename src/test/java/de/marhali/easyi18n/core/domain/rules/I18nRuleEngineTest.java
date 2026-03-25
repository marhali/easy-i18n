package de.marhali.easyi18n.core.domain.rules;


import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * @author marhali
 */
public class I18nRuleEngineTest {
    @Test
    public void test() {
        List<EditorRule> rules = List.of(
            new EditorRule(
                "java-messages-get-arg0",
                Set.of(EditorLanguage.JAVA),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_FQN, "com.acme.i18n.Messages.get"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0"),
                    EditorRuleConstraint.match(RuleConstraintType.TEXT_PATTERN, "^[a-z0-9_.-]+$", TextMatchMode.REGEX)
                ),
                100,
                false
            ),
            new EditorRule(
                "exclude-human-sentences",
                Set.of(),
                TriggerKind.UNKNOWN,
                List.of(
                    EditorRuleConstraint.match(RuleConstraintType.TEXT_PATTERN, ".*\\s+.*", TextMatchMode.REGEX)
                ),
                1000,
                true
            )
        );

        RuleCompiler compiler = new RuleCompiler();
        CompiledRules compiledRules = compiler.compile(rules);
        I18nRuleEngine engine = new I18nRuleEngine(compiledRules);

        EditorElement facts = EditorElement.builder(
                EditorLanguage.JAVA,
                LiteralKind.STRING,
                TriggerKind.CALL_ARGUMENT,
                "checkout.success"
            )
            .callableFqn("com.acme.i18n.Messages.get")
            .argumentIndex(0)
            .build();

        RuleMatch match = engine.match(facts);
        boolean isI18nLiteral = match.matched();
        Assert.assertTrue(isI18nLiteral);
    }
}
