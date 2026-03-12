package de.marhali.easyi18n.idea.assistance;

import com.intellij.openapi.project.Project;
import com.intellij.platform.backend.documentation.DocumentationTarget;
import com.intellij.platform.backend.documentation.DocumentationTargetProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.util.PsiTreeUtil;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.EditorElementI18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import de.marhali.easyi18n.idea.assistance.java.JavaEditorElementExtractor;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.ScheduledModuleLoaderService;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * @author marhali
 */
public class I18nDocumentationTargetProvider implements DocumentationTargetProvider {
    @Override
    public @NotNull List<? extends @NotNull DocumentationTarget> documentationTargets(@NotNull PsiFile file, int offset) {

        PsiElement leaf = file.findElementAt(offset);
        if (leaf == null) {
            return List.of();
        }

        PsiLiteralExpression literal = PsiTreeUtil.getParentOfType(leaf, PsiLiteralExpression.class, false);
        if (literal == null) {
            return List.of();
        }

        Project project = file.getProject();

        I18nProjectService projectService = project.getService(I18nProjectService.class);

        EditorFilePath editorFilePath = EditorFilePathExtractor.extract(literal.getContainingFile());

        Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

        if (moduleIdResponse.isEmpty()) {
            // No associated translation module for the editor element
            return List.of();
        }

        ModuleId moduleId = moduleIdResponse.get();

        JavaEditorElementExtractor extractor = new JavaEditorElementExtractor();
        EditorElement editorElement = extractor.extract(literal, literal.getContainingFile(), false);

        if (editorElement == null) {
            return List.of();
        }

        PossiblyUnavailable<Optional<I18nEntryPreview>> entryResponse =
            projectService.query(new EditorElementI18nEntryPreviewQuery(moduleId, editorElement));

        if (!entryResponse.available()) {
            // Response is not available - module is not loaded yet
            project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId); // Schedule module load
            return List.of();
        }

        if (entryResponse.result() == null || entryResponse.result().isEmpty()) {
            return List.of();
        }

        I18nEntryPreview entryPreview = entryResponse.result().get();

        return List.of(new I18nKeyDocumentationTarget(file, moduleId, entryPreview));
    }
}
