package de.marhali.easyi18n.assistance.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl;

/**
 * Xml specific completion contributor.
 * @author adeptius
 */
public class XmlCompletionContributor extends CompletionContributor {
    public XmlCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inside(XmlAttributeValueImpl.class),
                new KeyCompletionProvider());
    }
}
