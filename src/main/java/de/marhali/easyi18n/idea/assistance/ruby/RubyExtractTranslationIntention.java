package de.marhali.easyi18n.idea.assistance.ruby;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyElementFactoryCore;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;

import java.util.Optional;

/**
 * @author marhali
 */
public class RubyExtractTranslationIntention extends AbstractExtractTranslationIntention {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        PsiFile containingFile = psiElement.getContainingFile();
        if (containingFile == null) {
            return false;
        }

        RStringLiteral literal = findParentOfType(psiElement, RStringLiteral.class);
        if (literal == null || RubyEditorElementExtractor.isInterpolatedString(literal)) {
            return false;
        }

        String text = literal.getContentValue();
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

        RubyEditorElementExtractor extractor = new RubyEditorElementExtractor();
        EditorElement editorElement = extractor.extract(literal, literal.getContainingFile());

        if (editorElement == null) {
            return true;
        }

        Boolean matched = projectService.query(new MatchEditorElementQuery(moduleId, editorElement));
        return !matched;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        if (editor == null) {
            return;
        }

        RStringLiteral literal = findParentOfType(psiElement, RStringLiteral.class);
        if (literal == null || RubyEditorElementExtractor.isInterpolatedString(literal)) {
            return;
        }

        String text = literal.getContentValue();
        if (text == null || text.isBlank()) {
            return;
        }

        ModuleId moduleId = literal.getUserData(PluginKey.MODULE_ID);
        if (moduleId == null) {
            throw new IllegalStateException("ModuleId is not defined on literal for extraction");
        }

        I18nProjectService projectService = project.getService(I18nProjectService.class);
        NullableI18nEntry guessedEntry = projectService.query(new GuessNullableI18nEntryQuery(moduleId, text));

        TranslationDialog dialog = TranslationDialogFactory.createAddDialog(project, moduleId, guessedEntry);
        dialog.registerCallback((entry) -> {
            I18nKey key = entry.key();
            String i18nFlavor = projectService.query(new FilledI18nFlavorQuery(moduleId, key));

            WriteCommandAction
                .writeCommandAction(project)
                .withName("Extract Translation")
                .run(() -> {
                    RExpression replacement = RubyElementFactoryCore.createExpressionFromText(literal, i18nFlavor);
                    literal.replace(replacement);
                });
        });

        dialog.show();
    }
}
