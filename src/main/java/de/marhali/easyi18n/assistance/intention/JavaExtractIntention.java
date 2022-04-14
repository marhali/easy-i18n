package de.marhali.easyi18n.assistance.intention;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.psi.*;
import com.siyeh.ipp.base.PsiElementPredicate;
import org.jetbrains.annotations.NotNull;

/**
 * Java specific translation key intention.
 * @author marhali
 */
public class JavaExtractIntention extends AbstractExtractIntention {

    @Override
    protected @IntentionName String getTextForElement(PsiElement element) {
        return getTextForElement(element.getProject(), (String) ((PsiLiteralExpression) element).getValue());
    }

    @Override
    protected void processIntention(@NotNull PsiElement element) {
        System.out.println("proci2");
        if(!(element instanceof PsiLiteralExpression)
                || !(((PsiLiteralExpression) element).getValue() instanceof String)) {
            return;
        }

        extractTranslation(element.getProject(), (String) ((PsiLiteralExpression) element).getValue(), element);
    }

    @Override
    protected @NotNull PsiElementPredicate getElementPredicate() {
        System.out.println("predi2");
        return element -> element instanceof PsiLiteralExpression;
    }
}
