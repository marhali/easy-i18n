package de.marhali.easyi18n.idea.assistance.python;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nDocumentationTargetProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author marhali
 */
public class PythonI18nDocumentationTargetProvider extends AbstractI18nDocumentationTargetProvider {

    @Override
    protected @Nullable EditorElement extractEditorElement(@NotNull PsiElement leaf, @NotNull PsiFile file) {
        PyStringLiteralExpression literal = PsiTreeUtil.getParentOfType(leaf, PyStringLiteralExpression.class, false);
        if (literal == null) {
            return null;
        }
        return new PythonEditorElementExtractor().extract(literal, file);
    }
}
