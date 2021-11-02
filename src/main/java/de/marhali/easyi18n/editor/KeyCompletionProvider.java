package de.marhali.easyi18n.editor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.*;
import com.intellij.util.*;
import de.marhali.easyi18n.model.*;
import de.marhali.easyi18n.service.*;
import de.marhali.easyi18n.util.TranslationsUtil;
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

        LegacyDataStore store = LegacyDataStore.getInstance(project);
        String previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();
        String pathPrefix = SettingsService.getInstance(project).getState().getPathPrefix();

        String path = result.getPrefixMatcher().getPrefix();

        if(pathPrefix == null) {
            pathPrefix = "";
        }

        if(path.startsWith(pathPrefix)) {
            path = path.substring(pathPrefix.length());

            if(path.startsWith(".")) { // Remove leading dot
                path = path.substring(1);
            }

        } else {
            path = ""; // Show suggestions for root view
        }

        if(pathPrefix.length() > 0 && !pathPrefix.endsWith(".")) {
            pathPrefix += ".";
        }

        List<String> fullKeys = store.getTranslations().getFullKeys();

        int sections = path.split("\\.").length;
        int maxSectionForwardLookup = 5;

        for(String key : fullKeys) {
            // Path matches
            if(key.startsWith(path)) {
                String[] keySections = key.split("\\.");

                if(keySections.length > sections + maxSectionForwardLookup) { // Key is too deep nested
                    String shrinkKey = TranslationsUtil.sectionsToFullPath(Arrays.asList(
                            Arrays.copyOf(keySections, sections + maxSectionForwardLookup)));

                    result.addElement(LookupElementBuilder.create(pathPrefix + shrinkKey)
                        .appendTailText(" I18n([])", true));

                } else {
                    LocalizedNode node = store.getTranslations().getNode(key);
                    String translation = node != null ? node.getValue().get(previewLocale) : null;

                    result.addElement(LookupElementBuilder.create(pathPrefix + key)
                            .withIcon(AllIcons.Actions.PreserveCaseHover)
                            .appendTailText(" I18n(" + previewLocale + ": " + translation + ")", true)
                    );
                }
            }
        }
    }
}
