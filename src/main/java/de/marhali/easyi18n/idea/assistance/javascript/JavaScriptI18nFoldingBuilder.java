package de.marhali.easyi18n.idea.assistance.javascript;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSRecursiveWalkingElementVisitor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.AbstractI18nFoldingBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class JavaScriptI18nFoldingBuilder extends AbstractI18nFoldingBuilder {

    private final EditorLanguage language;

    public JavaScriptI18nFoldingBuilder() {
        this(EditorLanguage.JAVASCRIPT);
    }

    protected JavaScriptI18nFoldingBuilder(EditorLanguage language) {
        this.language = language;
    }

    @Override
    protected void collectLiterals(@NotNull PsiElement root, @NotNull LiteralConsumer consumer) {
        // Skip injected language fragments (synthetic VirtualFiles) because those are handled by
        // VueTemplateI18nFoldingBuilder to avoid coordinate-space mismatches.
        VirtualFile vf = root.getContainingFile().getVirtualFile();
        if (vf != null && !vf.isInLocalFileSystem()) return;

        root.accept(new JSRecursiveWalkingElementVisitor() {
            @Override
            public void visitJSLiteralExpression(@NotNull JSLiteralExpression literal) {
                super.visitJSLiteralExpression(literal);

                if (!literal.isStringLiteral()) {
                    return;
                }

                String key = literal.getStringValue();
                if (key == null || key.isBlank()) {
                    return;
                }

                consumer.accept(key, literal.getNode(), literal.getTextRange());
            }
        });
    }
}
