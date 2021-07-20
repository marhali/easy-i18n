package de.marhali.easyi18n.editor.kotlin;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;

import de.marhali.easyi18n.editor.KeyCompletionProvider;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;

/**
 * Kotlin specific translation key completion contributor.
 * @author marhali
 */
public class KotlinKeyCompletionContributor  extends CompletionContributor {

    public KotlinKeyCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inside(KtLiteralStringTemplateEntry.class),
                new KeyCompletionProvider());
    }
}
