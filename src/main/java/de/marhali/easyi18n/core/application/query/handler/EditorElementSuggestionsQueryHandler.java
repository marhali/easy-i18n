package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.cqrs.SynchronousQueryHandler;
import de.marhali.easyi18n.core.application.query.EditorElementSuggestionsQuery;
import de.marhali.easyi18n.core.application.service.CachedModuleRules;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.I18nRuleEngine;
import de.marhali.easyi18n.core.domain.rules.RuleMatch;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Query handler for {@link EditorElementSuggestionsQuery}.
 *
 * @author marhali
 */
public class EditorElementSuggestionsQueryHandler
    implements SynchronousQueryHandler<EditorElementSuggestionsQuery, Optional<List<I18nEntryPreview>>> {

    private final @NotNull I18nStore store;
    private final @NotNull CachedModuleRules cachedModuleRules;
    private final @NotNull ProjectConfigPort projectConfigPort;

    public EditorElementSuggestionsQueryHandler(@NotNull I18nStore store, @NotNull CachedModuleRules cachedModuleRules, @NotNull ProjectConfigPort projectConfigPort) {
        this.store = store;
        this.cachedModuleRules = cachedModuleRules;
        this.projectConfigPort = projectConfigPort;
    }

    @Override
    public @NotNull PossiblyUnavailable<Optional<List<I18nEntryPreview>>> handle(@NotNull EditorElementSuggestionsQuery query) {
        ModuleId moduleId = query.moduleId();
        EditorElement editorElement = query.editorElement();

        if (!store.getSnapshot().hasModule(moduleId)) {
            return PossiblyUnavailable.unavailable();
        }

        I18nRuleEngine ruleEngine = cachedModuleRules.resolve(moduleId);

        RuleMatch match = ruleEngine.match(editorElement);

        if (!match.matched()) {
            // Defined editor rules are not applicable to editor element
            return PossiblyUnavailable.available(Optional.empty());
        }

        ProjectConfig projectConfig = projectConfigPort.read();
        LocaleId previewLocaleId = projectConfig.previewLocale();
        Set<I18nKeyPrefix> defaultKeyPrefixes = projectConfig.modules().get(moduleId).defaultKeyPrefixes();

        I18nModule moduleStore = store.getSnapshot().getModuleOrThrow(moduleId);
        List<I18nEntryPreview> entries = new ArrayList<>();

        for (Map.Entry<@NotNull I18nKey, @NotNull I18nContent> entry : moduleStore.translations().entrySet()) {
            entries.add(I18nEntryPreview.fromEntry(I18nEntry.fromEntry(entry), previewLocaleId));

            for (I18nKeyPrefix defaultKeyPrefix : defaultKeyPrefixes) {
                if (defaultKeyPrefix.isPrefixed(entry.getKey())) {
                    entries.add(new I18nEntryPreview(
                        defaultKeyPrefix.withoutPrefix(entry.getKey()),
                        entry.getValue().values().get(previewLocaleId)
                    ));
                }
            }
        }

        return PossiblyUnavailable.available(Optional.of(entries));
    }
}
