package de.marhali.easyi18n.assistance.documentation;

import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.assistance.OptionalAssistance;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Provides locale values as documentation for translation keys.
 * @author marhali
 */
abstract class AbstractDocumentationProvider implements DocumentationProvider, OptionalAssistance {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    /**
     * Checks if the provided key is a valid translation-key and generates the equivalent documentation for it.
     * @param project Opened project
     * @param key Designated translation key
     * @return Generated documentation or null if not responsible
     */
    protected @Nullable String generateDoc(@NotNull Project project, @Nullable String key) {
        if(key == null || !isAssistance(project)) {
            return null;
        }

        ProjectSettings settings = ProjectSettingsService.get(project).getState();
        KeyPathConverter converter = new KeyPathConverter(settings);
        KeyPath path = converter.fromString(key);

        // So we want to take care of context and pluralization here
        // we should check the last key section for plural / context delims and if so provide all leafs within the last node

        if(path.isEmpty()) {
            return null;
        }

        TranslationData data = InstanceManager.get(project).store().getData();
        String leaf = path.remove(path.size() - 1);
        TranslationNode leafNode = data.getRootNode();

        for(String section : path) {
            leafNode = leafNode.getChildren().get(section);
            if(leafNode == null) { // Cannot resolve last node before leaf
                return null;
            }
        }

        Map<String, String> results = new LinkedHashMap<>();

        // Filter results for matching leafs (contextual and pluralization support)
        for (Map.Entry<String, TranslationNode> entry : leafNode.getChildren().entrySet()) {
            if(entry.getKey().startsWith(leaf) && entry.getValue().isLeaf()) {
                results.put(entry.getKey(), entry.getValue().getValue().get(settings.getPreviewLocale()));
            }
        }

        if(results.isEmpty()) { // No results to show
            return null;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(DocumentationMarkup.DEFINITION_START);
        builder.append(bundle.getString("documentation"));
        builder.append(DocumentationMarkup.DEFINITION_END);

        if(results.size() == 1) { // Single value
            builder.append(DocumentationMarkup.CONTENT_START);
            builder.append("<strong>").append(results.values().toArray()[0]).append("</strong>");
            builder.append(DocumentationMarkup.CONTENT_END);

        } else { // Pluralization | Contextual relevant values
            builder.append(DocumentationMarkup.SECTIONS_START);

            for (Map.Entry<String, String> entry : results.entrySet()) {
                builder.append(DocumentationMarkup.SECTION_HEADER_START);
                builder.append(entry.getKey()).append(":");
                builder.append(DocumentationMarkup.SECTION_SEPARATOR);
                builder.append("<p>");
                builder.append("<strong>").append(entry.getValue()).append("</strong>");
            }

            builder.append(DocumentationMarkup.SECTIONS_END);
        }

        return  builder.toString();
    }
}
