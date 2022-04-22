package de.marhali.easyi18n.assistance.reference;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.assistance.OptionalAssistance;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.util.KeyPathConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Language specific translation key reference contributor.
 * @author marhali
 */
abstract class AbstractKeyReferenceContributor extends PsiReferenceContributor implements OptionalAssistance {
    /**
     * Searches for relevant translation-key references
     * @param project Opened project
     * @param element Targeted element
     * @param text Designated translation key
     * @return Matched translation-key reference(s)
     */
    protected @NotNull PsiReference[] getReferences(
            @NotNull Project project, @NotNull PsiElement element, @Nullable String text) {

        if(text == null || text.isEmpty() || !isAssistance(project)) {
            return PsiReference.EMPTY_ARRAY;
        }

        ProjectSettings settings = ProjectSettingsService.get(project).getState();
        KeyPathConverter converter = new KeyPathConverter(settings);

        // TODO: We should provide multiple references if not a leaf node was provided (contextual / plurals support)

        KeyPath path = converter.fromString(text);
        TranslationValue values = InstanceManager.get(project).store().getData().getTranslation(path);

        if(values == null) { // We only reference valid and existing translations
            return PsiReference.EMPTY_ARRAY;
        }

        return new PsiReference[] {
                new PsiKeyReference(converter, new Translation(path, values), element)
        };
    }
}
