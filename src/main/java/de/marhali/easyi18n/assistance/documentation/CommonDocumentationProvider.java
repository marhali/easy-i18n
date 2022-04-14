package de.marhali.easyi18n.assistance.documentation;

import com.intellij.psi.PsiElement;
import de.marhali.easyi18n.assistance.reference.PsiKeyReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * Language unspecific documentation provider. Every supported language should register an extension to this EP.
 * @author marhali
 */
public class CommonDocumentationProvider extends AbstractDocumentationProvider {

    @Override
    public @Nullable
    @Nls String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if(!(element instanceof PsiKeyReference.TranslationReference)) {
            return null;
        }

        PsiKeyReference.TranslationReference keyReference = (PsiKeyReference.TranslationReference) element;
        String value = keyReference.getName();

        return generateDoc(element.getProject(), value);
    }
}
