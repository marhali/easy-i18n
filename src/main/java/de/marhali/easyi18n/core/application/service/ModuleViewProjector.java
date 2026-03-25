package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.application.query.view.ModuleViewOptions;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Templates;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Module query view projector.
 *
 * @author marhali
 */
public final class ModuleViewProjector {

    private final @NotNull CachedModuleTemplates cachedModuleTemplates;

    public ModuleViewProjector(@NotNull CachedModuleTemplates cachedModuleTemplates) {
        this.cachedModuleTemplates = cachedModuleTemplates;
    }

    public @NotNull ModuleView project(
        @NotNull ModuleId moduleId,
        @NotNull I18nModule module,
        @NotNull ModuleViewOptions options
    ) {
        Templates templates = cachedModuleTemplates.resolve(moduleId);
        List<@NotNull LocaleId> locales = module.locales().stream().sorted().toList();

        DuplicateValueIndex duplicateValueIndex = new DuplicateValueIndex();
        duplicateValueIndex.rebuild(module.translations());

        var entries = module.translations().entrySet().stream()
            .map((entry) -> toEntry(entry, locales, duplicateValueIndex, templates))
            .filter((entry) -> !options.filterByMissingValues() || !entry.missingLocaleIds().isEmpty())
            .filter((entry) -> !options.filterByDuplicateValues() || !entry.duplicateLocaleIds().isEmpty())
            .filter((entry) -> options.filterBySearchQuery() == null || matchesSearchQuery(entry, options.filterBySearchQuery()))
            .toList();

        return switch (options.type()) {
            case TABLE -> toTableView(moduleId, locales, entries);
            case TREE -> toTreeView(moduleId, locales, entries);
        };
    }

    private @NotNull ModuleView toTableView(@NotNull ModuleId moduleId, @NotNull List<@NotNull LocaleId> locales, List<ModuleView.Entry> entries) {
        var rows = new ArrayList<ModuleView.Table.Row>(entries.size());

        for (ModuleView.Entry entry : entries) {
            var cells = new HashMap<LocaleId, ModuleView.Table.Row.Cell>();

            for (LocaleId localeId : locales) {
                cells.put(localeId, new ModuleView.Table.Row.Cell(entry.content().values().get(localeId), entry.duplicateLocaleIds().contains(localeId)));
            }

            rows.add(new ModuleView.Table.Row(entry.key(), cells, !entry.missingLocaleIds().isEmpty(), !entry.duplicateLocaleIds().isEmpty()));
        }

        return new ModuleView.Table(moduleId, locales, rows);
    }

    private @NotNull ModuleView toTreeView(@NotNull ModuleId moduleId, List<@NotNull LocaleId> locales, @NotNull List<ModuleView.Entry> entries) {
        return new ModuleView.Tree(moduleId, locales, entries);
    }

    private @NotNull ModuleView.Entry toEntry(
        @NotNull Map.Entry<I18nKey, I18nContent> entry,
        @NotNull List<@NotNull LocaleId> locales,
        @NotNull DuplicateValueIndex duplicateValueIndex,
        @NotNull Templates templates
    ) {
        Set<LocaleId> missingLocaleIds = locales.stream()
            .filter((localeId) -> !entry.getValue().hasLocale(localeId))
            .collect(Collectors.toSet());

        Set<LocaleId> duplicateLocaleIds = entry.getValue().values().entrySet().stream()
            .filter(valueEntry -> duplicateValueIndex.hasDuplicates(valueEntry.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());

        List<String> keyHierarchy = templates.key().toHierarchy(entry.getKey());

        return new ModuleView.Entry(entry.getKey(), keyHierarchy, entry.getValue(), missingLocaleIds, duplicateLocaleIds);
    }

    private boolean matchesSearchQuery(@NotNull ModuleView.Entry entry, @NotNull String searchQuery) {
        var normalizedSearchQuery = normalize(searchQuery);

        if (normalizedSearchQuery.isEmpty()) {
            return true;
        }

        if (normalize(entry.key().canonical()).contains(normalizedSearchQuery)) {
            return true;
        }

        for (I18nValue value : entry.content().values().values()) {
            if (normalize(value.toInputString()).contains(normalizedSearchQuery)) {
                return true;
            }
        }

        return false;
    }

    private @NotNull String normalize(@NotNull String input) {
        return input.trim().toLowerCase(Locale.ROOT);
    }
}
