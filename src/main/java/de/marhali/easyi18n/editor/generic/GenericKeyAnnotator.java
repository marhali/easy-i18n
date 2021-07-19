package de.marhali.easyi18n.editor.generic;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;

import de.marhali.easyi18n.editor.KeyAnnotator;

import org.jetbrains.annotations.NotNull;

/**
 * Translation key annotator for generic languages which support {@link PsiLiteralValue}.
 * @author marhali
 */
public class GenericKeyAnnotator extends KeyAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if(!(element instanceof PsiLiteralValue)) {
            return;
        }

        PsiLiteralValue literalValue = (PsiLiteralValue) element;
        String value = literalValue.getValue() instanceof String ? (String) literalValue.getValue() : null;

        if(value == null) {
            return;
        }

        annotate(value, element.getProject(), holder);
    }
}