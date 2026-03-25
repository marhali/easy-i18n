package de.marhali.easyi18n.idea.assistance.dart;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.lang.dart.psi.DartStringLiteralExpression;
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
public class DartExtractTranslationIntention extends AbstractExtractTranslationIntention {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        PsiFile containingFile = psiElement.getContainingFile();

        if (containingFile == null) {
            return false;
        }

        DartStringLiteralExpression literal = findParentOfType(psiElement, DartStringLiteralExpression.class);
        if (literal == null) {
            return false;
        }

        String text = DartEditorElementExtractor.getStringContent(literal);
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
        literal.putUserData(PluginKey.MODULE_ID, moduleId);

        DartEditorElementExtractor extractor = new DartEditorElementExtractor();
        EditorElement editorElement = extractor.extract(literal, literal.getContainingFile());

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

        DartStringLiteralExpression literal = findParentOfType(psiElement, DartStringLiteralExpression.class);
        if (literal == null) {
            return;
        }

        String text = DartEditorElementExtractor.getStringContent(literal);
        if (text == null || text.isBlank()) {
            return;
        }

        ModuleId moduleId = literal.getUserData(PluginKey.MODULE_ID);

        if (moduleId == null) {
            throw new IllegalStateException("ModuleId is not defined on literal for extraction");
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
                    PsiFile containingFile = literal.getContainingFile();
                    Document document = PsiDocumentManager.getInstance(project).getDocument(containingFile);
                    if (document != null) {
                        TextRange range = literal.getTextRange();
                        document.replaceString(range.getStartOffset(), range.getEndOffset(), i18nFlavor);
                    }
                });
        });

        dialog.show();
    }
}
