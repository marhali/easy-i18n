package de.marhali.easyi18n.idea.assistance.kotlin;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nKeyLocalInspection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;
import org.jetbrains.kotlin.psi.KtStringTemplateEntry;
import org.jetbrains.kotlin.psi.KtStringTemplateExpression;
import org.jetbrains.kotlin.psi.KtVisitorVoid;

/**
 * @author marhali
 */
public class KotlinI18nKeyLocalInspection extends AbstractI18nKeyLocalInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new KtVisitorVoid() {
            @Override
            public void visitStringTemplateExpression(@NotNull KtStringTemplateExpression literal) {
                String key = extractSimpleStringValue(literal);
                if (key == null || key.isBlank()) {
                    return;
                }

                KotlinEditorElementExtractor extractor = new KotlinEditorElementExtractor();
                EditorElement editorElement = extractor.extract(literal, literal.getContainingFile());

                checkI18nLiteral(literal, key, editorElement, literal.getContainingFile(), holder);
            }

            private String extractSimpleStringValue(@NotNull KtStringTemplateExpression literal) {
                KtStringTemplateEntry[] entries = literal.getEntries();
                if (entries.length == 0) {
                    return "";
                }
                StringBuilder sb = new StringBuilder();
                for (KtStringTemplateEntry entry : entries) {
                    if (entry instanceof KtLiteralStringTemplateEntry) {
                        sb.append(entry.getText());
                    } else {
                        // Contains interpolation - not a simple string
                        return null;
                    }
                }
                return sb.toString();
            }
        };
    }
}
