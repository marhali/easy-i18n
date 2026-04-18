package de.marhali.easyi18n.idea.assistance.html;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSRecursiveWalkingElementVisitor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlText;
import de.marhali.easyi18n.idea.assistance.AbstractI18nFoldingBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class HtmlI18nFoldingBuilder extends AbstractI18nFoldingBuilder {

    @Override
    protected void collectLiterals(@NotNull PsiElement root, @NotNull LiteralConsumer consumer) {
        InjectedLanguageManager ilm = InjectedLanguageManager.getInstance(root.getProject());

        root.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);

                if (element instanceof XmlAttributeValue attributeValue) {
                    // HTML/Vue attribute values (e.g. v-t="'key'", translate="key")
                    String key = attributeValue.getValue();
                    if (key.isBlank()) return;

                    consumer.accept(key, attributeValue.getNode(), attributeValue.getTextRange());

                } else if (element instanceof XmlText && element instanceof PsiLanguageInjectionHost) {
                    // Vue template expressions: {{ $t('key') }}
                    // XmlText elements in Vue templates are injection hosts for JS/TS.
                    // We enumerate injections here (in the host-document pass, DocumentImpl)
                    // so that fold descriptors carry absolute offsets and persist across reopens.
                    ilm.enumerate(element, (injectedPsi, places) -> {
                        injectedPsi.accept(new JSRecursiveWalkingElementVisitor() {
                            @Override
                            public void visitJSLiteralExpression(@NotNull JSLiteralExpression literal) {
                                super.visitJSLiteralExpression(literal);

                                if (!literal.isStringLiteral()) return;

                                String key = literal.getStringValue();
                                if (key == null || key.isBlank()) return;

                                // Convert injected-document coords to absolute host-document coords.
                                // This is the critical step: fold state is keyed by absolute offset,
                                // so using host coords ensures state survives file reopens.
                                TextRange hostRange = ilm.injectedToHost(literal, literal.getTextRange());

                                consumer.accept(key, element.getNode(), hostRange);
                            }
                        });
                    });
                }
            }
        });
    }
}
