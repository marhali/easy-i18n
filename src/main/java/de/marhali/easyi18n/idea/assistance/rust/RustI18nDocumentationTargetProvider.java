package de.marhali.easyi18n.idea.assistance.rust;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nDocumentationTargetProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rust.lang.core.psi.RsLitExpr;

/**
 * @author marhali
 */
public class RustI18nDocumentationTargetProvider extends AbstractI18nDocumentationTargetProvider {

    @Override
    protected @Nullable EditorElement extractEditorElement(@NotNull PsiElement leaf, @NotNull PsiFile file) {
        RsLitExpr literal = PsiTreeUtil.getParentOfType(leaf, RsLitExpr.class, false);
        if (literal == null) {
            return null;
        }
        return new RustEditorElementExtractor().extract(literal, file);
    }
}
