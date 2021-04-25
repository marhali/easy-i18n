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
import org.jetbrains.annotations.NotNull;

public class I18nCompletionProvider extends CompletionProvider<CompletionParameters> {

    Project project;

    public I18nCompletionProvider() {
        DataManager.getInstance().getDataContextFromFocusAsync().onSuccess(data -> {
           project = PlatformDataKeys.PROJECT.getData(data);
        });
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        String query = result.getPrefixMatcher().getPrefix();

        LocalizedNode node = query.contains(".") ?
                DataStore.getInstance(project).getTranslations().getNode(query)
                : DataStore.getInstance(project).getTranslations().getNodes();

        if(node == null) {
            return;
        }

        for(LocalizedNode children : node.getChildren()) {
            result.addElement(LookupElementBuilder.create(children.getKey()).appendTailText("I18n", true));
        }

        System.out.println(result.getPrefixMatcher().getPrefix()); // Debug
    }
}
