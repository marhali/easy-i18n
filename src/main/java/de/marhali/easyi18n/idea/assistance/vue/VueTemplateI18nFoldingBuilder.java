package de.marhali.easyi18n.idea.assistance.vue;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSRecursiveWalkingElementVisitor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import de.marhali.easyi18n.idea.assistance.AbstractI18nFoldingBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Folding builder for Vue SFC files.
 *
 * <p>Vue template expressions ({{ $t('key') }}) are injected as a separate language fragment
 * whose {@link com.intellij.openapi.vfs.VirtualFile} is a synthetic {@code LightVirtualFile}
 * (path ends in {@code .int}) and is not in the local file system. The base
 * {@link de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nFoldingBuilder} skips such
 * fragments to avoid coordinate-space mismatches between the injected document and the host
 * document.
 *
 * <p>This subclass handles the template expressions explicitly: it walks the host Vue PSI tree,
 * enumerates injected JS fragments via {@link InjectedLanguageManager}, converts each literal's
 * range from injected-document space to host-document space, and locates the smallest host PSI
 * node that covers the converted range — satisfying {@link com.intellij.lang.folding.FoldingDescriptor}'s
 * requirement that the node's text range contains the folding range.
 *
 * @author marhali
 */
public class VueTemplateI18nFoldingBuilder extends AbstractI18nFoldingBuilder {

    @Override
    protected void collectLiterals(@NotNull PsiElement root, @NotNull LiteralConsumer consumer) {
        InjectedLanguageManager injectedLangManager = InjectedLanguageManager.getInstance(root.getProject());
        PsiFile hostFile = root.getContainingFile();

        root.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);
                injectedLangManager.enumerate(element, (injectedPsi, places) ->
                    injectedPsi.accept(new JSRecursiveWalkingElementVisitor() {
                        @Override
                        public void visitJSLiteralExpression(@NotNull JSLiteralExpression literal) {
                            super.visitJSLiteralExpression(literal);
                            if (!literal.isStringLiteral()) return;
                            String key = literal.getStringValue();
                            if (key == null || key.isBlank()) return;
                            // Convert from injected-document space to host-document space
                            TextRange hostRange = injectedLangManager.injectedToHost(literal, literal.getTextRange());
                            if (hostRange.isEmpty()) return;
                            // Find the smallest host PSI node containing hostRange (FoldingDescriptor invariant)
                            PsiElement hostEl = hostFile.findElementAt(hostRange.getStartOffset());
                            while (hostEl != null && !hostEl.getTextRange().contains(hostRange)) {
                                hostEl = hostEl.getParent();
                            }
                            if (hostEl == null) return;
                            consumer.accept(key, hostEl.getNode(), hostRange);
                        }
                    })
                );
            }
        });
    }
}
