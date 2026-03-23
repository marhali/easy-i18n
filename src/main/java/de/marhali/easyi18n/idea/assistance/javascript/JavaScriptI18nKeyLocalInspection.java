package de.marhali.easyi18n.idea.assistance.javascript;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiElementVisitor;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.AbstractI18nKeyLocalInspection;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class JavaScriptI18nKeyLocalInspection extends AbstractI18nKeyLocalInspection {

    private final EditorLanguage language;

    public JavaScriptI18nKeyLocalInspection() {
        this(EditorLanguage.JAVASCRIPT);
    }

    protected JavaScriptI18nKeyLocalInspection(EditorLanguage language) {
        this.language = language;
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        // TypeScript is a JavaScript dialect — IntelliJ also runs language="JavaScript" extensions
        // for TypeScript files. Detect the actual language once per file to avoid duplicate markers.
        EditorLanguage effectiveLang = effectiveLanguage(holder.getFile());
        return new JSElementVisitor() {
            @Override
            public void visitJSLiteralExpression(@NotNull JSLiteralExpression literal) {
                if (!literal.isStringLiteral()) {
                    return;
                }

                String key = literal.getStringValue();
                if (key == null || key.isBlank()) {
                    return;
                }

                JavaScriptEditorElementExtractor extractor = new JavaScriptEditorElementExtractor(effectiveLang);
                EditorElement editorElement = extractor.extract(literal, literal.getContainingFile());

                checkI18nLiteral(literal, key, editorElement, literal.getContainingFile(), holder);
            }
        };
    }

    /**
     * Returns the language to use for rule matching.
     * When this extension runs as a JavaScript dialect extension (e.g. on TypeScript files),
     * the actual file language is detected at runtime instead of using the constructor default.
     */
    protected EditorLanguage effectiveLanguage(@NotNull com.intellij.psi.PsiFile file) {
        if (language == EditorLanguage.JAVASCRIPT
                && "TypeScript".equals(file.getLanguage().getID())) {
            return EditorLanguage.TYPESCRIPT;
        }
        return language;
    }
}
