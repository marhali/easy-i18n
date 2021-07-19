package de.marhali.easyi18n.editor.kotlin;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;

/**
 * Kotlin specific translation key annotator
 * @author marhali
 */
public class KotlinKeyAnnotator extends KeyAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if(!(element instanceof KtLiteralStringTemplateEntry)) {
            return;
        }

        String value = element.getText();

        if(value == null) {
            return;
        }

        annotate(value, element.getProject(), holder);
    }
}