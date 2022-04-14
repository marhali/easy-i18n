package de.marhali.easyi18n.assistance.intention;

import com.intellij.codeInsight.intention.FileModifier;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiEditorUtil;

import com.siyeh.ipp.base.MutablyNamedIntention;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.assistance.OptionalAssistance;
import de.marhali.easyi18n.dialog.AddDialog;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ResourceBundle;

/**
 * Intention for extracting a translation. Either by translation key or preferred locale value
 * @author marhali
 */
abstract class AbstractExtractIntention extends MutablyNamedIntention implements OptionalAssistance {

    protected static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    @Override
    public @NotNull String getFamilyName() {
        return "EasyI18n";
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    protected @NotNull String getTextForElement(@NotNull Project project, @Nullable String key) {
        KeyPathConverter converter = new KeyPathConverter(project);
        TranslationData data = InstanceManager.get(project).store().getData();

        return key != null && data.getTranslation(converter.fromString(key)) != null
                ? bundle.getString("action.edit")
                : bundle.getString("action.extract");
    }

    protected void extractTranslation(@NotNull Project project, @NotNull String text, PsiElement psi) {
        ProjectSettings settings = ProjectSettingsService.get(project).getState();
        KeyPathConverter converter = new KeyPathConverter(settings);

        // Extract translation key
        // We assume that a text is a translation-key if it contains section delimiters and does not end with them
        if(text.contains(settings.getSectionDelimiter()) && !text.endsWith(settings.getSectionDelimiter())) {
            new AddDialog(project, converter.fromString(text), null).showAndHandle();

        } else { // Extract translation value (here preview locale value)
            AddDialog dialog = new AddDialog(project, new KeyPath(), text);

            // Replace editor caret with chosen translation key
            dialog.registerCallback(translationUpdate -> {
                Editor editor = PsiEditorUtil.findEditor(psi);

                if(editor != null) {
                    Document doc = editor.getDocument();
                    Caret caret = editor.getCaretModel().getPrimaryCaret();
                    int start = psi.getTextOffset() + 1;
                    int end = start + text.length();

                    WriteCommandAction.runWriteCommandAction(project, () ->
                            doc.replaceString(start, end, converter.toString(translationUpdate.getChange().getKey())));

                    caret.removeSelection();
                }
            });

            dialog.showAndHandle();;
        }
    }

    @Override
    public @Nullable FileModifier getFileModifierForPreview(@NotNull PsiFile target) {
        return this;
    }
}
