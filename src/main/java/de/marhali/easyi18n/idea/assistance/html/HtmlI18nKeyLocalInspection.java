package de.marhali.easyi18n.idea.assistance.html;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.xml.XmlAttributeValue;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nKeyLocalInspection;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class HtmlI18nKeyLocalInspection extends AbstractI18nKeyLocalInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new XmlElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (!(element instanceof XmlAttributeValue attributeValue)) {
                    return;
                }

                String key = attributeValue.getValue();
                if (key.isBlank()) {
                    return;
                }

                HtmlEditorElementExtractor extractor = new HtmlEditorElementExtractor();
                EditorElement editorElement = extractor.extract(attributeValue, attributeValue.getContainingFile());

                checkI18nLiteral(attributeValue, key, editorElement, attributeValue.getContainingFile(), holder);
            }
        };
    }
}
