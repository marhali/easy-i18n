package de.marhali.easyi18n.idea.assistance.python;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.*;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Responsible for extracting {@link EditorElement} out from {@link PyStringLiteralExpression}.
 *
 * @author marhali
 */
public final class PythonEditorElementExtractor implements EditorElementExtractor<PyStringLiteralExpression, PsiFile> {

    @Override
    public @Nullable EditorElement extract(@NotNull PyStringLiteralExpression literal, @Nullable PsiFile psiFile) {
        // Skip f-strings — they are interpolated templates, not i18n keys
        if (isFString(literal)) {
            return null;
        }

        String stringValue = literal.getStringValue();
        if (stringValue == null || stringValue.isBlank()) {
            return null;
        }

        PsiElement parent = literal.getParent();
        if (parent == null) {
            return null;
        }

        String rawText = literal.getText();
        LiteralKind literalKind = (rawText.contains("\"\"\"") || rawText.contains("'''"))
            ? LiteralKind.TEXT_BLOCK
            : LiteralKind.STRING;

        TriggerKind triggerKind = detectTriggerKind(literal, parent);

        EditorElement.Builder builder = EditorElement.builder(
            EditorLanguage.PYTHON,
            literalKind,
            triggerKind,
            stringValue
        );

        builder.staticallyKnown(true);
        builder.filePath(extractFilePath(psiFile));
        builder.inTestSources(isInTestSources(literal));
        builder.importSources(extractImportSources(literal));

        switch (triggerKind) {
            case CALL_ARGUMENT -> fillCallArgumentFacts(literal, builder);
            case DECLARATION_TARGET -> fillDeclarationFacts(literal, builder);
            case RETURN_VALUE -> fillReturnFacts(literal, builder);
            case PROPERTY_VALUE -> fillPropertyFacts(literal, builder);
            case UNKNOWN -> {
                return null;
            }
        }

        return builder.build();
    }

    private @NotNull TriggerKind detectTriggerKind(@NotNull PyStringLiteralExpression literal, @NotNull PsiElement parent) {
        if (parent instanceof PyArgumentList) {
            return TriggerKind.CALL_ARGUMENT;
        }
        if (parent instanceof PyAssignmentStatement) {
            return TriggerKind.DECLARATION_TARGET;
        }
        if (parent instanceof PyReturnStatement) {
            return TriggerKind.RETURN_VALUE;
        }
        if (parent instanceof PyKeyValueExpression keyValue) {
            // Only treat as property if it's the value, not the key
            if (keyValue.getValue() == literal) {
                return TriggerKind.PROPERTY_VALUE;
            }
        }
        return TriggerKind.UNKNOWN;
    }

    private void fillCallArgumentFacts(@NotNull PyStringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PyArgumentList argumentList = findParentOfType(literal, PyArgumentList.class);
        if (argumentList == null) {
            return;
        }

        PyExpression[] args = argumentList.getArguments();
        for (int i = 0; i < args.length; i++) {
            if (isDescendant(literal, args[i])) {
                builder.argumentIndex(i);
                break;
            }
        }

        PsiElement callParent = argumentList.getParent();
        if (!(callParent instanceof PyCallExpression callExpr)) {
            return;
        }

        PyExpression callee = callExpr.getCallee();
        if (callee != null) {
            String calleeText = callee.getText();
            if (calleeText.contains(".")) {
                int lastDot = calleeText.lastIndexOf('.');
                builder.callableName(calleeText.substring(lastDot + 1));
                builder.receiverTypeFqn(calleeText.substring(0, lastDot));
            } else {
                builder.callableName(calleeText);
            }
        }
    }

    private void fillDeclarationFacts(@NotNull PyStringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PyAssignmentStatement assignment = findParentOfType(literal, PyAssignmentStatement.class);
        if (assignment == null) {
            return;
        }

        PyExpression[] targets = assignment.getTargets();
        if (targets.length > 0) {
            builder.declarationName(targets[0].getText());
        }
    }

