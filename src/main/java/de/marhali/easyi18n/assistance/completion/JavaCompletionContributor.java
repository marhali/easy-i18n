package de.marhali.easyi18n.assistance.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiLiteralExpression;

/**
 * Java specific completion contributor.
 * @author marhali
 */
public class JavaCompletionContributor extends CompletionContributor {
    public JavaCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inside(PsiLiteralExpression.class),
                new KeyCompletionProvider());
    }
}
