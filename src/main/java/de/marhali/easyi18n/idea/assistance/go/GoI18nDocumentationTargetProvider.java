package de.marhali.easyi18n.idea.assistance.go;

import com.goide.psi.GoStringLiteral;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nDocumentationTargetProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author marhali
 */
public class GoI18nDocumentationTargetProvider extends AbstractI18nDocumentationTargetProvider {

    @Override
    protected @Nullable EditorElement extractEditorElement(@NotNull PsiElement leaf, @NotNull PsiFile file) {
        GoStringLiteral literal = PsiTreeUtil.getParentOfType(leaf, GoStringLiteral.class, false);
        if (literal == null) {
            return null;
        }
        return new GoEditorElementExtractor().extract(literal, file);
    }
}
