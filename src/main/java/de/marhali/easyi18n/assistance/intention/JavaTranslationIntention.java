package de.marhali.easyi18n.assistance.intention;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Java specific translation intention.
 * @author marhali
 */
public class JavaTranslationIntention extends AbstractTranslationIntention {
    @Override
    protected @Nullable String extractText(@NotNull PsiElement element) {
        if(!(element.getParent() instanceof PsiLiteralExpression)) {
            return null;
        }

        return String.valueOf(((PsiLiteralExpression) element.getParent()).getValue());
    }

    @Override
    @NotNull TextRange convertRange(@NotNull TextRange input) {
        return new TextRange(input.getStartOffset() + 1, input.getEndOffset() - 1);
    }
}
