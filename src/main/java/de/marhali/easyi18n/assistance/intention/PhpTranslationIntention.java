package de.marhali.easyi18n.assistance.intention;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Php specific translation intention
 * @author marhali
 */
public class PhpTranslationIntention extends AbstractTranslationIntention {
    @Override
    protected @Nullable String extractText(@NotNull PsiElement element) {
        if(!(element.getParent() instanceof StringLiteralExpression)) {
            return null;
        }

        return ((StringLiteralExpression) element.getParent()).getContents();
    }
}
