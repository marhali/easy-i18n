package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.cqrs.SynchronousQueryHandler;
import de.marhali.easyi18n.core.application.query.EditorElementI18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.service.CachedModuleRules;
import de.marhali.easyi18n.core.application.service.I18nKeyCandidateResolver;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.I18nRuleEngine;
import de.marhali.easyi18n.core.domain.rules.RuleMatch;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Query handler for {@link EditorElementI18nEntryPreviewQuery}.
 *
 * @author marhali
 */
public class EditorElementI18nEntryPreviewQueryHandler
    implements SynchronousQueryHandler<EditorElementI18nEntryPreviewQuery, Optional<I18nEntryPreview>> {

    private final @NotNull I18nStore store;
    private final @NotNull CachedModuleRules cachedModuleRules;
    private final @NotNull I18nKeyCandidateResolver keyResolver;
    private final @NotNull ProjectConfigPort projectConfigPort;

    public EditorElementI18nEntryPreviewQueryHandler(
        @NotNull I18nStore store,
        @NotNull CachedModuleRules cachedModuleRules,
        @NotNull I18nKeyCandidateResolver keyResolver, @NotNull ProjectConfigPort projectConfigPort
    ) {
        this.store = store;
        this.cachedModuleRules = cachedModuleRules;
        this.keyResolver = keyResolver;
        this.projectConfigPort = projectConfigPort;
    }

    @Override
    public @NotNull PossiblyUnavailable<Optional<I18nEntryPreview>> handle(@NotNull EditorElementI18nEntryPreviewQuery query) {
        ModuleId moduleId = query.moduleId();
        EditorElement editorElement = query.editorElement();

        if (!store.getSnapshot().hasModule(moduleId)) {
            // No module snapshot available in store
            return PossiblyUnavailable.unavailable();
        }

        I18nRuleEngine ruleEngine = cachedModuleRules.resolve(moduleId);

        RuleMatch match = ruleEngine.match(editorElement);

        if (!match.matched()) {
            // Defined editor rules are not applicable to editor element
            return PossiblyUnavailable.available(Optional.empty());
        }

        I18nEntry entry = keyResolver.resolveExact(moduleId, new I18nKeyCandidate(editorElement.literalValue()));
        LocaleId previewLocaleId = projectConfigPort.read().previewLocale();

        return PossiblyUnavailable.available(entry != null
            ? Optional.of(I18nEntryPreview.fromEntry(entry, previewLocaleId))
            : Optional.empty()
        );
    }
}
