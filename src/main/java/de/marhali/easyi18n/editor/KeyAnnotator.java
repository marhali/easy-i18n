package de.marhali.easyi18n.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.KeyPathConverter;
import de.marhali.easyi18n.model.TranslationNode;

import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.settings.ProjectSettingsService;
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
        if(!ProjectSettingsService.get(project).getState().isAssistance()) {
            return;
        }

        ProjectSettings state = ProjectSettingsService.get(project).getState();
        //String pathPrefix = state.getPathPrefix();
        // TODO: Path prefix removal
        String pathPrefix = "";
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