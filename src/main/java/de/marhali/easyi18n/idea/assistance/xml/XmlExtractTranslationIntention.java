package de.marhali.easyi18n.idea.assistance.xml;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import de.marhali.easyi18n.core.application.query.FilledI18nFlavorQuery;
import de.marhali.easyi18n.core.application.query.GuessNullableI18nEntryQuery;
import de.marhali.easyi18n.core.application.query.MatchEditorElementQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.model.NullableI18nEntry;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import de.marhali.easyi18n.idea.assistance.AbstractExtractTranslationIntention;
import de.marhali.easyi18n.idea.assistance.EditorFilePathExtractor;
import de.marhali.easyi18n.idea.dialog.TranslationDialog;
import de.marhali.easyi18n.idea.dialog.TranslationDialogFactory;
import de.marhali.easyi18n.idea.key.PluginKey;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author marhali
 */
public class XmlExtractTranslationIntention extends AbstractExtractTranslationIntention {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        PsiFile containingFile = psiElement.getContainingFile();

        if (containingFile == null) {
            return false;
        }

        XmlAttributeValue attributeValue = findParentOfType(psiElement, XmlAttributeValue.class);
        if (attributeValue == null) {
            return false;
        }

        String text = attributeValue.getValue();
        if (text == null || text.isBlank()) {
            return false;
        }

        I18nProjectService projectService = project.getService(I18nProjectService.class);

        EditorFilePath editorFilePath = EditorFilePathExtractor.extract(containingFile);

        Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

        if (moduleIdResponse.isEmpty()) {
            return false;
        }

        ModuleId moduleId = moduleIdResponse.get();
        attributeValue.putUserData(PluginKey.MODULE_ID, moduleId);

        XmlEditorElementExtractor extractor = new XmlEditorElementExtractor();
        EditorElement editorElement = extractor.extract(attributeValue, attributeValue.getContainingFile());

        if (editorElement == null) {
            return true;
        }

        Boolean editorElementMatched = projectService.query(new MatchEditorElementQuery(moduleId, editorElement));

        return !editorElementMatched;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        if (editor == null) {
            return;
        }

        XmlAttributeValue attributeValue = findParentOfType(psiElement, XmlAttributeValue.class);
        if (attributeValue == null) {
            return;
        }

        String text = attributeValue.getValue();
        if (text == null || text.isBlank()) {
            return;
        }

        ModuleId moduleId = attributeValue.getUserData(PluginKey.MODULE_ID);

        if (moduleId == null) {
            throw new IllegalStateException("ModuleId is not defined on attribute value for extraction");
        }

        I18nProjectService projectService = project.getService(I18nProjectService.class);

        NullableI18nEntry guessedEntry = projectService.query(new GuessNullableI18nEntryQuery(moduleId, text));

        TranslationDialog dialog = TranslationDialogFactory.createAddDialog(
            project,
            moduleId,
            guessedEntry
        );
        dialog.registerCallback((entry) -> {
            I18nKey key = entry.key();

            String i18nFlavor = projectService.query(new FilledI18nFlavorQuery(moduleId, key));

            WriteCommandAction
                .writeCommandAction(project)
                .withName("Extract Translation")
                .run(() -> {
                    PsiElement parent = attributeValue.getParent();
                    if (parent instanceof XmlAttribute attribute) {
                        attribute.setValue(i18nFlavor);
                    }
                });
        });

        dialog.show();
    }
}
