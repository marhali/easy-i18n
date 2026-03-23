package de.marhali.easyi18n.idea.assistance.ruby;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nKeyLocalInspection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;

/**
 * @author marhali
 */
public class RubyI18nKeyLocalInspection extends AbstractI18nKeyLocalInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (!(element instanceof RStringLiteral literal)) {
                    return;
                }

                String key = RubyEditorElementExtractor.getStringContent(literal);
                if (key == null || key.isBlank()) {
                    return;
                }

                RubyEditorElementExtractor extractor = new RubyEditorElementExtractor();
                EditorElement editorElement = extractor.extract(literal, literal.getContainingFile());

                checkI18nLiteral(literal, key, editorElement, literal.getContainingFile(), holder);
            }
        };
    }
}
