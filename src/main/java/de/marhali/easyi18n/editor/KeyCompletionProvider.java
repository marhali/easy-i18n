package de.marhali.easyi18n.editor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.*;
import com.intellij.util.*;

import de.marhali.easyi18n.DataStore;
import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
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

        DataStore store = InstanceManager.get(project).store();

        String previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();
        String pathPrefix = SettingsService.getInstance(project).getState().getPathPrefix();

        if(pathPrefix.length() > 0 && !pathPrefix.endsWith(KeyPath.DELIMITER)) {
            pathPrefix += KeyPath.DELIMITER;
        }

        Set<KeyPath> fullKeys = store.getData().getFullKeys();

        for(KeyPath currentKey :  fullKeys) {
            result.addElement(createElement(
                    pathPrefix,
                    currentKey,
                    previewLocale,
                    Objects.requireNonNull(store.getData().getTranslation(currentKey))
            ));
        }
    }

    private LookupElement createElement(String prefix, KeyPath path, String locale, Translation translation) {
        return LookupElementBuilder.create(prefix + path.toSimpleString())
                .withIcon(AllIcons.Actions.PreserveCaseHover)
                .appendTailText(" I18n(" + locale + ": " + translation.get(locale) + ")", true);
    }
}