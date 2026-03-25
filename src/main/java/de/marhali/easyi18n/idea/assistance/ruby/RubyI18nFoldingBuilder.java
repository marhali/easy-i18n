package de.marhali.easyi18n.idea.assistance.ruby;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import de.marhali.easyi18n.idea.assistance.AbstractI18nFoldingBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;

/**
 * @author marhali
 */
public class RubyI18nFoldingBuilder extends AbstractI18nFoldingBuilder {

    @Override
    protected void collectLiterals(@NotNull PsiElement root, @NotNull LiteralConsumer consumer) {
        for (RStringLiteral literal : PsiTreeUtil.findChildrenOfType(root, RStringLiteral.class)) {
            if (RubyEditorElementExtractor.isInterpolatedString(literal)) {
                continue;
            }

            String key = literal.getContentValue();
            if (key == null || key.isBlank()) {
                continue;
            }

            consumer.accept(key, literal.getNode(), literal.getTextRange());
        }
    }
}
