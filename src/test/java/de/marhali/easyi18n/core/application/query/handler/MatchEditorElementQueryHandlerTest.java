package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.query.MatchEditorElementQuery;
import de.marhali.easyi18n.core.application.service.CachedModuleRules;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * Unit tests for {@link MatchEditorElementQueryHandler}.
 *
 * @author marhali
 */
public class MatchEditorElementQueryHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");

    private MatchEditorElementQueryHandler buildHandler(List<EditorRule> rules) {
        var module = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(MODULE_ID)
            .editorRules(rules)
            .build();
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder().modules(List.of(module)).build()
        );
        return new MatchEditorElementQueryHandler(new CachedModuleRules(projectConfigPort));
    }

    @Test
    public void test_no_rules_returns_false() {
        var handler = buildHandler(List.of());
        var element = EditorElement.builder(
            EditorLanguage.JAVA, LiteralKind.STRING, TriggerKind.CALL_ARGUMENT, "greeting"
        ).filePath("src/Main.java").build();

        Boolean result = handler.handle(new MatchEditorElementQuery(MODULE_ID, element));

        Assert.assertFalse(result);
    }

    @Test
    public void test_matching_rule_returns_true() {
        var rule = new EditorRule(
            "test-rule",
            Set.of(EditorLanguage.JAVA),
            TriggerKind.CALL_ARGUMENT,
            List.of(
                EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_FQN, "com.acme.i18n.Messages.get"),
                EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0"),
                EditorRuleConstraint.match(RuleConstraintType.TEXT_PATTERN, "^[a-z0-9_.-]+$", TextMatchMode.REGEX)
            ),
            100,
            false
        );
        var handler = buildHandler(List.of(rule));
        var element = EditorElement.builder(
            EditorLanguage.JAVA, LiteralKind.STRING, TriggerKind.CALL_ARGUMENT, "checkout.success"
        ).callableFqn("com.acme.i18n.Messages.get").argumentIndex(0).filePath("src/Main.java").build();

        Boolean result = handler.handle(new MatchEditorElementQuery(MODULE_ID, element));

        Assert.assertTrue(result);
    }

    @Test
    public void test_non_matching_element_returns_false() {
        var rule = new EditorRule(
            "test-rule",
            Set.of(EditorLanguage.JAVA),
            TriggerKind.CALL_ARGUMENT,
            List.of(
                EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_FQN, "com.acme.i18n.Messages.get")
            ),
            100,
            false
        );
        var handler = buildHandler(List.of(rule));
        var element = EditorElement.builder(
            EditorLanguage.JAVA, LiteralKind.STRING, TriggerKind.CALL_ARGUMENT, "some.value"
        ).callableFqn("com.other.Service.method").filePath("src/Main.java").build();

        Boolean result = handler.handle(new MatchEditorElementQuery(MODULE_ID, element));

        Assert.assertFalse(result);
    }
}
