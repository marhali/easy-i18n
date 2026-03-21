package de.marhali.easyi18n.idea.assistance.rust;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.rust.lang.core.psi.RsLitExpr;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.I18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.I18nKeyCandidate;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
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
public class RustI18nFoldingBuilder extends FoldingBuilderEx implements DumbAware {

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        Project project = root.getProject();
        I18nProjectService projectService = project.getService(I18nProjectService.class);

        EditorFilePath editorFilePath = EditorFilePathExtractor.extract(root.getContainingFile());
        Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

        if (moduleIdResponse.isEmpty()) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }

        ModuleModificationTracker moduleModificationTracker = project.getService(ModuleModificationTracker.class);
        ModuleId moduleId = moduleIdResponse.get();
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        root.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);

                if (!(element instanceof RsLitExpr literal)) {
                    return;
                }

                if (!RustEditorElementExtractor.isStringLiteral(literal)) {
                    return;
                }

                String key = RustEditorElementExtractor.getStringContent(literal);
                if (key == null || key.isBlank()) {
                    return;
                }

                PossiblyUnavailable<Optional<I18nEntryPreview>> entryResponse
                    = projectService.query(new I18nEntryPreviewQuery(moduleId, I18nKeyCandidate.of(key)));

                if (!entryResponse.available() || entryResponse.result() == null) {
                    project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId);
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

                // Value range = content between first and last quote
                String rawText = literal.getText();
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
