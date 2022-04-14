package de.marhali.easyi18n.assistance.reference;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.SyntheticElement;
import com.intellij.psi.impl.FakePsiElement;

import de.marhali.easyi18n.dialog.AddDialog;
import de.marhali.easyi18n.dialog.EditDialog;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * References translation keys inside editor with corresponding {@link EditDialog} / {@link AddDialog}.
 * @author marhali
 */
public class PsiKeyReference extends PsiReferenceBase<PsiElement> {

    private final @NotNull Translation translation;
    private final @NotNull KeyPathConverter converter;

    protected PsiKeyReference(
            @NotNull KeyPathConverter converter, @NotNull Translation translation, @NotNull PsiElement element) {

        super(element, true);
        this.translation = translation;
        this.converter = converter;
    }

    public @NotNull String getKey() {
        return converter.toString(translation.getKey());
    }

    @Override
    public @Nullable PsiElement resolve() {
        return new TranslationReference();
    }

    public class TranslationReference extends FakePsiElement implements SyntheticElement {
        @Override
        public PsiElement getParent() {
            return myElement;
        }

        @Override
        public void navigate(boolean requestFocus) {
            new EditDialog(getProject(), translation).showAndHandle();
        }

        @Override
        public String getPresentableText() {
            return getKey();
        }

        @Override
        public String getName() {
            return getKey();
        }

        @Override
        public @Nullable TextRange getTextRange() {
            TextRange rangeInElement = getRangeInElement();
            TextRange elementRange = myElement.getTextRange();
            return elementRange != null ? rangeInElement.shiftRight(elementRange.getStartOffset()) : rangeInElement;
        }
    }
}