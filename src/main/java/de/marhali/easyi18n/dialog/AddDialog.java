package de.marhali.easyi18n.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import de.marhali.easyi18n.model.action.TranslationCreate;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.model.action.TranslationUpdate;
import de.marhali.easyi18n.settings.ProjectSettingsService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Dialog to create a new translation with all associated locale values.
 * Supports optional prefill technique for translation key or locale value.
 * @author marhali
 */
public class AddDialog extends TranslationDialog {

    private Consumer<String> onCreated;

    /**
     * Constructs a new create dialog with prefilled fields
     * @param project Opened project
     * @param prefillKey Prefill translation key
     * @param prefillLocale  Prefill preview locale value
     */
    public AddDialog(@NotNull Project project, @Nullable KeyPath prefillKey, @Nullable String prefillLocale) {
        super(project, new Translation(prefillKey != null ? prefillKey : new KeyPath(),
                        prefillLocale != null
                                ? new TranslationValue(ProjectSettingsService.get(project).getState().getPreviewLocale(), prefillLocale)
                                : null)
        );

        setTitle(bundle.getString("action.add"));
    }
    public AddDialog(@NotNull Project project, @Nullable KeyPath prefillKey, @Nullable String prefillLocale,Consumer<String> onCreated) {
        super(project, new Translation(prefillKey != null ? prefillKey : new KeyPath(),
                prefillLocale != null
                        ? new TranslationValue(ProjectSettingsService.get(project).getState().getPreviewLocale(), prefillLocale)
                        : null)
        );

        this.onCreated = onCreated;
        setTitle(bundle.getString("action.add"));
    }

    /**
     * Constructs a new create dialog without prefilled fields.
     * @param project Opened project
     */
    public AddDialog(@NotNull Project project) {
        this(project, new KeyPath(), "");
    }

    @Override
    protected @Nullable TranslationUpdate handleExit(int exitCode) {
        if(exitCode == DialogWrapper.OK_EXIT_CODE) {
            if(onCreated != null) {
                onCreated.accept(this.getKeyField().getText());
            }

            return new TranslationCreate(getState());
        }

        return null;
    }
}
