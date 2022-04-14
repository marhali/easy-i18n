package de.marhali.easyi18n.assistance.intention;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.psi.PsiElement;
import com.siyeh.ipp.base.PsiElementPredicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;
import org.jetbrains.kotlin.psi.KtStringTemplateExpression;

/**
 * Kotlin specific translation key intention.
 * @author marhali
 */
// TODO: kotlin impl does not work - no action
public class KtExtractIntention extends AbstractExtractIntention {
    @Override
    protected @IntentionName String getTextForElement(PsiElement element) {
        return "hallo";
        //return getTextForElement(element.getProject(), element.getText());
    }

    @Override
    protected void processIntention(@NotNull PsiElement element) {
        System.out.println("Hallo");

        if(!(element instanceof KtStringTemplateExpression)) {
            System.out.println(element.getClass());
            return;
        }

        System.out.println("hallo");

        extractTranslation(element.getProject() , element.getText(), element);
    }

    @Override
    protected @NotNull PsiElementPredicate getElementPredicate() {
        System.out.println("predi");
        return element -> element instanceof KtLiteralStringTemplateEntry;
    }
}
