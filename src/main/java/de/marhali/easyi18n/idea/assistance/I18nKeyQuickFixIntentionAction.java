package de.marhali.easyi18n.idea.assistance;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.model.NullableI18nEntry;
import de.marhali.easyi18n.idea.dialog.TranslationDialogFactory;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author marhali
 */
public class I18nKeyQuickFixIntentionAction extends LocalQuickFixAndIntentionActionOnPsiElement {

    private final @NotNull ModuleId moduleId;
    private final @NotNull NullableI18nEntry entry;

    public I18nKeyQuickFixIntentionAction(
        @Nullable PsiElement element,
        @NotNull ModuleId moduleId, @NotNull NullableI18nEntry entry
        ) {
        super(element);

        this.moduleId = moduleId;
        this.entry = entry;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return PluginBundle.message("editor.intention.unresolved.fix");
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @Nullable Editor editor, @NotNull PsiElement psiElement, @NotNull PsiElement psiElement1) {
        TranslationDialogFactory.createAddDialog(
            project,
            moduleId,
            entry
        ).show();
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return PluginBundle.message("editor.intention.family");
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
