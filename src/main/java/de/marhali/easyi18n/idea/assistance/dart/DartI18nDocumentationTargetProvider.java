package de.marhali.easyi18n.idea.assistance.dart;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.lang.dart.psi.DartStringLiteralExpression;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nDocumentationTargetProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author marhali
 */
public class DartI18nDocumentationTargetProvider extends AbstractI18nDocumentationTargetProvider {

    @Override
    protected @Nullable EditorElement extractEditorElement(@NotNull PsiElement leaf, @NotNull PsiFile file) {
        DartStringLiteralExpression literal = PsiTreeUtil.getParentOfType(leaf, DartStringLiteralExpression.class, false);
        if (literal == null) {
            return null;
        }
        return new DartEditorElementExtractor().extract(literal, file);
    }
}
