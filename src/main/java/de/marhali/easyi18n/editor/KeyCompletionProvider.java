package de.marhali.easyi18n.editor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.project.*;
import com.intellij.util.*;
import de.marhali.easyi18n.model.*;
import de.marhali.easyi18n.service.*;
import org.jetbrains.annotations.*;

import java.util.*;

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

        String path = result.getPrefixMatcher().getPrefix();

        if (path.endsWith(".")) {
            path = path.substring(0, path.length() - 1);
        }

        DataStore instance = DataStore.getInstance(project);
        Map<String, String> map = new HashMap<>();
        collect(map, instance.getTranslations().getNodes(), null, previewLocale, prefix);
        Map<String, String> containedPath = new HashMap<>();
        StringBuilder prefixedKey = new StringBuilder();
        while (containedPath.isEmpty()) {
            for (Map.Entry<String, String> e : map.entrySet()) {
                if (e.getKey().startsWith(path)) {
                    containedPath.put(e.getKey(), e.getValue());
                }
            }
            if (path.isEmpty()) break;
            if (containedPath.isEmpty()) {
                prefixedKey.append(path.charAt(0));
            }
            path = path.substring(1);
        }
        containedPath.forEach((key, value) -> {
            result.addElement(LookupElementBuilder.create(prefixedKey + key).appendTailText(" I18n("+previewLocale+": "+value+")", true));
        });
    }

    private void collect(Map<String, String> map, LocalizedNode node, String path, String locale, String prefix) {
        if (node.isLeaf() && !node.getKey().equals(LocalizedNode.ROOT_KEY)) {
            String value = node.getValue().get(locale);
            map.put(path, value);
            if (prefix != null && !prefix.isEmpty()) {
                map.put(prefix + "." + path, value);
            }
        } else {
            for (LocalizedNode child : node.getChildren()) {
                collect(map, child, path == null || path.isEmpty() ? child.getKey() : path + "." + child.getKey(), locale, prefix);
            }
        }
    }

}
