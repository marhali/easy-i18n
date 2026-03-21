package de.marhali.easyi18n.idea.assistance.php;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.*;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Responsible for extracting {@link EditorElement} out from {@link StringLiteralExpression}.
 *
 * @author marhali
 */
public final class PhpEditorElementExtractor implements EditorElementExtractor<StringLiteralExpression, PsiFile> {

    public @Nullable EditorElement extract(@NotNull StringLiteralExpression literal, @Nullable PsiFile psiFile) {
        String stringValue = literal.getContents();
        if (stringValue == null) {
            return null;
        }

        PsiElement parent = literal.getParent();
        if (parent == null) {
            return null;
        }

        TriggerKind triggerKind = detectTriggerKind(literal, parent);
        EditorElement.Builder builder = EditorElement.builder(
            EditorLanguage.PHP,
            LiteralKind.STRING,
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

    private @NotNull TriggerKind detectTriggerKind(@NotNull StringLiteralExpression literal, @NotNull PsiElement parent) {
        if (parent instanceof ParameterList) {
            return TriggerKind.CALL_ARGUMENT;
        }
        if (parent instanceof AssignmentExpression || parent instanceof PhpPsiElement && isVariableAssignment(parent)) {
            return TriggerKind.DECLARATION_TARGET;
        }
        if (parent instanceof PhpReturn) {
            return TriggerKind.RETURN_VALUE;
        }
        if (parent instanceof ArrayHashElement) {
            return TriggerKind.PROPERTY_VALUE;
        }
        if (parent instanceof ArrayCreationExpression) {
            // Check context - could be in a call or property
            PsiElement grandParent = parent.getParent();
            if (grandParent instanceof ParameterList) {
                return TriggerKind.CALL_ARGUMENT;
            }
            if (grandParent instanceof ArrayHashElement) {
                return TriggerKind.PROPERTY_VALUE;
            }
        }
        return TriggerKind.UNKNOWN;
    }

    private boolean isVariableAssignment(@NotNull PsiElement element) {
        if (element instanceof Variable) {
            PsiElement parent = element.getParent();
            return parent instanceof AssignmentExpression;
        }
        return false;
    }

    private void fillCallArgumentFacts(@NotNull StringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        ParameterList parameterList = findParentOfType(literal, ParameterList.class);
        if (parameterList == null) {
            return;
        }

        PsiElement callExpression = parameterList.getParent();
        if (!(callExpression instanceof FunctionReference functionRef)) {
            return;
        }

        // Find argument index
        PsiElement[] parameters = parameterList.getParameters();
        int argumentIndex = -1;
        for (int i = 0; i < parameters.length; i++) {
            if (isDescendant(literal, parameters[i])) {
                argumentIndex = i;
                break;
            }
        }
        if (argumentIndex >= 0) {
            builder.argumentIndex(argumentIndex);
        }

        String callableName = functionRef.getName();
        builder.callableName(callableName);

        // Try to resolve for FQN
        PsiElement resolved = functionRef.resolve();
        if (resolved instanceof Function resolvedFunction) {
            PhpClass containingClass = findParentOfType(resolvedFunction, PhpClass.class);
            if (containingClass != null) {
                builder.callableFqn(containingClass.getFQN() + "::" + resolvedFunction.getName());
            } else {
                // Standalone function
                String namespace = resolvedFunction.getNamespaceName();
                if (namespace != null && !namespace.isEmpty()) {
                    builder.callableFqn(namespace + "\\" + resolvedFunction.getName());
                }
            }

            // Get parameter name if possible
            Parameter[] funcParameters = resolvedFunction.getParameters();
            if (argumentIndex >= 0 && argumentIndex < funcParameters.length) {
                builder.argumentName(funcParameters[argumentIndex].getName());
            }
        }

        // Get receiver type for method calls
        if (functionRef instanceof MethodReference methodRef) {
            PhpExpression classReference = methodRef.getClassReference();
            if (classReference != null) {
                builder.receiverTypeFqn(classReference.getText());
            }
        }
    }

    private void fillDeclarationFacts(@NotNull StringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        AssignmentExpression assignment = findParentOfType(literal, AssignmentExpression.class);
        if (assignment == null) {
            return;
        }

        PhpPsiElement variable = assignment.getVariable();
        if (variable instanceof Variable var) {
            builder.declarationName(var.getName());
        }
    }

    private void fillReturnFacts(@NotNull StringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        Function function = findParentOfType(literal, Function.class);
        if (function == null) {
            return;
        }

        builder.callableName(function.getName());

        PhpClass containingClass = findParentOfType(function, PhpClass.class);
        if (containingClass != null) {
            builder.callableFqn(containingClass.getFQN() + "::" + function.getName());
        }
    }

    private void fillPropertyFacts(@NotNull StringLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        ArrayHashElement hashElement = findParentOfType(literal, ArrayHashElement.class);
        if (hashElement != null) {
            PhpPsiElement key = hashElement.getKey();
            if (key != null) {
                String keyText = key.getText();
                // Remove quotes from key if present
                if (keyText.startsWith("'") || keyText.startsWith("\"")) {
                    keyText = keyText.substring(1, keyText.length() - 1);
                }
                builder.propertyName(keyText);
                builder.propertyPath(keyText);
            }
        }
    }

    private @NotNull Set<String> extractImportSources(@NotNull StringLiteralExpression literal) {
        PsiFile file = literal.getContainingFile();
        if (!(file instanceof PhpFile phpFile)) {
            return Collections.emptySet();
        }

        Set<String> imports = new LinkedHashSet<>();
        for (PhpUse useStatement : phpFile.getTopLevelDefs().values().stream()
            .filter(e -> e instanceof PhpUse)
            .map(e -> (PhpUse) e)
            .toList()) {
            String fqn = useStatement.getFQN();
            if (fqn != null && !fqn.isBlank()) {
                imports.add(fqn);
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

    private boolean isInTestSources(@NotNull StringLiteralExpression literal) {
        PsiFile file = literal.getContainingFile();
        if (file == null || file.getVirtualFile() == null) {
            return false;
        }
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(literal.getProject());
        return fileIndex.isInTestSourceContent(file.getVirtualFile());
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
