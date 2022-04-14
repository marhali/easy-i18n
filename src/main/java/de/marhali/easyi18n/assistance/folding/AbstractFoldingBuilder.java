package de.marhali.easyi18n.assistance.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.DataStore;
import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.assistance.OptionalAssistance;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Language specific folding of translation key with preferred locale value.
 * @author marhali
 */
abstract class AbstractFoldingBuilder extends FoldingBuilderEx implements OptionalAssistance {

    protected static final FoldingGroup group = FoldingGroup.newGroup("EasyI18n key folding");

    /**
     * Constructs the folding text for the provided text.
     * @param project Opened project
     * @param text Designated translation key
     * @return Preferred locale value or null if translation does not exists
     */
    protected @Nullable String getPlaceholderText(@NotNull Project project, @Nullable String text) {
        if(text == null) {
            return null;
        }

        DataStore store = InstanceManager.get(project).store();
        KeyPathConverter converter = new KeyPathConverter(project);
        TranslationValue localeValues = store.getData().getTranslation(converter.fromString(text));

        if(localeValues == null) {
            return null;
        }

        String previewLocale = ProjectSettingsService.get(project).getState().getPreviewLocale();
        return localeValues.get(previewLocale);
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }
}
