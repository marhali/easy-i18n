package de.marhali.easyi18n.assistance.intention;

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.assistance.OptionalAssistance;
import de.marhali.easyi18n.dialog.AddDialog;
import de.marhali.easyi18n.dialog.EditDialog;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ResourceBundle;

/**
 * Intention for translation related use-cases.
 * Can be used to extract (create) translations or to edit existing ones.
 * @author marhali
 */
abstract class AbstractTranslationIntention extends BaseElementAtCaretIntentionAction implements OptionalAssistance {

    protected static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    private boolean existingTranslation = false;

    @Override
    public @IntentionName @NotNull String getText() {
        return existingTranslation
                ? bundle.getString("action.edit")
                : bundle.getString("action.extract");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "EasyI18n";
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    /**
     * This is the only method a language-specific translation intention needs to implement.
     * The implementation needs to verify element type and extract the relevant key literal or value.
     * @param element Element at caret
     * @return extract translation key (not verified!) or null if intention is not applicable for this element
     */
    protected abstract @Nullable String extractText(@NotNull PsiElement element);

    @NotNull TextRange convertRange(@NotNull TextRange input) {
        return new TextRange(input.getStartOffset(), input.getEndOffset());
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if(!isAssistance(project)) {
           return false;
        }

        String text = extractText(element);

        if(text != null) {
            KeyPathConverter converter = new KeyPathConverter(project);
            existingTranslation = InstanceManager.get(project).store().getData()
                    .getTranslation(converter.fromString(text)) != null;
        }

        return text != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element)
            throws IncorrectOperationException {

        ProjectSettings settings = ProjectSettingsService.get(project).getState();
        KeyPathConverter converter = new KeyPathConverter(settings);

        String text = extractText(element);

        if(text == null) {
            throw new IncorrectOperationException("Cannot extract translation intention at caret");
        }

        TranslationData data = InstanceManager.get(project).store().getData();
        KeyPath path = converter.fromString(text);
        TranslationValue existingTranslation = data.getTranslation(path);

        // Existing translation - edit dialog
        if(existingTranslation != null) {
            new EditDialog(project, new Translation(path, existingTranslation)).showAndHandle();
            return;
        }

        // Extract translation by key
        // We assume that a text is a translation-key if it contains section delimiters and does not end with them
        if(text.contains(settings.getSectionDelimiter()) && !text.endsWith(settings.getSectionDelimiter())) {
            new AddDialog(project, path, null).showAndHandle();
            return;
        }

        // Extract translation by preview locale value
        AddDialog dialog = new AddDialog(project, new KeyPath(), text);

        dialog.registerCallback(translationUpdate -> { // Replace text at caret with chosen translation key
            if(editor != null) {
                Document doc = editor.getDocument();
                Caret caret = editor.getCaretModel().getPrimaryCaret();
                TextRange range = convertRange(element.getTextRange());

                WriteCommandAction.runWriteCommandAction(project, () ->
                        doc.replaceString(range.getStartOffset(), range.getEndOffset(),
                                converter.toString(translationUpdate.getChange().getKey())));

                caret.removeSelection();
            }
        });

        dialog.showAndHandle();
    }
}
