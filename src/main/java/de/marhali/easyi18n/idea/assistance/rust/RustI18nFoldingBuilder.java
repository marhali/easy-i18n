package de.marhali.easyi18n.idea.assistance.rust;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import de.marhali.easyi18n.idea.assistance.AbstractI18nFoldingBuilder;
import org.jetbrains.annotations.NotNull;
import org.rust.lang.core.psi.RsLitExpr;

/**
 * @author marhali
 */
public class RustI18nFoldingBuilder extends AbstractI18nFoldingBuilder {

    @Override
    protected void collectLiterals(@NotNull PsiElement root, @NotNull LiteralConsumer consumer) {
        root.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);

                if (!(element instanceof RsLitExpr literal)) {
                    return;
                }

                if (!RustEditorElementExtractor.isStringLiteral(literal)) {
                    return;
                }

                String key = RustEditorElementExtractor.getStringContent(literal);
                if (key == null || key.isBlank()) {
                    return;
                }

                consumer.accept(key, literal.getNode(), literal.getTextRange());
            }
        });
    }
}
