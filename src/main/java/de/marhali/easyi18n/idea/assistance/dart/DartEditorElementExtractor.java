package de.marhali.easyi18n.idea.assistance.dart;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.lang.dart.psi.*;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Responsible for extracting {@link EditorElement} out from {@link DartStringLiteralExpression}.
 *
 * @author marhali
 */
public final class DartEditorElementExtractor implements EditorElementExtractor<DartStringLiteralExpression, PsiFile> {

    @Override
    public @Nullable EditorElement extract(@NotNull DartStringLiteralExpression literal, @Nullable PsiFile psiFile) {
        String stringValue = getStringContent(literal);
        if (stringValue == null || stringValue.isBlank()) {
            return null;
        }

        PsiElement parent = literal.getParent();
        if (parent == null) {
            return null;
        }

        String rawText = literal.getText();
        LiteralKind literalKind = (rawText.startsWith("\"\"\"") || rawText.startsWith("'''"))
            ? LiteralKind.TEXT_BLOCK
            : LiteralKind.STRING;

        TriggerKind triggerKind = detectTriggerKind(literal, parent);

        EditorElement.Builder builder = EditorElement.builder(
            EditorLanguage.DART,
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

    private @NotNull TriggerKind detectTriggerKind(@NotNull DartStringLiteralExpression literal, @NotNull PsiElement parent) {
        if (parent instanceof DartArgumentList) {
            return TriggerKind.CALL_ARGUMENT;
        }
        // Named argument: translate(key: "value")
        if (parent instanceof DartNamedArgument) {
            return TriggerKind.CALL_ARGUMENT;
        }
        if (parent instanceof DartVarDeclarationList) {
            return TriggerKind.DECLARATION_TARGET;
        }
        if (parent instanceof DartReturnStatement) {
            return TriggerKind.RETURN_VALUE;
        }
        if (parent instanceof DartMapEntry mapEntry) {
            List<DartExpression> exprs = mapEntry.getExpressionList();
            if (exprs.size() >= 2 && isDescendant(literal, exprs.get(1))) {
                return TriggerKind.PROPERTY_VALUE;
            }
        }
        return TriggerKind.UNKNOWN;
    }

    private void fillCallArgumentFacts(@NotNull DartStringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        DartArgumentList argumentList = findParentOfType(literal, DartArgumentList.class);
        if (argumentList == null) {
            return;
        }

        List<DartExpression> args = argumentList.getExpressionList();
        for (int i = 0; i < args.size(); i++) {
            if (isDescendant(literal, args.get(i))) {
                builder.argumentIndex(i);
                break;
            }
        }

        // DartArguments wraps DartArgumentList; its parent is DartCallExpression
        PsiElement dartArguments = argumentList.getParent();
        if (dartArguments == null) {
            return;
        }
        PsiElement callExpr = dartArguments.getParent();
        if (!(callExpr instanceof DartCallExpression dartCallExpr)) {
            return;
        }

        DartExpression fnExpr = dartCallExpr.getExpression();
        if (fnExpr != null) {
            String fnText = fnExpr.getText();
            if (fnText.contains(".")) {
                int lastDot = fnText.lastIndexOf('.');
                builder.callableName(fnText.substring(lastDot + 1));
                builder.receiverTypeFqn(fnText.substring(0, lastDot));
            } else {
                builder.callableName(fnText);
            }
        }
    }

    private void fillDeclarationFacts(@NotNull DartStringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        DartVarDeclarationList varDecl = findParentOfType(literal, DartVarDeclarationList.class);
        if (varDecl != null) {
            String name = varDecl.getVarAccessDeclaration().getName();
            if (name != null) {
                builder.declarationName(name);
            }
        }
    }

    private void fillReturnFacts(@NotNull DartStringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        DartComponent fn = findParentOfType(literal, DartFunctionDeclarationWithBodyOrNative.class);
        if (fn == null) {
            fn = findParentOfType(literal, DartMethodDeclaration.class);
        }
        if (fn != null) {
            builder.callableName(fn.getName());
        }
    }

    private void fillPropertyFacts(@NotNull DartStringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        DartMapEntry mapEntry = findParentOfType(literal, DartMapEntry.class);
        if (mapEntry != null) {
            List<DartExpression> exprs = mapEntry.getExpressionList();
            if (!exprs.isEmpty()) {
                String keyText = exprs.get(0).getText();
                // Strip quotes if the key is a string literal
                if ((keyText.startsWith("\"") && keyText.endsWith("\""))
                    || (keyText.startsWith("'") && keyText.endsWith("'"))) {
                    keyText = keyText.substring(1, keyText.length() - 1);
                }
                builder.propertyName(keyText);
                builder.propertyPath(keyText);
            }
        }
    }

    private @NotNull Set<String> extractImportSources(@NotNull DartStringLiteralExpression literal) {
        PsiFile file = literal.getContainingFile();
        if (!(file instanceof DartFile dartFile)) {
            return Collections.emptySet();
        }

        Set<String> imports = new LinkedHashSet<>();
        for (DartImportStatement stmt : PsiTreeUtil.findChildrenOfType(dartFile, DartImportStatement.class)) {
            DartStringLiteralExpression uriLiteral =
                PsiTreeUtil.findChildOfType(stmt, DartStringLiteralExpression.class);
            if (uriLiteral != null) {
                String uri = getStringContent(uriLiteral);
                if (uri != null && !uri.isBlank()) {
                    imports.add(uri);
                }
            }
        }
        return imports;
    }

    private @Nullable String extractFilePath(@Nullable PsiFile file) {
        if (file == null) {
            return null;
        }
        VirtualFile virtualFile = file.getVirtualFile();
        return virtualFile != null ? virtualFile.getPath() : null;
    }

    private boolean isInTestSources(@NotNull DartStringLiteralExpression literal) {
        PsiFile file = literal.getContainingFile();
        if (file == null || file.getVirtualFile() == null) {
            return false;
        }
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(literal.getProject());
        return fileIndex.isInTestSourceContent(file.getVirtualFile());
    }

    /**
     * Extracts the content of a Dart string literal, stripping surrounding quotes.
     * Handles regular strings ("text", 'text'), triple-quoted strings ("""text""", '''text'''),
     * and raw strings (r"text", r'text').
     */
    static @Nullable String getStringContent(@NotNull DartStringLiteralExpression literal) {
        String raw = literal.getText();
        if (raw == null || raw.isEmpty()) {
            return null;
        }

        // Raw strings: r"text" or r'text'
        if (raw.startsWith("r\"") || raw.startsWith("r'")) {
            return raw.length() >= 3 ? raw.substring(2, raw.length() - 1) : null;
        }

        // Triple-quoted strings: """text""" or '''text'''
        if (raw.startsWith("\"\"\"") || raw.startsWith("'''")) {
            return raw.length() >= 6 ? raw.substring(3, raw.length() - 3) : null;
        }

        // Regular strings: "text" or 'text'
        if (raw.startsWith("\"") || raw.startsWith("'")) {
            return raw.length() >= 2 ? raw.substring(1, raw.length() - 1) : null;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T extends PsiElement> @Nullable T findParentOfType(@NotNull PsiElement element, @NotNull Class<T> type) {
        PsiElement current = element.getParent();
        while (current != null) {
            if (type.isInstance(current)) {
                return (T) current;
            }
            current = current.getParent();
        }
        return null;
    }

    private boolean isDescendant(@NotNull PsiElement child, @Nullable PsiElement parent) {
        if (parent == null) {
            return false;
        }
        PsiElement current = child;
        while (current != null) {
            if (current == parent) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}
