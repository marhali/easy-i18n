package de.marhali.easyi18n.idea.assistance.java;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.util.PsiTreeUtil;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nDocumentationTargetProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author marhali
 */
public class JavaI18nDocumentationTargetProvider extends AbstractI18nDocumentationTargetProvider {

    @Override
    protected @Nullable EditorElement extractEditorElement(@NotNull PsiElement leaf, @NotNull PsiFile file) {
        PsiLiteralExpression literal = PsiTreeUtil.getParentOfType(leaf, PsiLiteralExpression.class, false);
        if (literal == null) {
            return null;
        }
        return new JavaEditorElementExtractor().extract(literal, file);
    }
}
