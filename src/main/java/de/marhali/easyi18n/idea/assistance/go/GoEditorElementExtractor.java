package de.marhali.easyi18n.idea.assistance.go;

import com.goide.psi.*;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Responsible for extracting {@link EditorElement} out from {@link GoStringLiteral}.
 *
 * @author marhali
 */
public final class GoEditorElementExtractor implements EditorElementExtractor<GoStringLiteral, PsiFile> {

    @Override
    public @Nullable EditorElement extract(@NotNull GoStringLiteral literal, @Nullable PsiFile psiFile) {
        String stringValue = getStringContent(literal);
        if (stringValue == null || stringValue.isBlank()) {
            return null;
        }

        PsiElement parent = literal.getParent();
        if (parent == null) {
            return null;
        }

        LiteralKind literalKind = literal.getText().startsWith("`") ? LiteralKind.TEXT_BLOCK : LiteralKind.STRING;
        TriggerKind triggerKind = detectTriggerKind(literal, parent);

        EditorElement.Builder builder = EditorElement.builder(
            EditorLanguage.GO,
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

    private @NotNull TriggerKind detectTriggerKind(@NotNull GoStringLiteral literal, @NotNull PsiElement parent) {
        if (parent instanceof GoArgumentList) {
            return TriggerKind.CALL_ARGUMENT;
        }
        if (parent instanceof GoShortVarDeclaration || parent instanceof GoVarSpec) {
            return TriggerKind.DECLARATION_TARGET;
        }
        if (parent instanceof GoReturnStatement) {
            return TriggerKind.RETURN_VALUE;
        }
        if (parent instanceof GoValue goValue && goValue.getParent() instanceof GoElement) {
            return TriggerKind.PROPERTY_VALUE;
        }
        return TriggerKind.UNKNOWN;
    }

    private void fillCallArgumentFacts(@NotNull GoStringLiteral literal, @NotNull EditorElement.Builder builder) {
        GoArgumentList argumentList = findParentOfType(literal, GoArgumentList.class);
        if (argumentList == null) {
            return;
        }

        List<GoExpression> args = argumentList.getExpressionList();
        for (int i = 0; i < args.size(); i++) {
            if (isDescendant(literal, args.get(i))) {
                builder.argumentIndex(i);
                break;
            }
        }

        PsiElement callExpr = argumentList.getParent();
        if (!(callExpr instanceof GoCallExpr goCallExpr)) {
            return;
        }

        GoExpression fnExpr = goCallExpr.getExpression();
        if (fnExpr instanceof GoReferenceExpression refExpr) {
            builder.callableName(refExpr.getIdentifier().getText());
            GoQualifier qualifier = refExpr.getQualifier();
            if (qualifier != null) {
                builder.receiverTypeFqn(qualifier.getText());
            }
        }
    }

    private void fillDeclarationFacts(@NotNull GoStringLiteral literal, @NotNull EditorElement.Builder builder) {
        GoShortVarDeclaration shortVar = findParentOfType(literal, GoShortVarDeclaration.class);
        if (shortVar != null) {
            List<GoVarDefinition> defs = shortVar.getVarDefinitionList();
            if (!defs.isEmpty()) {
                builder.declarationName(defs.get(0).getName());
            }
            return;
        }

        GoVarSpec varSpec = findParentOfType(literal, GoVarSpec.class);
        if (varSpec != null) {
            List<GoVarDefinition> defs = varSpec.getVarDefinitionList();
            if (!defs.isEmpty()) {
                builder.declarationName(defs.get(0).getName());
            }
        }
    }

    private void fillReturnFacts(@NotNull GoStringLiteral literal, @NotNull EditorElement.Builder builder) {
        GoFunctionDeclaration fn = findParentOfType(literal, GoFunctionDeclaration.class);
        if (fn != null) {
            builder.callableName(fn.getName());
        }
    }

    private void fillPropertyFacts(@NotNull GoStringLiteral literal, @NotNull EditorElement.Builder builder) {
        GoElement element = findParentOfType(literal, GoElement.class);
        if (element != null) {
            GoKey key = element.getKey();
            if (key != null) {
                String keyText = key.getText();
                builder.propertyName(keyText);
                builder.propertyPath(keyText);
            }
        }
    }

    private @NotNull Set<String> extractImportSources(@NotNull GoStringLiteral literal) {
        PsiFile file = literal.getContainingFile();
        if (!(file instanceof GoFile goFile)) {
            return Collections.emptySet();
        }

        Set<String> imports = new LinkedHashSet<>();
        GoImportList importList = goFile.getImportList();
        if (importList != null) {
            for (GoImportDeclaration decl : importList.getImportDeclarationList()) {
                for (GoImportSpec spec : decl.getImportSpecList()) {
                    String path = spec.getPath();
                    if (path != null && !path.isBlank()) {
                        imports.add(path);
                    }
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

    private boolean isInTestSources(@NotNull GoStringLiteral literal) {
        PsiFile file = literal.getContainingFile();
        if (file == null || file.getVirtualFile() == null) {
            return false;
        }
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(literal.getProject());
        return fileIndex.isInTestSourceContent(file.getVirtualFile());
    }

    static @Nullable String getStringContent(@NotNull GoStringLiteral literal) {
        String raw = literal.getText();
        if (raw == null || raw.length() < 2) {
            return null;
        }
        return raw.substring(1, raw.length() - 1);
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
