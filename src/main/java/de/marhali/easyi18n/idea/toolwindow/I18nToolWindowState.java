package de.marhali.easyi18n.idea.toolwindow;

import de.marhali.easyi18n.core.application.query.view.ModuleViewOptions;
import de.marhali.easyi18n.core.application.query.view.ModuleViewType;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tracked state for a translations tool window.
 *
 * @author marhali
 */
public class I18nToolWindowState {
    public @Nullable String filterBySearchQuery;
    public @Nullable Boolean filterByMissingValues;
    public @Nullable Boolean filterByDuplicateValues;
    public @Nullable Boolean filterByMissingComments;
    public @Nullable Boolean showAsTree;
    public @Nullable ModuleId selectedModuleId;

    /**
     * Default state to apply for newly instantiated tool windows.
     * @return {@link I18nToolWindowState}
     */
    public static @NotNull I18nToolWindowState fromDefaultState() {
        var state = new I18nToolWindowState();

        state.filterBySearchQuery = "";
        state.filterByMissingValues = false;
        state.filterByDuplicateValues = false;
        state.filterByMissingComments = false;
        state.showAsTree = false;
        state.selectedModuleId = null;

        return state;
    }

    public I18nToolWindowState() {}

    /**
     * Transforms the tool window state to {@link ModuleViewOptions}.
     * @return {@link ModuleViewOptions}
     */
    public @NotNull ModuleViewOptions toModuleViewOptions() {
        return new ModuleViewOptions(
            Boolean.TRUE.equals(showAsTree) ? ModuleViewType.TREE : ModuleViewType.TABLE,
            filterBySearchQuery,
            Boolean.TRUE.equals(filterByMissingValues),
            Boolean.TRUE.equals(filterByDuplicateValues),
            Boolean.TRUE.equals(filterByMissingComments)
        );
    }

    @Override
    public String toString() {
        return "I18nToolWindowState{" +
            "filterBySearchQuery='" + filterBySearchQuery + '\'' +
            ", filterByMissingValues=" + filterByMissingValues +
            ", filterByDuplicateValues=" + filterByDuplicateValues +
            ", filterByMissingComments=" + filterByMissingComments +
            ", showAsTree=" + showAsTree +
            ", selectedModuleId=" + selectedModuleId +
            '}';
    }
}
