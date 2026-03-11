package de.marhali.easyi18n.idea.assistance;

import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * {@link de.marhali.easyi18n.core.domain.model.I18nKey} reference for psi elements.
 *
 * @param <T> Psi element type
 *
 * @author marhali
 */
public final class I18nKeyPsiReference<T extends PsiElement> extends PsiReferenceBase<T> {

    private final @NotNull ModuleId moduleId;
    private final @NotNull I18nEntryPreview entryPreview;

    public I18nKeyPsiReference(@NotNull T element, @NotNull ModuleId moduleId, @NotNull I18nEntryPreview entryPreview) {
        super(element, ElementManipulators.getValueTextRange(element), true);

        this.moduleId = moduleId;
        this.entryPreview = entryPreview;
    }

    @Override
    public PsiElement resolve() {
        return new I18nKeyPsiElement(myElement, moduleId, entryPreview);
    }

    @Override
    public Object @NotNull [] getVariants() {
        return super.getVariants();
    }
}
