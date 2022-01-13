package de.marhali.easyi18n.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.KeyPathConverter;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.service.SettingsService;

import org.jetbrains.annotations.NotNull;

/**
 * Superclass for managing key annotations.
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
        // Do not annotate keys if service is disabled
        if(!SettingsService.getInstance(project).getState().isCodeAssistance()) {
            return;
        }

        SettingsState state = SettingsService.getInstance(project).getState();
        String pathPrefix = state.getPathPrefix();
        String previewLocale = state.getPreviewLocale();

        KeyPathConverter converter = new KeyPathConverter(project);

        String searchKey = key.length() >= pathPrefix.length()
                ? key.substring(pathPrefix.length())
                : key;

        if(searchKey.startsWith(KeyPath.DELIMITER)) {
            searchKey = searchKey.substring(KeyPath.DELIMITER.length());
        }

        TranslationNode node = InstanceManager.get(project).store().getData().getNode(converter.split(searchKey));

        if(node == null) { // Unknown translation. Just ignore it
            return;
        }

        String tooltip = node.isLeaf() ? "I18n(" + previewLocale + ": " + node.getValue().get(previewLocale) + ")"
                : "I18n ([])";

        holder.newAnnotation(HighlightSeverity.INFORMATION, tooltip).create();
    }
}