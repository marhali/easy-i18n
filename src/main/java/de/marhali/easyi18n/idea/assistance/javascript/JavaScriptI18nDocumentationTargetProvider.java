package de.marhali.easyi18n.idea.assistance.javascript;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.AbstractI18nDocumentationTargetProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author marhali
 */
public class JavaScriptI18nDocumentationTargetProvider extends AbstractI18nDocumentationTargetProvider {

    private final EditorLanguage language;

    public JavaScriptI18nDocumentationTargetProvider() {
        this(EditorLanguage.JAVASCRIPT);
    }

    public JavaScriptI18nDocumentationTargetProvider(EditorLanguage language) {
        this.language = language;
    }

    @Override
    protected @Nullable EditorElement extractEditorElement(@NotNull PsiElement leaf, @NotNull PsiFile file) {
        JSLiteralExpression literal = PsiTreeUtil.getParentOfType(leaf, JSLiteralExpression.class, false);
        if (literal == null) {
            return null;
        }
        return new JavaScriptEditorElementExtractor(language).extract(literal, file);
    }
}
