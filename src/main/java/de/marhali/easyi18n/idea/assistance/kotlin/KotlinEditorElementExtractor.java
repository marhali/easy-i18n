package de.marhali.easyi18n.idea.assistance.kotlin;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference;
import org.jetbrains.kotlin.psi.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Responsible for extracting {@link EditorElement} out from {@link KtStringTemplateExpression}.
 *
 * @author marhali
 */
public final class KotlinEditorElementExtractor implements EditorElementExtractor<KtStringTemplateExpression, PsiFile> {

    public @Nullable EditorElement extract(@NotNull KtStringTemplateExpression literal, @Nullable PsiFile psiFile, boolean quick) {
        String stringValue = extractStringValue(literal);

        if (stringValue == null) {
            return null;
        }

        PsiElement parent = literal.getParent();
        if (parent == null) {
            return null;
        }

        TriggerKind triggerKind = detectTriggerKind(literal, parent);
        EditorElement.Builder builder = EditorElement.builder(
            EditorLanguage.KOTLIN,
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

    private @Nullable String extractStringValue(@NotNull KtStringTemplateExpression literal) {
        KtStringTemplateEntry[] entries = literal.getEntries();
        if (entries.length == 0) {
            return "";
        }
        if (entries.length == 1 && entries[0] instanceof KtLiteralStringTemplateEntry) {
            return entries[0].getText();
        }
        // For complex templates with interpolation, we can't extract a simple value
        StringBuilder sb = new StringBuilder();
        for (KtStringTemplateEntry entry : entries) {
            if (entry instanceof KtLiteralStringTemplateEntry) {
                sb.append(entry.getText());
            } else {
                // Contains interpolation - not a simple string
                return null;
            }
        }
        return sb.toString();
    }

    private void fillCallArgumentFactsQuick(@NotNull KtStringTemplateExpression literal, @NotNull EditorElement.Builder builder) {
        KtValueArgumentList argumentList = findParentOfType(literal, KtValueArgumentList.class);
        if (argumentList == null) return;

        KtCallExpression callExpression = findParentOfType(argumentList, KtCallExpression.class);
        if (callExpression == null) return;

        // Find argument index
        int index = 0;
        for (KtValueArgument argument : argumentList.getArguments()) {
            if (isDescendant(literal, argument)) {
                builder.argumentIndex(index);
                break;
            }
            index++;
        }

        KtExpression callee = callExpression.getCalleeExpression();
        if (callee != null) {
            builder.callableName(callee.getText());
        }

        // Try to get receiver type
        PsiElement parent = callExpression.getParent();
        if (parent instanceof KtDotQualifiedExpression dotExpr) {
            KtExpression receiver = dotExpr.getReceiverExpression();
            if (receiver != null) {
                // Quick mode: just use the text representation
                builder.receiverTypeFqn(receiver.getText());
            }
        }
    }

    private void fillDeclarationFactsQuick(@NotNull KtStringTemplateExpression literal, @NotNull EditorElement.Builder builder) {
        KtProperty property = findParentOfType(literal, KtProperty.class);
        if (property != null && isDescendant(literal, property.getInitializer())) {
            builder.declarationName(property.getName());
            KtTypeReference typeRef = property.getTypeReference();
            builder.declarationType(typeRef != null ? typeRef.getText() : null);
        }
    }

    private void fillReturnFactsQuick(@NotNull KtStringTemplateExpression literal, @NotNull EditorElement.Builder builder) {
        KtNamedFunction function = findParentOfType(literal, KtNamedFunction.class);
        if (function == null) return;

        builder.callableName(function.getName());
        KtTypeReference returnType = function.getTypeReference();
        builder.declarationType(returnType != null ? returnType.getText() : null);
    }

    private @NotNull TriggerKind detectTriggerKind(@NotNull KtStringTemplateExpression literal, @NotNull PsiElement parent) {
        if (parent instanceof KtValueArgument) {
            return TriggerKind.CALL_ARGUMENT;
        }
        if (parent instanceof KtProperty || parent instanceof KtBinaryExpression) {
            return TriggerKind.DECLARATION_TARGET;
        }
        if (parent instanceof KtReturnExpression) {
            return TriggerKind.RETURN_VALUE;
        }
        if (parent instanceof KtAnnotationEntry || parent instanceof KtValueArgumentList) {
            // Check if inside annotation
            if (findParentOfType(literal, KtAnnotationEntry.class) != null) {
                return TriggerKind.PROPERTY_VALUE;
            }
            return TriggerKind.CALL_ARGUMENT;
        }
        // Check if it's directly a return expression in a single-expression function
        KtNamedFunction function = findParentOfType(literal, KtNamedFunction.class);
        if (function != null && function.getBodyExpression() == literal) {
            return TriggerKind.RETURN_VALUE;
        }
        return TriggerKind.UNKNOWN;
    }

    private @NotNull LiteralKind detectLiteralKind(@NotNull KtStringTemplateExpression literal) {
        return LiteralKind.STRING;
    }

    private void fillCallArgumentFacts(@NotNull KtStringTemplateExpression literal, @NotNull EditorElement.Builder builder) {
        KtValueArgumentList argumentList = findParentOfType(literal, KtValueArgumentList.class);
        if (argumentList == null) {
            return;
        }

        KtCallExpression callExpression = findParentOfType(argumentList, KtCallExpression.class);
        if (callExpression == null) {
            return;
        }

        // Find argument index and name
        int index = 0;
        for (KtValueArgument argument : argumentList.getArguments()) {
            if (isDescendant(literal, argument)) {
                builder.argumentIndex(index);
                KtValueArgumentName argName = argument.getArgumentName();
                if (argName != null) {
                    builder.argumentName(argName.getAsName().asString());
                }
                break;
            }
            index++;
        }

        KtExpression callee = callExpression.getCalleeExpression();
        if (callee != null) {
            builder.callableName(callee.getText());

            // Try to resolve the reference for FQN
            if (callee instanceof KtNameReferenceExpression nameRef) {
                for (var ref : nameRef.getReferences()) {
                    if (ref instanceof KtSimpleNameReference simpleRef) {
                        PsiElement resolved = simpleRef.resolve();
                        if (resolved instanceof KtNamedFunction resolvedFunction) {
                            KtClassOrObject containingClass = findParentOfType(resolvedFunction, KtClassOrObject.class);
                            if (containingClass != null && containingClass.getFqName() != null) {
                                builder.callableFqn(containingClass.getFqName().asString() + "." + resolvedFunction.getName());
                            }
                        }
                    }
                }
            }
        }

        // Get receiver type
        PsiElement callParent = callExpression.getParent();
        if (callParent instanceof KtDotQualifiedExpression dotExpr) {
            KtExpression receiver = dotExpr.getReceiverExpression();
            if (receiver != null) {
                builder.receiverTypeFqn(receiver.getText());
            }
        }
    }

    private void fillDeclarationFacts(@NotNull KtStringTemplateExpression literal, @NotNull EditorElement.Builder builder) {
        KtProperty property = findParentOfType(literal, KtProperty.class);
        if (property != null && isDescendant(literal, property.getInitializer())) {
            builder.declarationName(property.getName());
            KtTypeReference typeRef = property.getTypeReference();
            builder.declarationType(typeRef != null ? typeRef.getText() : null);
            builder.declarationMarkers(extractAnnotationNames(property));
            return;
        }

        KtBinaryExpression assignment = findParentOfType(literal, KtBinaryExpression.class);
        if (assignment == null || !isDescendant(literal, assignment.getRight())) {
            return;
        }

        KtExpression left = assignment.getLeft();
        if (left instanceof KtNameReferenceExpression nameRef) {
            builder.declarationName(nameRef.getReferencedName());
        }
    }

    private void fillReturnFacts(@NotNull KtStringTemplateExpression literal, @NotNull EditorElement.Builder builder) {
        KtNamedFunction function = findParentOfType(literal, KtNamedFunction.class);
        if (function == null) {
            return;
        }

        builder.callableName(function.getName());

        KtClassOrObject containingClass = findParentOfType(function, KtClassOrObject.class);
        if (containingClass != null && containingClass.getFqName() != null) {
            builder.callableFqn(containingClass.getFqName().asString() + "." + function.getName());
        }

        builder.declarationMarkers(extractAnnotationNames(function));

        KtTypeReference returnType = function.getTypeReference();
        builder.declarationType(returnType != null ? returnType.getText() : null);
    }

    private void fillPropertyFacts(@NotNull KtStringTemplateExpression literal, @NotNull EditorElement.Builder builder) {
        KtValueArgument argument = findParentOfType(literal, KtValueArgument.class);
        if (argument != null) {
            KtValueArgumentName argName = argument.getArgumentName();
            if (argName != null) {
                String name = argName.getAsName().asString();
                builder.propertyName(name);
                builder.propertyPath(name);
            }
        }
    }

    private @NotNull Set<String> extractAnnotationNames(@Nullable KtModifierListOwner element) {
        if (element == null) {
            return Collections.emptySet();
        }
        KtModifierList modifierList = element.getModifierList();
        if (modifierList == null) {
            return Collections.emptySet();
        }
        Set<String> names = new LinkedHashSet<>();
        for (KtAnnotationEntry annotation : modifierList.getAnnotationEntries()) {
            KtTypeReference typeRef = annotation.getTypeReference();
            if (typeRef != null) {
                String text = typeRef.getText();
                if (text != null && !text.isBlank()) {
                    names.add(text);
                }
            }
        }
        return names;
    }

    private @NotNull Set<String> extractImportSources(@NotNull KtStringTemplateExpression literal) {
        KtFile ktFile = findParentOfType(literal, KtFile.class);
        if (ktFile == null) {
            return Collections.emptySet();
        }

        Set<String> imports = new LinkedHashSet<>();
        for (KtImportDirective directive : ktFile.getImportDirectives()) {
            String importPath = directive.getImportedFqName() != null
                ? directive.getImportedFqName().asString()
                : null;
            if (importPath != null && !importPath.isBlank()) {
                imports.add(importPath);
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

    private boolean isInTestSources(@NotNull KtStringTemplateExpression literal) {
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