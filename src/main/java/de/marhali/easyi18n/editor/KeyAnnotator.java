package de.marhali.easyi18n.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.model.LocalizedNode;
import de.marhali.easyi18n.service.DataStore;
import de.marhali.easyi18n.service.SettingsService;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author marhali
 */
public class KeyAnnotator {

    /**
     * Adds annotations for i18n keys with content preview for preferred locale.
     * @param key I18n key extracted by psi element
     * @param project Project instance
     * @param holder Annotation holder
     */
    protected void annotate(@NotNull String key, @NotNull Project project, @NotNull AnnotationHolder holder) {
        String previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();
        LocalizedNode node = DataStore.getInstance(project).getTranslations().getNode(key);

        if(node == null) { // Unknown translation. Just ignore it
            return;
        }

        String tooltip = node.isLeaf() ? "I18n(" + previewLocale + ": " + node.getValue().get(previewLocale) + ")"
                : "I18n ([])";

        holder.newAnnotation(HighlightSeverity.INFORMATION, tooltip).create();
    }
}