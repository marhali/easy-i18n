package de.marhali.easyi18n.idea.assistance.python;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nKeyLocalInspection;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class PythonI18nKeyLocalInspection extends AbstractI18nKeyLocalInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (!(element instanceof PyStringLiteralExpression literal)) {
                    return;
                }

                String key = PythonEditorElementExtractor.getStringContent(literal);
                if (key == null || key.isBlank()) {
                    return;
                }

                PythonEditorElementExtractor extractor = new PythonEditorElementExtractor();
                EditorElement editorElement = extractor.extract(literal, literal.getContainingFile());

                checkI18nLiteral(literal, key, editorElement, literal.getContainingFile(), holder);
            }
        };
    }
}
