package de.marhali.easyi18n.idea.assistance;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all language-specific i18n completion contributors.
 *
 * <p>Provides shared utility methods used identically across all language implementations.
 * Subclasses register a language-specific {@link com.intellij.codeInsight.completion.CompletionProvider}
 * in their constructor.
 *
 * @author marhali
 */
public abstract class AbstractI18nCompletionContributor extends CompletionContributor {

    /**
     * Replaces the text in the completion range with the selected lookup item's text,
     * moving the caret to the end of the inserted text.
     */
    protected static void replaceCompletionRange(
        @NotNull InsertionContext context,
        @NotNull LookupElement item
    ) {
        Document document = context.getDocument();

        int startOffset = context.getStartOffset();
        int endOffset = context.getTailOffset();

        if (startOffset < 0 || endOffset < startOffset || endOffset > document.getTextLength()) {
            return;
        }

        String newText = item.getLookupString();
        document.replaceString(startOffset, endOffset, newText);

        int newTailOffset = startOffset + newText.length();
        context.setTailOffset(newTailOffset);
        context.getEditor().getCaretModel().moveToOffset(newTailOffset);
    }

    /**
     * Walks up the PSI tree from {@code element} to find the nearest ancestor (or self)
     * of the given {@code type}, returning {@code null} if none is found.
     */
    @SuppressWarnings("unchecked")
    protected static <T extends PsiElement> @org.jetbrains.annotations.Nullable T findParentOfType(
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
