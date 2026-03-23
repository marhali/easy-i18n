package de.marhali.easyi18n.idea.assistance.xml;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.xml.XmlAttributeValue;
import de.marhali.easyi18n.idea.assistance.AbstractI18nFoldingBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class XmlI18nFoldingBuilder extends AbstractI18nFoldingBuilder {

    @Override
    protected void collectLiterals(@NotNull PsiElement root, @NotNull LiteralConsumer consumer) {
        root.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);

                if (!(element instanceof XmlAttributeValue attributeValue)) {
                    return;
                }

                String key = attributeValue.getValue();
                if (key == null || key.isBlank()) {
                    return;
                }

                TextRange attributeValueTextRange = attributeValue.getTextRange();
                consumer.accept(key, attributeValue.getNode(), attributeValueTextRange);
            }
        });
    }
}
