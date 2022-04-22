package de.marhali.easyi18n.assistance.intention;

import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;

/**
 * Kotlin specific translation key intention.
 * @author marhali
 */
public class KtTranslationIntention extends AbstractTranslationIntention {
    @Override
    protected @Nullable String extractText(@NotNull PsiElement element) {
        if(!(element.getParent() instanceof KtLiteralStringTemplateEntry)) {
            return null;
        }

        KtLiteralStringTemplateEntry expression = (KtLiteralStringTemplateEntry) element.getParent();
        return expression.getText();
    }
}
