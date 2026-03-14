package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.QueryHandler;
import de.marhali.easyi18n.core.application.query.MatchEditorElementQuery;
import de.marhali.easyi18n.core.application.service.CachedModuleRules;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.I18nRuleEngine;
import de.marhali.easyi18n.core.domain.rules.RuleMatch;
import org.jetbrains.annotations.NotNull;

/**
 * Query handler for {@link MatchEditorElementQuery}
 *
 * @author marhali
 */
public class MatchEditorElementQueryHandler implements QueryHandler<MatchEditorElementQuery, Boolean> {

    private final @NotNull CachedModuleRules cachedModuleRules;

    public MatchEditorElementQueryHandler(@NotNull CachedModuleRules cachedModuleRules) {
        this.cachedModuleRules = cachedModuleRules;
    }

    @Override
    public @NotNull Boolean handle(@NotNull MatchEditorElementQuery query) {
        ModuleId moduleId = query.moduleId();
        EditorElement editorElement = query.editorElement();

        I18nRuleEngine ruleEngine = cachedModuleRules.resolve(moduleId);

        RuleMatch match = ruleEngine.match(editorElement);

        return match.matched();
    }
}
