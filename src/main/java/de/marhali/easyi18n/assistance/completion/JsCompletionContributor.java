package de.marhali.easyi18n.assistance.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.patterns.PlatformPatterns;

/**
 * JavaScript specific completion contributor.
 * @author marhali
 */
public class JsCompletionContributor extends CompletionContributor {
    public JsCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inside(JSLiteralExpression.class),
                new KeyCompletionProvider());
    }
}