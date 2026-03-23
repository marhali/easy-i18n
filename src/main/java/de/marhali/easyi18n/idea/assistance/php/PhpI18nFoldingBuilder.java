package de.marhali.easyi18n.idea.assistance.php;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.marhali.easyi18n.idea.assistance.AbstractI18nFoldingBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class PhpI18nFoldingBuilder extends AbstractI18nFoldingBuilder {

    @Override
    protected void collectLiterals(@NotNull PsiElement root, @NotNull LiteralConsumer consumer) {
        root.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);

                if (!(element instanceof StringLiteralExpression literal)) {
                    return;
                }

                String key = literal.getContents();
                if (key == null || key.isBlank()) {
                    return;
                }

                consumer.accept(key, literal.getNode(), literal.getTextRange());
            }
        });
    }
}
