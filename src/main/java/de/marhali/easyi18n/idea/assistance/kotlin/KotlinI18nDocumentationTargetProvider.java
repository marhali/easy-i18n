package de.marhali.easyi18n.idea.assistance.kotlin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nDocumentationTargetProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtStringTemplateExpression;

/**
 * @author marhali
 */
public class KotlinI18nDocumentationTargetProvider extends AbstractI18nDocumentationTargetProvider {

    @Override
    protected @Nullable EditorElement extractEditorElement(@NotNull PsiElement leaf, @NotNull PsiFile file) {
        KtStringTemplateExpression literal = PsiTreeUtil.getParentOfType(leaf, KtStringTemplateExpression.class, false);
        if (literal == null) {
            return null;
        }
        return new KotlinEditorElementExtractor().extract(literal, file);
    }
}
