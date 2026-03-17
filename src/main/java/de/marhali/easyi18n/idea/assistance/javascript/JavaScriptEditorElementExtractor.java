package de.marhali.easyi18n.idea.assistance.javascript;

import com.intellij.lang.javascript.psi.*;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptVariable;
import com.intellij.lang.javascript.psi.ecmal4.JSAttribute;
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList;
import com.intellij.lang.javascript.psi.ecmal4.JSClass;
import com.intellij.lang.javascript.psi.ecmal4.JSImportStatement;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Responsible for extracting {@link EditorElement} out from {@link JSLiteralExpression}.
 *
 * @author marhali
 */
public final class JavaScriptEditorElementExtractor implements EditorElementExtractor<JSLiteralExpression, PsiFile> {

    private final EditorLanguage language;

    public JavaScriptEditorElementExtractor(EditorLanguage language) {
        this.language = language;
    }

    public @Nullable EditorElement extract(@NotNull JSLiteralExpression literal, @Nullable PsiFile psiFile, boolean quick) {
        if (!literal.isStringLiteral()) {
            return null;
        }

        String stringValue = literal.getStringValue();
        if (stringValue == null) {
            return null;
        }

        PsiElement parent = literal.getParent();
        if (parent == null) {
            return null;
        }

        TriggerKind triggerKind = detectTriggerKind(literal, parent);
        EditorElement.Builder builder = EditorElement.builder(
            language,
            LiteralKind.STRING,
            triggerKind,
            stringValue
        );

        builder.staticallyKnown(true);
        builder.filePath(extractFilePath(psiFile));
        builder.inTestSources(isInTestSources(literal));
        builder.importSources(extractImportSources(literal));

        switch (triggerKind) {
            case CALL_ARGUMENT -> {
                if (quick) {
                    fillCallArgumentFactsQuick(literal, builder);
                } else {
                    fillCallArgumentFacts(literal, builder);
                }
            }
            case DECLARATION_TARGET -> {
                if (quick) {
                    fillDeclarationFactsQuick(literal, builder);
                } else {
                    fillDeclarationFacts(literal, builder);
                }
            }
            case RETURN_VALUE -> {
                if (quick) {
                    fillReturnFactsQuick(literal, builder);
                } else {
                    fillReturnFacts(literal, builder);
                }
            }
            case PROPERTY_VALUE -> fillPropertyFacts(literal, builder);
            case UNKNOWN -> {
                return null;
            }
        }

        return builder.build();
    }

    private void fillCallArgumentFactsQuick(@NotNull JSLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        JSArgumentList argumentList = findParentOfType(literal, JSArgumentList.class);
        if (argumentList == null) return;

        JSCallExpression callExpression = findParentOfType(argumentList, JSCallExpression.class);
        if (callExpression == null) return;

        // Find argument index
        JSExpression[] arguments = argumentList.getArguments();
        for (int i = 0; i < arguments.length; i++) {
            if (isDescendant(literal, arguments[i])) {
                builder.argumentIndex(i);
                break;
            }
        }

        JSExpression methodExpression = callExpression.getMethodExpression();
        if (methodExpression instanceof JSReferenceExpression refExpr) {
            builder.callableName(refExpr.getReferenceName());

            JSExpression qualifier = refExpr.getQualifier();
            if (qualifier != null) {
                builder.receiverTypeFqn(qualifier.getText());
            }
        }
    }

    private void fillDeclarationFactsQuick(@NotNull JSLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        JSVariable variable = findParentOfType(literal, JSVariable.class);
        if (variable != null && isDescendant(literal, variable.getInitializer())) {
            builder.declarationName(variable.getName());
            if (variable instanceof TypeScriptVariable tsVar) {
                JSType type = tsVar.getJSType();
                builder.declarationType(type != null ? type.getTypeText() : null);
            }
        }
    }

    private void fillReturnFactsQuick(@NotNull JSLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        JSFunction function = findParentOfType(literal, JSFunction.class);
        if (function == null) return;

        builder.callableName(function.getName());
        JSType returnType = function.getReturnType();
        builder.declarationType(returnType != null ? returnType.getTypeText() : null);
    }

    private @NotNull TriggerKind detectTriggerKind(@NotNull JSLiteralExpression literal, @NotNull PsiElement parent) {
        if (parent instanceof JSArgumentList) {
            return TriggerKind.CALL_ARGUMENT;
        }
        if (parent instanceof JSVariable || parent instanceof JSAssignmentExpression) {
            return TriggerKind.DECLARATION_TARGET;
        }
        if (parent instanceof JSReturnStatement) {
            return TriggerKind.RETURN_VALUE;
        }
        if (parent instanceof JSProperty) {
            return TriggerKind.PROPERTY_VALUE;
        }
        if (parent instanceof JSArrayLiteralExpression) {
            // Check context - could be in a call or property
            PsiElement grandParent = parent.getParent();
            if (grandParent instanceof JSArgumentList) {
                return TriggerKind.CALL_ARGUMENT;
            }
            if (grandParent instanceof JSProperty) {
                return TriggerKind.PROPERTY_VALUE;
            }
        }
        // Check for arrow function single expression return
        JSFunction function = findParentOfType(literal, JSFunction.class);
        if (function != null && function.getBlock() == null) {
            // Single expression arrow function
            return TriggerKind.RETURN_VALUE;
        }
        return TriggerKind.UNKNOWN;
    }

