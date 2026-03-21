package de.marhali.easyi18n.idea.assistance.rust;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.rust.lang.core.psi.*;
import org.rust.lang.core.psi.ext.*;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Responsible for extracting {@link EditorElement} out from {@link RsLitExpr} string literals.
 *
 * @author marhali
 */
public final class RustEditorElementExtractor implements EditorElementExtractor<RsLitExpr, PsiFile> {

    @Override
    public @Nullable EditorElement extract(@NotNull RsLitExpr literal, @Nullable PsiFile psiFile) {
        if (!isStringLiteral(literal)) {
            return null;
        }

        String stringValue = getStringContent(literal);
        if (stringValue == null || stringValue.isBlank()) {
            return null;
        }

        PsiElement parent = literal.getParent();
        if (parent == null) {
            return null;
        }

        String rawText = literal.getText();
        LiteralKind literalKind = (rawText.startsWith("r\"") || rawText.startsWith("r#")
            || rawText.startsWith("br\"") || rawText.startsWith("br#"))
            ? LiteralKind.TEXT_BLOCK
            : LiteralKind.STRING;

        TriggerKind triggerKind = detectTriggerKind(literal, parent);

        EditorElement.Builder builder = EditorElement.builder(
            EditorLanguage.RUST,
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

    private @NotNull TriggerKind detectTriggerKind(@NotNull RsLitExpr literal, @NotNull PsiElement parent) {
        if (parent instanceof RsValueArgumentList) {
            return TriggerKind.CALL_ARGUMENT;
        }
        if (parent instanceof RsLetDecl) {
            return TriggerKind.DECLARATION_TARGET;
        }
        if (parent instanceof RsRetExpr) {
            return TriggerKind.RETURN_VALUE;
        }
        // Struct literal field: Foo { key: "value" } — literal is always the value
        if (parent instanceof RsStructLiteralField) {
            return TriggerKind.PROPERTY_VALUE;
        }
        return TriggerKind.UNKNOWN;
    }

    private void fillCallArgumentFacts(@NotNull RsLitExpr literal, @NotNull EditorElement.Builder builder) {
        RsValueArgumentList argumentList = findParentOfType(literal, RsValueArgumentList.class);
        if (argumentList == null) {
            return;
        }

        List<RsExpr> args = argumentList.getExprList();
        for (int i = 0; i < args.size(); i++) {
            if (isDescendant(literal, args.get(i))) {
                builder.argumentIndex(i);
                break;
            }
        }

        PsiElement callParent = argumentList.getParent();
        if (callParent instanceof RsCallExpr callExpr) {
            RsExpr fnExpr = callExpr.getExpr();
            if (fnExpr != null) {
                String fnText = fnExpr.getText();
                if (fnText.contains("::")) {
                    int lastSep = fnText.lastIndexOf("::");
                    builder.callableName(fnText.substring(lastSep + 2));
                    builder.callableFqn(fnText);
                } else {
                    builder.callableName(fnText);
                }
            }
        } else if (callParent instanceof RsMethodCallExpr methodCallExpr) {
            builder.callableName(methodCallExpr.getMethodCall().getIdentifier().getText());
            RsExpr receiver = methodCallExpr.getReceiver();
            if (receiver != null) {
                builder.receiverTypeFqn(receiver.getText());
            }
        }
    }

    private void fillDeclarationFacts(@NotNull RsLitExpr literal, @NotNull EditorElement.Builder builder) {
        RsLetDecl letDecl = findParentOfType(literal, RsLetDecl.class);
        if (letDecl == null) {
            return;
        }

        RsPat pat = letDecl.getPat();
        if (pat instanceof RsPatIdent patIdent) {
            builder.declarationName(patIdent.getPatBinding().getIdentifier().getText());
        }
    }

    private void fillReturnFacts(@NotNull RsLitExpr literal, @NotNull EditorElement.Builder builder) {
        RsFunction fn = findParentOfType(literal, RsFunction.class);
        if (fn != null) {
            builder.callableName(fn.getName());
        }
    }

    private void fillPropertyFacts(@NotNull RsLitExpr literal, @NotNull EditorElement.Builder builder) {
        RsStructLiteralField field = findParentOfType(literal, RsStructLiteralField.class);
        if (field != null) {
            String fieldName = field.getIdentifier().getText();
            builder.propertyName(fieldName);
            builder.propertyPath(fieldName);
        }
    }

    private @NotNull Set<String> extractImportSources(@NotNull RsLitExpr literal) {
        PsiFile file = literal.getContainingFile();
        if (!(file instanceof RsFile)) {
            return Collections.emptySet();
        }

        Set<String> imports = new LinkedHashSet<>();
        Collection<RsUseItem> useItems = PsiTreeUtil.findChildrenOfType(file, RsUseItem.class);
        for (RsUseItem useItem : useItems) {
            String text = useItem.getText();
            if (text != null && text.startsWith("use ") && text.endsWith(";")) {
                imports.add(text.substring(4, text.length() - 1).trim());
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

    private boolean isInTestSources(@NotNull RsLitExpr literal) {
        PsiFile file = literal.getContainingFile();
        if (file == null || file.getVirtualFile() == null) {
            return false;
        }
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(literal.getProject());
        return fileIndex.isInTestSourceContent(file.getVirtualFile());
    }

    /**
     * Returns true if the given literal is a Rust string literal (regular, byte, or raw).
     */
    static boolean isStringLiteral(@NotNull RsLitExpr literal) {
        String text = literal.getText();
        if (text == null || text.isEmpty()) return false;
        return text.startsWith("\"")
            || text.startsWith("b\"")
            || text.startsWith("r\"")
            || text.startsWith("r#")
            || text.startsWith("br\"")
            || text.startsWith("br#");
    }

    /**
     * Extracts the content of a Rust string literal, stripping surrounding delimiters.
     * Works for regular ("text"), byte (b"text"), and raw (r#"text"#) strings.
     */
    static @Nullable String getStringContent(@NotNull RsLitExpr literal) {
        if (!isStringLiteral(literal)) return null;
        String raw = literal.getText();
        if (raw == null) return null;

        // Find first and last double-quote to extract content between delimiters.
        // Works for all formats: "t", b"t", r"t", r#"t"#, r##"t"##, br"t", br#"t"#
        int firstQuote = raw.indexOf('"');
        int lastQuote = raw.lastIndexOf('"');

        if (firstQuote < 0 || lastQuote <= firstQuote) return null;
        return raw.substring(firstQuote + 1, lastQuote);
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
        if (parent == null) return false;
        PsiElement current = child;
        while (current != null) {
            if (current == parent) return true;
            current = current.getParent();
        }
        return false;
    }
}
