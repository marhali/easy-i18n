package de.marhali.easyi18n.idea.assistance.ruby;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.assoc.RAssoc;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RReturnStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RAssignmentExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Responsible for extracting {@link EditorElement} out from {@link RStringLiteral}.
 *
 * @author marhali
 */
public final class RubyEditorElementExtractor implements EditorElementExtractor<RStringLiteral, PsiFile> {

    @Override
    public @Nullable EditorElement extract(@NotNull RStringLiteral literal, @Nullable PsiFile psiFile) {
        if (isInterpolatedString(literal)) {
            return null;
        }

        String stringValue = literal.getContentValue();
        if (stringValue == null || stringValue.isBlank()) {
            return null;
        }

        PsiElement parent = literal.getParent();
        if (parent == null) {
            return null;
        }

        TriggerKind triggerKind = detectTriggerKind(literal, parent);

        EditorElement.Builder builder = EditorElement.builder(
            EditorLanguage.RUBY,
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

    private @NotNull TriggerKind detectTriggerKind(@NotNull RStringLiteral literal, @NotNull PsiElement parent) {
        if (parent instanceof RListOfExpressions listExpr) {
            if (listExpr.getParent() instanceof RReturnStatement) {
                return TriggerKind.RETURN_VALUE;
            }
            return TriggerKind.CALL_ARGUMENT;
        }
        if (parent instanceof RAssignmentExpression assignment) {
            if (isDescendant(literal, assignment.getValue())) {
                return TriggerKind.DECLARATION_TARGET;
            }
        }
        if (parent instanceof RReturnStatement) {
            return TriggerKind.RETURN_VALUE;
        }
        if (parent instanceof RAssoc assoc) {
            if (isDescendant(literal, assoc.getValue())) {
                return TriggerKind.PROPERTY_VALUE;
            }
        }
        return TriggerKind.UNKNOWN;
    }

    private void fillCallArgumentFacts(@NotNull RStringLiteral literal, @NotNull EditorElement.Builder builder) {
        RListOfExpressions argList = findParentOfType(literal, RListOfExpressions.class);
        if (argList == null) {
            return;
        }

        var args = argList.getElements();
        for (int i = 0; i < args.size(); i++) {
            if (isDescendant(literal, args.get(i))) {
                builder.argumentIndex(i);
                break;
            }
        }

        PsiElement callParent = argList.getParent();
        if (!(callParent instanceof RCall call)) {
            return;
        }

        builder.callableName(call.getCommand());
    }

    private void fillDeclarationFacts(@NotNull RStringLiteral literal, @NotNull EditorElement.Builder builder) {
        RAssignmentExpression assignment = findParentOfType(literal, RAssignmentExpression.class);
        if (assignment == null) {
            return;
        }

        var obj = assignment.getObject();
        if (obj != null) {
            builder.declarationName(obj.getText());
        }
    }

    private void fillReturnFacts(@NotNull RStringLiteral literal, @NotNull EditorElement.Builder builder) {
        RMethod method = findParentOfType(literal, RMethod.class);
        if (method != null) {
            builder.callableName(method.getName());
        }
    }

    private void fillPropertyFacts(@NotNull RStringLiteral literal, @NotNull EditorElement.Builder builder) {
        RAssoc assoc = findParentOfType(literal, RAssoc.class);
        if (assoc == null) {
            return;
        }

        String keyText = assoc.getKeyText();
        if (keyText != null && !keyText.isBlank()) {
            builder.propertyName(keyText);
            builder.propertyPath(keyText);
        }
    }

    private @NotNull Set<String> extractImportSources(@NotNull RStringLiteral literal) {
        PsiFile file = literal.getContainingFile();
        if (!(file instanceof RFile)) {
            return Collections.emptySet();
        }

        Set<String> requires = new LinkedHashSet<>();
        for (RCall call : PsiTreeUtil.findChildrenOfType(file, RCall.class)) {
            String cmd = call.getCommand();
            if (!"require".equals(cmd) && !"require_relative".equals(cmd)) {
                continue;
            }
            RListOfExpressions args = call.getCallArguments();
            if (args == null) {
                continue;
            }
            for (var arg : args.getElements()) {
                if (arg instanceof RStringLiteral argLit) {
                    String content = argLit.getContentValue();
                    if (content != null && !content.isBlank()) {
                        requires.add(content);
                    }
                }
            }
        }
        return requires;
    }

    private @Nullable String extractFilePath(@Nullable PsiFile file) {
        if (file == null) {
            return null;
        }
        VirtualFile virtualFile = file.getVirtualFile();
        return virtualFile != null ? virtualFile.getPath() : null;
    }

    private boolean isInTestSources(@NotNull RStringLiteral literal) {
        PsiFile file = literal.getContainingFile();
        if (file == null || file.getVirtualFile() == null) {
            return false;
        }
        return ProjectFileIndex.getInstance(literal.getProject())
            .isInTestSourceContent(file.getVirtualFile());
    }

    /**
     * Returns true if the string literal contains interpolated expressions (e.g. "Hello #{name}").
     * Interpolated strings cannot be static i18n keys.
     */
    static boolean isInterpolatedString(@NotNull RStringLiteral literal) {
        return literal.hasExpressionSubstitutions();
    }

    /**
     * Returns the string content without surrounding quotes, or null if interpolated or blank.
     */
    static @Nullable String getStringContent(@NotNull RStringLiteral literal) {
        if (isInterpolatedString(literal)) {
            return null;
        }
        return literal.getContentValue();
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