    private void fillCallArgumentFacts(@NotNull JSLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        JSArgumentList argumentList = findParentOfType(literal, JSArgumentList.class);
        if (argumentList == null) {
            return;
        }

        JSCallExpression callExpression = findParentOfType(argumentList, JSCallExpression.class);
        if (callExpression == null) {
            return;
        }

        // Find argument index
        JSExpression[] arguments = argumentList.getArguments();
        int argumentIndex = -1;
        for (int i = 0; i < arguments.length; i++) {
            if (isDescendant(literal, arguments[i])) {
                argumentIndex = i;
                break;
            }
        }
        if (argumentIndex >= 0) {
            builder.argumentIndex(argumentIndex);
        }

        JSExpression methodExpression = callExpression.getMethodExpression();
        if (methodExpression instanceof JSReferenceExpression refExpr) {
            String callableName = refExpr.getReferenceName();
            builder.callableName(callableName);

            // Try to resolve for FQN
            PsiElement resolved = refExpr.resolve();
            if (resolved instanceof JSFunction resolvedFunction) {
                JSClass containingClass = findParentOfType(resolvedFunction, JSClass.class);
                if (containingClass != null) {
                    builder.callableFqn(containingClass.getQualifiedName() + "." + resolvedFunction.getName());
                }

                // Get parameter name if possible
                JSParameterList parameterList = resolvedFunction.getParameterList();
                if (parameterList != null && argumentIndex >= 0) {
                    JSParameter[] parameters = parameterList.getParameterVariables();
                    if (argumentIndex < parameters.length) {
                        builder.argumentName(parameters[argumentIndex].getName());
                    }
                }
            }

            JSExpression qualifier = refExpr.getQualifier();
            if (qualifier != null) {
                builder.receiverTypeFqn(qualifier.getText());
            }
        }
    }

    private void fillDeclarationFacts(@NotNull JSLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        JSVariable variable = findParentOfType(literal, JSVariable.class);
        if (variable != null && isDescendant(literal, variable.getInitializer())) {
            builder.declarationName(variable.getName());
            if (variable instanceof TypeScriptVariable tsVar) {
                JSType type = tsVar.getJSType();
                builder.declarationType(type != null ? type.getTypeText() : null);
            }
            builder.declarationMarkers(extractAttributeNames(variable));
            return;
        }

        JSAssignmentExpression assignment = findParentOfType(literal, JSAssignmentExpression.class);
        if (assignment == null || !isDescendant(literal, assignment.getROperand())) {
            return;
        }

        JSExpression left = assignment.getLOperand();
        if (left instanceof JSReferenceExpression refExpr) {
            builder.declarationName(refExpr.getReferenceName());
        }
    }

    private void fillReturnFacts(@NotNull JSLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        JSFunction function = findParentOfType(literal, JSFunction.class);
        if (function == null) {
            return;
        }

        builder.callableName(function.getName());

        JSClass containingClass = findParentOfType(function, JSClass.class);
        if (containingClass != null && containingClass.getQualifiedName() != null) {
            builder.callableFqn(containingClass.getQualifiedName() + "." + function.getName());
        }

        JSType returnType = function.getReturnType();
        builder.declarationType(returnType != null ? returnType.getTypeText() : null);
    }

    private void fillPropertyFacts(@NotNull JSLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        JSProperty property = findParentOfType(literal, JSProperty.class);
        if (property != null && isDescendant(literal, property.getValue())) {
            String name = property.getName();
            builder.propertyName(name);
            builder.propertyPath(name);
        }
    }

    private @NotNull Set<String> extractAttributeNames(@Nullable JSVariable variable) {
        if (variable == null) {
            return Collections.emptySet();
        }
        Set<String> names = new LinkedHashSet<>();
        JSAttributeList attributeList = variable.getAttributeList();
        if (attributeList != null) {
            for (JSAttribute attribute : attributeList.getAttributes()) {
                String name = attribute.getName();
                if (name != null && !name.isBlank()) {
                    names.add(name);
                }
            }
        }
        return names;
    }

    private @NotNull Set<String> extractImportSources(@NotNull JSLiteralExpression literal) {
        PsiFile file = literal.getContainingFile();
        if (!(file instanceof JSFile jsFile)) {
            return Collections.emptySet();
        }

        Set<String> imports = new LinkedHashSet<>();
        for (PsiElement child : jsFile.getChildren()) {
            if (child instanceof JSImportStatement importStmt) {
                String importPath = importStmt.getImportText();
                if (importPath != null && !importPath.isBlank()) {
                    imports.add(importPath);
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

    private boolean isInTestSources(@NotNull JSLiteralExpression literal) {
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
