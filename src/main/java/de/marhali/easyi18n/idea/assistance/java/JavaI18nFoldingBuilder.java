package de.marhali.easyi18n.idea.assistance.java;

import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import de.marhali.easyi18n.idea.assistance.AbstractI18nFoldingBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class JavaI18nFoldingBuilder extends AbstractI18nFoldingBuilder {

    @Override
    protected void collectLiterals(@NotNull PsiElement root, @NotNull LiteralConsumer consumer) {
        root.accept(new JavaRecursiveElementWalkingVisitor() {
            @Override
            public void visitLiteralExpression(@NotNull PsiLiteralExpression literal) {
                super.visitLiteralExpression(literal);

                Object rawValue = literal.getValue();
                if (!(rawValue instanceof String key) || key.isBlank()) {
                    return;
                }

                consumer.accept(key, literal.getNode(), literal.getTextRange());
            }
        });
    }
}
