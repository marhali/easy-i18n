package de.marhali.easyi18n.core.application.query.view;

import de.marhali.easyi18n.core.domain.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Module view records.
 *
 * @author marhali
 */
public sealed interface ModuleView permits ModuleView.Table, ModuleView.Tree {

    @NotNull ModuleId moduleId();
    @NotNull List<LocaleId> locales();

    record Entry(
        @NotNull I18nKey key,
        @NotNull List<@NotNull String> keyHierarchy,
        @NotNull I18nContent content,
        @NotNull Set<@NotNull LocaleId> missingLocaleIds,
        @NotNull Set<@NotNull LocaleId> duplicateLocaleIds
    ) {}

    record Table (
        @NotNull ModuleId moduleId,
        @NotNull List<@NotNull LocaleId> locales,
        @NotNull List<@NotNull Row> rows
        ) implements ModuleView {
        public record Row (
            @NotNull I18nKey key,
            @NotNull Map<@NotNull LocaleId, @NotNull Cell> cells,
            @NotNull Boolean missingValues,
            @NotNull Boolean duplicateValues
        ) {
            public record Cell(
                @Nullable I18nValue value,
                @NotNull Boolean duplicate
            ) {}
        }
    }

    record Tree (
        @NotNull ModuleId moduleId,
        @NotNull List<LocaleId> locales,
        @NotNull List<ModuleView.@NotNull Entry> entries
    ) implements ModuleView {}
}
