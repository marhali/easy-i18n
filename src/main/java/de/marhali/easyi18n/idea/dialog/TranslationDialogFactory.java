package de.marhali.easyi18n.idea.dialog;

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.application.query.TranslationByKeyQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntry;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.model.NullableI18nEntry;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.PluginExecutorService;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Factory to instantiate translation dialogs.
 *
 * @author marhali
 */
public final class TranslationDialogFactory {

    /**
     * Instantiates a translation dialog for a new translation.
     * @param project Opened project
     * @param moduleId Module identifier
     * @return {@link TranslationDialog}
     */
    public static @NotNull TranslationDialog createAddDialog(
        @NotNull Project project,
        @NotNull ModuleId moduleId
    ) {
        return new TranslationDialog(project, moduleId, TranslationDialogMode.ADD, NullableI18nEntry.empty());
    }

    /**
     * Instantiates a translation dialog to edit an existing translation.
     * @param project Opened project
     * @param moduleId Module identifier
     * @param originEntry Translation to edit
     * @return {@link TranslationDialog}
     */
    public static @NotNull TranslationDialog createEditDialog(
        @NotNull Project project,
        @NotNull ModuleId moduleId,
        @NotNull I18nEntry originEntry
    ) {
        return new TranslationDialog(project, moduleId, TranslationDialogMode.EDIT, NullableI18nEntry.from(originEntry));
    }

    /**
     * Instantiates a translation dialog to edit an existing translation.
     * @param project IntelliJ project
     * @param moduleId Module identifier
     * @param key Translation key
     * @param onDialogConsumer Callback with instantiated translation dialog
     */
    public static void createEditDialog(
        @NotNull Project project,
        @NotNull ModuleId moduleId,
        @NotNull I18nKey key,
        @NotNull Consumer<@NotNull TranslationDialog> onDialogConsumer
        ) {
        I18nProjectService projectService = project.getService(I18nProjectService.class);
        PluginExecutorService executorService = project.getService(PluginExecutorService.class);

        executorService.runAsync(
            () -> projectService.query(new TranslationByKeyQuery(moduleId, key)),
            (optionalContent) -> {
                // TODO: how should be handle empty result? (missing translation might be only rare edge case)
                optionalContent.ifPresent((content) -> onDialogConsumer.accept(
                    createEditDialog(project, moduleId, new I18nEntry(key, content))
                ));
            },
            (throwable) -> throwable.printStackTrace(), // TODO: ex handling
            ModalityState.defaultModalityState(),
            project.getDisposed()
        );
    }
}
