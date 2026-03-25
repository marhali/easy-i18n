package de.marhali.easyi18n.idea.assistance;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.*;
import com.intellij.psi.impl.FakePsiElement;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.dialog.TranslationDialogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link FakePsiElement} that represents an {@link de.marhali.easyi18n.core.domain.model.I18nKey}.
 *
 * @author marhali
 */
public class I18nKeyPsiElement extends FakePsiElement implements SyntheticElement {

    private final @NotNull Project project;
    private final @NotNull SmartPsiElementPointer<PsiElement> contextPointer;
    private final @NotNull ModuleId moduleId;
    private final @NotNull I18nEntryPreview entryPreview;

    public I18nKeyPsiElement(
        @NotNull PsiElement context,
        @NotNull ModuleId moduleId,
        @NotNull I18nEntryPreview entryPreview
    ) {
        this.project = context.getProject();
        this.contextPointer = SmartPointerManager.getInstance(project).createSmartPsiElementPointer(context);
        this.moduleId = moduleId;
        this.entryPreview = entryPreview;
    }

    @Override
    public String getName() {
        return entryPreview.key().canonical();
    }

    @Override
    public String getPresentableText() {
        return "I18nKey: " + entryPreview.key().canonical();
    }

    @Override
    public @NlsSafe @Nullable String getLocationString() {
        PsiFile file = getContainingFile();
        return file != null ? file.getVirtualFile().getPath() : null;
    }

    @Override
    public PsiElement getParent() {
        return contextPointer.getElement();
    }

    @Override
    public PsiFile getContainingFile() {
        PsiElement context = contextPointer.getElement();
        return context != null ? context.getContainingFile() : null;
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    @Override
    public void navigate(boolean requestFocus) {
        TranslationDialogFactory.createEditDialog(
            project,
            moduleId,
            entryPreview.key(),
            DialogWrapper::show
        );
    }

    @Override
    public boolean isValid() {
        return !project.isDisposed() && contextPointer.getElement() != null;
    }

    public @NotNull I18nEntryPreview getEntryPreview() {
        return entryPreview;
    }
}
