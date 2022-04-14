package de.marhali.easyi18n.assistance.intention;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiElement;
import com.siyeh.ipp.base.PsiElementPredicate;
import org.jetbrains.annotations.NotNull;

/**
 * JavaScript specific translation key intention.
 * @author marhali
 */
// TODO: current implementation does not support other languages than Java
public class JsExtractIntention extends AbstractExtractIntention {

    @Override
    public @NotNull String getFamilyName() {
        return "JavaScript";
    }

    @Override
    protected @IntentionName String getTextForElement(PsiElement element) {
        return "HILFE";
        //return getTextForElement(element.getProject(), ((JSLiteralExpression) element).getStringValue());
    }

    @Override
    protected void processIntention(@NotNull PsiElement element) {
        if(!(element instanceof JSLiteralExpression) || ((JSLiteralExpression) element).getStringValue() == null) {
            return;
        }

        extractTranslation(element.getProject(), ((JSLiteralExpression) element).getStringValue(), element);
    }

    @Override
    protected @NotNull PsiElementPredicate getElementPredicate() {
        return element -> true;
    }
}
