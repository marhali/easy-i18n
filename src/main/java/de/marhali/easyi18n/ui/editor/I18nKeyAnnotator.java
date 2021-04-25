package de.marhali.easyi18n.ui.editor;

import com.intellij.ide.DataManager;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;
import de.marhali.easyi18n.model.LocalizedNode;
import de.marhali.easyi18n.service.DataStore;
import de.marhali.easyi18n.service.SettingsService;
import org.jetbrains.annotations.NotNull;

/**
 * Translation key annotator.
 * @author marhali
 */
public class I18nKeyAnnotator implements Annotator {

    private Project project;
    private String previewLocale;

    public I18nKeyAnnotator() {
        DataManager.getInstance().getDataContextFromFocusAsync().onSuccess(data -> {
            project = PlatformDataKeys.PROJECT.getData(data);
            previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();
        });
    }

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

        LocalizedNode node = DataStore.getInstance(project).getTranslations().getNode(value);

        if(node == null) { // Unknown translation. Just ignore it
            return;
        }

        holder.newAnnotation(HighlightSeverity.INFORMATION,
                "I18n(" + previewLocale + ": " + node.getValue().get(previewLocale) + ")").create();
    }
}