package de.marhali.easyi18n.idea.assistance.javascript;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSRecursiveWalkingElementVisitor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.I18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.I18nKeyCandidate;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.EditorFilePathExtractor;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.ModuleModificationTracker;
import de.marhali.easyi18n.idea.service.ScheduledModuleLoaderService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author marhali
 */
public class JavaScriptI18nFoldingBuilder extends FoldingBuilderEx implements DumbAware {

    private final EditorLanguage language;

    public JavaScriptI18nFoldingBuilder() {
        this(EditorLanguage.JAVASCRIPT);
    }

    protected JavaScriptI18nFoldingBuilder(EditorLanguage language) {
        this.language = language;
    }

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        Project project = root.getProject();

        I18nProjectService projectService = project.getService(I18nProjectService.class);

        EditorFilePath editorFilePath = EditorFilePathExtractor.extract(root.getContainingFile());

        Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

        if (moduleIdResponse.isEmpty()) {
            // No associated translation module for the editor element
            return FoldingDescriptor.EMPTY_ARRAY;
        }

        ModuleModificationTracker moduleModificationTracker = project.getService(ModuleModificationTracker.class);
        ModuleId moduleId = moduleIdResponse.get();
        JavaScriptEditorElementExtractor extractor = new JavaScriptEditorElementExtractor(language);
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        root.accept(new JSRecursiveWalkingElementVisitor() {
            @Override
            public void visitJSLiteralExpression(@NotNull JSLiteralExpression literal) {
                super.visitJSLiteralExpression(literal);

                if (!literal.isStringLiteral()) {
                    return;
                }

                String key = literal.getStringValue();
                if (key == null || key.isBlank()) {
                    return;
                }

                PossiblyUnavailable<Optional<I18nEntryPreview>> entryResponse
                    = projectService.query(new I18nEntryPreviewQuery(moduleId, I18nKeyCandidate.of(key)));

                if (!entryResponse.available() || entryResponse.result() == null) {
                    // Response is not available - module is not loaded yet
                    project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId); // Schedule module load
                    return;
                }

                if (entryResponse.result().isEmpty()) {
                    return;
                }

                I18nEntryPreview entryPreview = entryResponse.result().get();

                if (entryPreview.previewValue() == null) {
                    return;
                }

                String placeholder = sanitizePlaceholder(entryPreview.previewValue().toInputString());

                TextRange literalRange = literal.getTextRange();

                if (literalRange.isEmpty()) {
                    return;
                }

                descriptors.add(new FoldingDescriptor(
                    literal.getNode(),
                    literalRange,
                    null,
                    placeholder,
                    Boolean.TRUE,
                    Set.of(moduleModificationTracker.get(moduleId))
                ));
            }
        });

        return descriptors.toArray(FoldingDescriptor.EMPTY_ARRAY);
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode astNode) {
        return null;
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        return true;
    }

    private static @NotNull String sanitizePlaceholder(@NotNull String value) {
        if (value.isBlank()) {
            return StringUtil.THREE_DOTS;
        }
        return value
            .replace("\n", "\\n")
            .replace("\r", "");
    }
}
