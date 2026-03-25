package de.marhali.easyi18n.idea.assistance.ruby;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nDocumentationTargetProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;

/**
 * @author marhali
 */
public class RubyI18nDocumentationTargetProvider extends AbstractI18nDocumentationTargetProvider {

    @Override
    protected @Nullable EditorElement extractEditorElement(@NotNull PsiElement leaf, @NotNull PsiFile file) {
        RStringLiteral literal = PsiTreeUtil.getParentOfType(leaf, RStringLiteral.class, false);
        if (literal == null) {
            return null;
        }
        return new RubyEditorElementExtractor().extract(literal, file);
    }
}
