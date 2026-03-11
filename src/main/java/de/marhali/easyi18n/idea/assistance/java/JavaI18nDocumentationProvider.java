package de.marhali.easyi18n.idea.assistance.java;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.idea.assistance.I18nKeyPsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * @author marhali
 */
public final class JavaI18nDocumentationProvider extends AbstractDocumentationProvider {
    @Override
    public @Nullable @Nls String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (!(element instanceof I18nKeyPsiElement keyPsiElement)) {
            return null;
        }

        I18nEntryPreview entryPreview = keyPsiElement.getEntryPreview();
        String key = entryPreview.key().canonical();
        String value = entryPreview.previewValue() != null
            ? entryPreview.previewValue().toInputString()
            : "";

        return "<b>%s</b>=%s".formatted(key, value);
    }
}
