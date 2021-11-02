package de.marhali.easyi18n.editor;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.FakePsiElement;

import de.marhali.easyi18n.dialog.AddDialog;
import de.marhali.easyi18n.dialog.EditDialog;
import de.marhali.easyi18n.model.KeyedTranslation;
import de.marhali.easyi18n.model.LocalizedNode;
import de.marhali.easyi18n.service.LegacyDataStore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeyReference extends PsiReferenceBase<PsiElement> {

    @Nullable private final String myKey;

    public KeyReference(@NotNull final PsiElement element) {
        this(element, (String)null);
    }

    public KeyReference(@NotNull final PsiElement element, @Nullable final String myKey) {
        super(element, true);
        this.myKey = myKey;
    }

    public KeyReference(@NotNull final PsiElement element, @NotNull TextRange textRange) {
        this(element, textRange, null);
    }

    public KeyReference(@NotNull PsiElement element, TextRange textRange, @Nullable String myKey) {
        super(element, textRange, true);
        this.myKey = myKey;
    }

    @Override
    public @Nullable PsiElement resolve() {
        return new TranslationKey();
    }

    public String getKey() {
        return myKey != null ? myKey : getValue();
    }

    class TranslationKey extends FakePsiElement implements SyntheticElement {
        @Override
        public PsiElement getParent() {
            return myElement;
        }

        @Override
        public void navigate(boolean requestFocus) {
            LocalizedNode node = LegacyDataStore.getInstance(getProject()).getTranslations().getNode(getKey());

            if(node != null) {
                new EditDialog(getProject(), new KeyedTranslation(getKey(), node.getValue())).showAndHandle();
            } else {
                new AddDialog(getProject(), getKey()).showAndHandle();
            }
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
            final TextRange rangeInElement = getRangeInElement();
            final TextRange elementRange = myElement.getTextRange();
            return elementRange != null ? rangeInElement.shiftRight(elementRange.getStartOffset()) : rangeInElement;
        }
    }

    public static boolean isReferencable(String value) {
        return value.matches("^[^\\s:/]+$");
    }
}