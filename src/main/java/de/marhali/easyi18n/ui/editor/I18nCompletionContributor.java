package de.marhali.easyi18n.ui.editor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.*;
import com.intellij.psi.PsiLiteralValue;

public class I18nCompletionContributor extends CompletionContributor {

    public I18nCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inside(PsiLiteralValue.class),
                new I18nCompletionProvider());
    }
}