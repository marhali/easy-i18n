package de.marhali.easyi18n.idea.assistance.kotlin;

import com.intellij.psi.PsiElement;
import de.marhali.easyi18n.idea.assistance.AbstractI18nFoldingBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;
import org.jetbrains.kotlin.psi.KtStringTemplateEntry;
import org.jetbrains.kotlin.psi.KtStringTemplateExpression;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;

/**
 * @author marhali
 */
public class KotlinI18nFoldingBuilder extends AbstractI18nFoldingBuilder {

    @Override
    protected void collectLiterals(@NotNull PsiElement root, @NotNull LiteralConsumer consumer) {
        root.accept(new KtTreeVisitorVoid() {
            @Override
            public void visitStringTemplateExpression(@NotNull KtStringTemplateExpression literal) {
                super.visitStringTemplateExpression(literal);

                String key = extractSimpleStringValue(literal);
                if (key == null || key.isBlank()) {
                    return;
                }

                consumer.accept(key, literal.getNode(), literal.getTextRange());
            }
        });
    }

    private @Nullable String extractSimpleStringValue(@NotNull KtStringTemplateExpression literal) {
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
}
