package de.marhali.easyi18n.assistance.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;

/**
 * Kotlin specific completion contributor.
 * @author marhali
 */
public class KtCompletionContributor extends CompletionContributor {
    public KtCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inside(KtLiteralStringTemplateEntry.class),
                new KeyCompletionProvider());
    }
}
