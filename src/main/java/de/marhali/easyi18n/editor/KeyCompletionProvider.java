package de.marhali.easyi18n.editor;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;

import de.marhali.easyi18n.model.LocalizedNode;
import de.marhali.easyi18n.service.DataStore;
import de.marhali.easyi18n.service.SettingsService;
import de.marhali.easyi18n.util.TranslationsUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * I18n translation key completion provider.
 * @author marhali
 */
public class KeyCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

        Project project = parameters.getOriginalFile().getProject();

        // Do not annotate keys if service is disabled
        if(!SettingsService.getInstance(project).getState().isCodeAssistance()) {
            return;
        }

        String previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();
        String prefix = SettingsService.getInstance(project).getState().getPrefix();

        String query = result.getPrefixMatcher().getPrefix();
        List<String> sections = TranslationsUtil.getSections(query);

        String path = TranslationsUtil.sectionsToFullPath(sections);
        if (prefix != null && path.startsWith(prefix)) {
            path = path.substring(prefix.length());
            if (path.startsWith(".")) {
                path = path.substring(1);
            }
        }

        LocalizedNode node = sections.isEmpty() ? DataStore.getInstance(project).getTranslations().getNodes()
                : DataStore.getInstance(project).getTranslations().getNode(path);

        if(node == null) { // Unknown translation
            return;
        }

        append(node, previewLocale, path, prefix, result);
//        for(LocalizedNode children : node.getChildren()) {
//            if(lastSection == null || children.getKey().startsWith(lastSection)) {
//                // Construct full key path / Fore nested objects add '.' to indicate deeper level
//                String fullKey = (path.isEmpty() ? children.getKey() : path + "." + children.getKey()) + (children.isLeaf() ? "" : ".");
//
//                if (prefix != null && !fullKey.isEmpty()) {
//                    fullKey = prefix + "." + fullKey;
//                }
//
//                result.addElement(LookupElementBuilder.create(fullKey)
//                        .appendTailText(getTailText(children, previewLocale), true));
//            }
//        }
    }

    private void append(LocalizedNode node, String locale, String path, String prefix, CompletionResultSet result) {
        if (node.isLeaf() && !node.getKey().equals(LocalizedNode.ROOT_KEY)) {
            if (prefix != null) {
                path = prefix + "." + path;
            }
            result.addElement(LookupElementBuilder.create(path)
                    .appendTailText(getTailText(node, locale), true));
        } else {
            for (LocalizedNode children : node.getChildren()) {
                append(children, locale, path == null || path.isEmpty() ? children.getKey() : path + "." + children.getKey(), prefix, result);
            }
        }
    }

    private String getTailText(LocalizedNode node, String previewLocale) {
        return !node.isLeaf() ? " I18n([])"
                : " I18n(" + previewLocale + ": " + node.getValue().get(previewLocale) + ")";
    }
}
