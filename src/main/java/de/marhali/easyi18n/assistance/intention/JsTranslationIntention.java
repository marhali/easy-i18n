package de.marhali.easyi18n.assistance.intention;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * JavaScript specific translation key intention.
 * @author marhali
 */
public class JsTranslationIntention extends AbstractTranslationIntention {
    @Override
    protected @Nullable String extractText(@NotNull PsiElement element) {
        if(!(element.getParent() instanceof JSLiteralExpression)) {
            return null;
        }

        return ((JSLiteralExpression) element.getParent()).getStringValue();
    }

    @Override
    @NotNull TextRange convertRange(@NotNull TextRange input) {
        return new TextRange(input.getStartOffset() + 1, input.getEndOffset() - 1);
    }
}
