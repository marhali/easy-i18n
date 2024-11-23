package de.marhali.easyi18n.assistance.intention;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Xml specific translation key intention.
 * @author adeptius
 */
public class XmlTranslationIntention extends AbstractTranslationIntention {
    @Override
    protected @Nullable String extractText(@NotNull PsiElement element) {
        if(!(element.getParent() instanceof XmlAttributeValue)) {
            return null;
        }

        return ((XmlAttributeValue) element.getParent()).getValue();
    }

    @Override
    @NotNull TextRange convertRange(@NotNull TextRange input) {
        return new TextRange(input.getStartOffset() + 1, input.getEndOffset() - 1);
    }
}