    private void fillReturnFacts(@NotNull PyStringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PyFunction fn = findParentOfType(literal, PyFunction.class);
        if (fn != null) {
            builder.callableName(fn.getName());
        }
    }

    private void fillPropertyFacts(@NotNull PyStringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PyKeyValueExpression keyValue = findParentOfType(literal, PyKeyValueExpression.class);
        if (keyValue == null) {
            return;
        }

        PyExpression key = keyValue.getKey();
        if (key != null) {
            String keyText = key.getText();
            // Strip quotes if the key is a string literal
            if (keyText.length() >= 2
                && ((keyText.startsWith("\"") && keyText.endsWith("\""))
                    || (keyText.startsWith("'") && keyText.endsWith("'")))) {
                keyText = keyText.substring(1, keyText.length() - 1);
            }
            builder.propertyName(keyText);
            builder.propertyPath(keyText);
        }
    }

    private @NotNull Set<String> extractImportSources(@NotNull PyStringLiteralExpression literal) {
        PsiFile file = literal.getContainingFile();
        if (!(file instanceof PyFile)) {
            return Collections.emptySet();
        }

        Set<String> imports = new LinkedHashSet<>();
        for (PyImportStatementBase stmt : PsiTreeUtil.findChildrenOfType(file, PyImportStatementBase.class)) {
            String text = stmt.getText();
            if (text == null) continue;

            if (text.startsWith("from ")) {
                int importIdx = text.indexOf(" import");
                if (importIdx > 5) {
                    imports.add(text.substring(5, importIdx).trim());
                }
            } else if (text.startsWith("import ")) {
                for (String module : text.substring(7).split(",")) {
                    String m = module.trim();
                    int asIdx = m.indexOf(" as ");
                    if (asIdx >= 0) m = m.substring(0, asIdx).trim();
                    if (!m.isBlank()) imports.add(m);
                }
            }
        }
        return imports;
    }

    private @Nullable String extractFilePath(@Nullable PsiFile file) {
        if (file == null) return null;
        VirtualFile virtualFile = file.getVirtualFile();
        return virtualFile != null ? virtualFile.getPath() : null;
    }

    private boolean isInTestSources(@NotNull PyStringLiteralExpression literal) {
        PsiFile file = literal.getContainingFile();
        if (file == null || file.getVirtualFile() == null) return false;
        return ProjectFileIndex.getInstance(literal.getProject())
            .isInTestSourceContent(file.getVirtualFile());
    }

    /**
     * Returns true if the literal is a Python f-string (interpolated string template).
     * F-strings cannot be static i18n keys.
     */
    static boolean isFString(@NotNull PyStringLiteralExpression literal) {
        String text = literal.getText();
        if (text == null || text.isEmpty()) return false;
        String lower = text.toLowerCase();
        // Covers f"", f'', F"", rf"", fr"" etc.
        return lower.startsWith("f\"") || lower.startsWith("f'")
            || lower.startsWith("rf\"") || lower.startsWith("rf'")
            || lower.startsWith("fr\"") || lower.startsWith("fr'");
    }

    /**
     * Returns the string content (decoded, without surrounding quotes/prefixes),
     * or null if the literal is an f-string or has no content.
     */
    static @Nullable String getStringContent(@NotNull PyStringLiteralExpression literal) {
        if (isFString(literal)) return null;
        return literal.getStringValue();
    }

    @SuppressWarnings("unchecked")
    private <T extends PsiElement> @Nullable T findParentOfType(@NotNull PsiElement element, @NotNull Class<T> type) {
        PsiElement current = element.getParent();
        while (current != null) {
            if (type.isInstance(current)) return (T) current;
            current = current.getParent();
        }
        return null;
    }

    private boolean isDescendant(@NotNull PsiElement child, @Nullable PsiElement parent) {
        if (parent == null) return false;
        PsiElement current = child;
        while (current != null) {
            if (current == parent) return true;
            current = current.getParent();
        }
        return false;
    }
}
