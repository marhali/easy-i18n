package de.marhali.easyi18n.idea.assistance.java;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Responsible for extracting {@link EditorElement} out from {@link PsiLiteralExpression}.
 *
 * @author marhali
 */
public final class JavaEditorElementExtractor implements EditorElementExtractor<PsiLiteralExpression, PsiFile> {

    public @Nullable EditorElement extract(@NotNull PsiLiteralExpression literal, @Nullable PsiFile psiFile, boolean quick) {
        Object rawValue = literal.getValue();

        if (!(rawValue instanceof String stringValue)) {
            return null;
        }

        PsiElement parent = literal.getParent();
        if (parent == null) {
            return null;
        }

        TriggerKind triggerKind = detectTriggerKind(literal, parent);
        EditorElement.Builder builder = EditorElement.builder(
            EditorLanguage.JAVA,
            detectLiteralKind(literal),
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

    private void fillCallArgumentFactsQuick(@NotNull PsiLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PsiExpressionList expressionList = PsiTreeUtil.getParentOfType(literal, PsiExpressionList.class, false);
        if (expressionList == null) return;

        PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(expressionList, PsiMethodCallExpression.class, false);
        if (methodCall == null) return;

        PsiExpression[] expressions = expressionList.getExpressions();
        for (int i = 0; i < expressions.length; i++) {
            if (expressions[i] == literal) {
                builder.argumentIndex(i);
                break;
            }
        }

        PsiReferenceExpression methodExpression = methodCall.getMethodExpression();
        builder.callableName(methodExpression.getReferenceName());

        PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
        if (qualifierExpression != null) {
            PsiType qualifierType = qualifierExpression.getType();
            if (qualifierType != null) {
                builder.receiverTypeFqn(qualifierType.getCanonicalText());
            }
        }
    }

    private void fillDeclarationFactsQuick(@NotNull PsiLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PsiVariable variable = PsiTreeUtil.getParentOfType(literal, PsiVariable.class, false);
        if (variable != null && variable.getInitializer() == literal) {
            builder.declarationName(variable.getName());
            PsiType type = variable.getType();
            builder.declarationType(type != null ? type.getCanonicalText() : null);
        }
    }

    private void fillReturnFactsQuick(@NotNull PsiLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PsiMethod method = PsiTreeUtil.getParentOfType(literal, PsiMethod.class, false);
        if (method == null) return;

        builder.callableName(method.getName());
        PsiType returnType = method.getReturnType();
        builder.declarationType(returnType != null ? returnType.getCanonicalText() : null);
    }

    private @NotNull TriggerKind detectTriggerKind(@NotNull PsiLiteralExpression literal, @NotNull PsiElement parent) {
        if (parent instanceof PsiExpressionList) {
            return TriggerKind.CALL_ARGUMENT;
        }
        if (parent instanceof PsiAssignmentExpression
            || parent instanceof PsiLocalVariable
            || parent instanceof PsiField
            || parent instanceof PsiNameValuePair) {
            return TriggerKind.DECLARATION_TARGET;
        }
        if (parent instanceof PsiReturnStatement) {
            return TriggerKind.RETURN_VALUE;
        }
        if (parent instanceof PsiArrayInitializerMemberValue) {
            return TriggerKind.PROPERTY_VALUE;
        }
        return TriggerKind.UNKNOWN;
    }

    private @NotNull LiteralKind detectLiteralKind(@NotNull PsiLiteralExpression literal) {
        PsiType type = literal.getType();
        if (type != null && CommonClassNames.JAVA_LANG_STRING.equals(type.getCanonicalText())) {
            return LiteralKind.STRING;
        }
        return LiteralKind.UNKNOWN;
    }

    private void fillCallArgumentFacts(@NotNull PsiLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PsiExpressionList expressionList = PsiTreeUtil.getParentOfType(literal, PsiExpressionList.class, false);
        if (expressionList == null) {
            return;
        }

        PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(expressionList, PsiMethodCallExpression.class, false);
        if (methodCall == null) {
            return;
        }

        PsiExpression[] expressions = expressionList.getExpressions();
        int argumentIndex = -1;
        for (int i = 0; i < expressions.length; i++) {
            if (expressions[i] == literal) {
                argumentIndex = i;
                break;
            }
        }
        if (argumentIndex >= 0) {
            builder.argumentIndex(argumentIndex);
        }

        PsiReferenceExpression methodExpression = methodCall.getMethodExpression();
        String callableName = methodExpression.getReferenceName();
        builder.callableName(callableName);

        PsiMethod resolvedMethod = methodCall.resolveMethod();
        if (resolvedMethod != null) {
            PsiClass containingClass = resolvedMethod.getContainingClass();
            if (containingClass != null && containingClass.getQualifiedName() != null) {
                builder.callableFqn(containingClass.getQualifiedName() + "." + resolvedMethod.getName());
            }

            PsiParameter[] parameters = resolvedMethod.getParameterList().getParameters();
            if (argumentIndex >= 0 && argumentIndex < parameters.length) {
                PsiParameter parameter = parameters[argumentIndex];
                builder.argumentName(parameter.getName());
                builder.declarationMarkers(extractAnnotationNames(parameter.getModifierList()));
            }
        }

        PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
        if (qualifierExpression != null) {
            PsiType qualifierType = qualifierExpression.getType();
            if (qualifierType != null) {
                builder.receiverTypeFqn(qualifierType.getCanonicalText());
            }
        }
    }

    private void fillDeclarationFacts(@NotNull PsiLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PsiVariable variable = PsiTreeUtil.getParentOfType(literal, PsiVariable.class, false);
        if (variable != null && variable.getInitializer() == literal) {
            builder.declarationName(variable.getName());
            PsiType type = variable.getType();
            builder.declarationType(type != null ? type.getCanonicalText() : null);
            builder.declarationMarkers(extractAnnotationNames(variable.getModifierList()));
            return;
        }

        PsiAssignmentExpression assignment = PsiTreeUtil.getParentOfType(literal, PsiAssignmentExpression.class, false);
        if (assignment == null || assignment.getRExpression() != literal) {
            return;
        }

        PsiExpression left = assignment.getLExpression();
        if (left instanceof PsiReferenceExpression referenceExpression) {
            builder.declarationName(referenceExpression.getReferenceName());
            PsiElement resolved = referenceExpression.resolve();
            if (resolved instanceof PsiVariable variableTarget) {
                PsiType type = variableTarget.getType();
                builder.declarationType(type != null ? type.getCanonicalText() : null);
                builder.declarationMarkers(extractAnnotationNames(variableTarget.getModifierList()));
            }
        }
    }

    private void fillReturnFacts(@NotNull PsiLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PsiMethod method = PsiTreeUtil.getParentOfType(literal, PsiMethod.class, false);
        if (method == null) {
            return;
        }

        builder.callableName(method.getName());

        PsiClass containingClass = method.getContainingClass();
        if (containingClass != null && containingClass.getQualifiedName() != null) {
            builder.callableFqn(containingClass.getQualifiedName() + "." + method.getName());
        }

        builder.declarationMarkers(extractAnnotationNames(method.getModifierList()));

        PsiType returnType = method.getReturnType();
        builder.declarationType(returnType != null ? returnType.getCanonicalText() : null);
    }

    private void fillPropertyFacts(@NotNull PsiLiteralExpression literal, @NotNull EditorElement.Builder builder) {
        PsiNameValuePair nameValuePair = PsiTreeUtil.getParentOfType(literal, PsiNameValuePair.class, false);
        if (nameValuePair != null && nameValuePair.getValue() == literal) {
            builder.propertyName(nameValuePair.getName());
            builder.propertyPath(nameValuePair.getName());
        }
    }

    private @NotNull Set<String> extractAnnotationNames(@Nullable PsiModifierList modifierList) {
        if (modifierList == null) {
            return Collections.emptySet();
        }
        Set<String> names = new LinkedHashSet<>();
        for (PsiAnnotation annotation : modifierList.getAnnotations()) {
            String qualifiedName = annotation.getQualifiedName();
            if (qualifiedName != null && !qualifiedName.isBlank()) {
                names.add(qualifiedName);
            }
        }
        return names;
    }

    private @NotNull Set<String> extractImportSources(@NotNull PsiLiteralExpression literal) {
        PsiJavaFile javaFile = PsiTreeUtil.getParentOfType(literal, PsiJavaFile.class);
        if (javaFile == null) {
            return Collections.emptySet();
        }

        PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return Collections.emptySet();
        }

        Set<String> imports = new LinkedHashSet<>();
        for (PsiImportStatementBase statement : importList.getAllImportStatements()) {
            String text = statement.getImportReference() != null ? statement.getImportReference().getQualifiedName() : null;
            if (text != null && !text.isBlank()) {
                imports.add(text);
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

    private boolean isInTestSources(@NotNull PsiLiteralExpression literal) {
        PsiFile file = literal.getContainingFile();
        if (file == null || file.getVirtualFile() == null) {
            return false;
        }
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(literal.getProject());
        return fileIndex.isInTestSourceContent(file.getVirtualFile());
    }
}
