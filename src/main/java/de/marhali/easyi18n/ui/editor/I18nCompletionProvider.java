package de.marhali.easyi18n.ui.editor;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
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
public class I18nCompletionProvider extends CompletionProvider<CompletionParameters> {

    private Project project;
    private String previewLocale;

    public I18nCompletionProvider() {
        DataManager.getInstance().getDataContextFromFocusAsync().onSuccess(data -> {
           project = PlatformDataKeys.PROJECT.getData(data);
           previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();
        });
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        String query = result.getPrefixMatcher().getPrefix();
        List<String> sections = TranslationsUtil.getSections(query);
        String lastSection = null;

        if(!sections.isEmpty() && !query.endsWith(".")) {
            lastSection = sections.remove(sections.size() - 1);
        }

        String path = TranslationsUtil.sectionsToFullPath(sections);

        LocalizedNode node = sections.isEmpty() ? DataStore.getInstance(project).getTranslations().getNodes()
                : DataStore.getInstance(project).getTranslations().getNode(path);

        for(LocalizedNode children : node.getChildren()) {
            if(lastSection == null || children.getKey().startsWith(lastSection)) {
                // Construct full key path / Fore nested objects add '.' to indicate deeper level
                String fullKey = (path.isEmpty() ? children.getKey() : path + "." + children.getKey()) + (children.isLeaf() ? "" : ".");

                result.addElement(LookupElementBuilder.create(fullKey).appendTailText(getTailText(children), true));
            }
        }
    }

    private String getTailText(LocalizedNode node) {
        return !node.isLeaf() ? " I18n([])"
                : " I18n(" + previewLocale + ": " + node.getValue().get(previewLocale) + ")";
    }
}
