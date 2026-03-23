package de.marhali.easyi18n.idea.assistance.java;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLiteralExpression;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nKeyLocalInspection;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class JavaI18nKeyLocalInspection extends AbstractI18nKeyLocalInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitLiteralExpression(@NotNull PsiLiteralExpression literal) {
                Object value = literal.getValue();
                if (!(value instanceof String key) || key.isBlank()) {
                    return;
                }

                JavaEditorElementExtractor extractor = new JavaEditorElementExtractor();
                EditorElement editorElement = extractor.extract(literal, literal.getContainingFile());

                checkI18nLiteral(literal, key, editorElement, literal.getContainingFile(), holder);
            }
        };
    }
}
