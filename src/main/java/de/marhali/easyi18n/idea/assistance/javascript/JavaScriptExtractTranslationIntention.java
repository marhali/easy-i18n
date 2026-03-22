package de.marhali.easyi18n.idea.assistance.javascript;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.impl.JSPsiElementFactory;
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
import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.EditorFilePathExtractor;
import de.marhali.easyi18n.idea.dialog.TranslationDialog;
import de.marhali.easyi18n.idea.dialog.TranslationDialogFactory;
import de.marhali.easyi18n.idea.key.PluginKey;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author marhali
 */
public class JavaScriptExtractTranslationIntention extends PsiElementBaseIntentionAction {

    private final EditorLanguage language;

    public JavaScriptExtractTranslationIntention() {
        this(EditorLanguage.JAVASCRIPT);
    }

    protected JavaScriptExtractTranslationIntention(EditorLanguage language) {
        this.language = language;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        PsiFile containingFile = psiElement.getContainingFile();

        if (containingFile == null) {
            return false;
        }

        JSLiteralExpression literal = findParentOfType(psiElement, JSLiteralExpression.class);
        if (literal == null || !literal.isStringLiteral()) {
            return false;
        }

        String text = literal.getStringValue();
        if (text == null || text.isBlank()) {
            return false;
        }

        I18nProjectService projectService = project.getService(I18nProjectService.class);

        EditorFilePath editorFilePath = EditorFilePathExtractor.extract(containingFile);

        Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

        if (moduleIdResponse.isEmpty()) {
            // No associated translation module for the editor element
            return false;
        }

        ModuleId moduleId = moduleIdResponse.get();
        literal.putUserData(PluginKey.MODULE_ID, moduleId);

        EditorLanguage effectiveLang = (language == EditorLanguage.JAVASCRIPT
                && "TypeScript".equals(containingFile.getLanguage().getID()))
            ? EditorLanguage.TYPESCRIPT : language;
        JavaScriptEditorElementExtractor extractor = new JavaScriptEditorElementExtractor(effectiveLang);
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

        JSLiteralExpression literal = findParentOfType(psiElement, JSLiteralExpression.class);
        if (literal == null || !literal.isStringLiteral()) {
            return;
        }

        String text = literal.getStringValue();
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
                    JSExpression replacement = JSPsiElementFactory.createJSExpression(
                        i18nFlavor,
                        literal,
                        JSExpression.class
                    );
                    if (replacement != null) {
                        literal.replace(replacement);
                    }
                });
        });

        dialog.show();
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return PluginBundle.message("editor.intention.extract.title");
    }

    @Override
    public @NotNull @IntentionName String getText() {
        return PluginBundle.message("editor.intention.extract.title");
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T extends PsiElement> T findParentOfType(@NotNull PsiElement element, @NotNull Class<T> type) {
        PsiElement current = element;
        while (current != null) {
            if (type.isInstance(current)) {
                return (T) current;
            }
            current = current.getParent();
        }
        return null;
    }
}
