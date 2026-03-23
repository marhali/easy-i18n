package de.marhali.easyi18n.idea.assistance;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.psi.PsiElement;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all language-specific "Extract Translation" intention actions.
 *
 * <p>Provides the shared {@link #getFamilyName()}, {@link #getText()},
 * {@link #startInWriteAction()}, and {@link #findParentOfType} implementations that are
 * identical across all language-specific subclasses.
 *
 * @author marhali
 */
public abstract class AbstractExtractTranslationIntention extends PsiElementBaseIntentionAction {

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return PluginBundle.message("editor.intention.extract.title");
    }

    @Override
    public @NotNull @IntentionName String getText() {
        return PluginBundle.message("editor.intention.extract.title");
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    /**
     * Walks up the PSI tree from {@code element} to find the nearest ancestor (or self)
     * of the given {@code type}, returning {@code null} if none is found.
     */
    @SuppressWarnings("unchecked")
    protected <T extends PsiElement> @org.jetbrains.annotations.Nullable T findParentOfType(
        @NotNull PsiElement element, @NotNull Class<T> type
    ) {
        PsiElement current = element;
        while (current != null) {
            if (type.isInstance(current)) {
                return (T) current;
            }
            current = current.getParent();
        }
        return null;
    }
}
