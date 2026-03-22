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
 * Unified documentation target provider for all JavaScript-family languages
 * (JavaScript, TypeScript, Vue, Svelte). The {@link EditorLanguage} is derived
 * at runtime from the {@link PsiFile} so that a single registration suffices.
 *
 * @author marhali
 */
public class JavaScriptI18nDocumentationTargetProvider extends AbstractI18nDocumentationTargetProvider {

    @Override
    protected @Nullable EditorElement extractEditorElement(@NotNull PsiElement leaf, @NotNull PsiFile file) {
        JSLiteralExpression literal = PsiTreeUtil.getParentOfType(leaf, JSLiteralExpression.class, false);
        if (literal == null) {
            return null;
        }
        return new JavaScriptEditorElementExtractor(detectLanguage(file)).extract(literal, file);
    }

    private static @NotNull EditorLanguage detectLanguage(@NotNull PsiFile file) {
        return switch (file.getLanguage().getID()) {
            case "TypeScript" -> EditorLanguage.TYPESCRIPT;
            case "VUE" -> EditorLanguage.VUE;
            case "Svelte" -> EditorLanguage.SVELTE;
            default -> EditorLanguage.JAVASCRIPT;
        };
    }
}
