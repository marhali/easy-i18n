package de.marhali.easyi18n.assistance.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

/**
 * Php specific completion contributor.
 * @author marhali
 */
public class PhpCompletionContributor extends CompletionContributor {
    public PhpCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inside(StringLiteralExpression.class),
                new KeyCompletionProvider());
    }
}
