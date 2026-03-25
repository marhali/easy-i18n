package de.marhali.easyi18n.idea.assistance;

import com.intellij.openapi.project.Project;
import com.intellij.platform.backend.documentation.DocumentationTarget;
import com.intellij.platform.backend.documentation.DocumentationTargetProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.I18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.MatchEditorElementQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.I18nKeyCandidate;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.ScheduledModuleLoaderService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base for language-specific i18n documentation target providers.
 *
 * @author marhali
 */
public abstract class AbstractI18nDocumentationTargetProvider implements DocumentationTargetProvider {

    /**
     * Extract an {@link EditorElement} from the leaf PSI element at the cursor.
     * Return {@code null} if the element is not an i18n string literal candidate.
     */
    protected abstract @Nullable EditorElement extractEditorElement(@NotNull PsiElement leaf, @NotNull PsiFile file);

    @Override
    public @NotNull List<? extends @NotNull DocumentationTarget> documentationTargets(@NotNull PsiFile file, int offset) {
        PsiElement leaf = file.findElementAt(offset);
        if (leaf == null) {
            return List.of();
        }

        EditorElement editorElement = extractEditorElement(leaf, file);
        if (editorElement == null) {
            return List.of();
        }

        String key = editorElement.literalValue();
        if (key.isBlank()) {
            return List.of();
        }

        Project project = file.getProject();
        I18nProjectService projectService = project.getService(I18nProjectService.class);
        EditorFilePath editorFilePath = EditorFilePathExtractor.extract(file);

        Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));
        if (moduleIdResponse.isEmpty()) {
            return List.of();
        }

        ModuleId moduleId = moduleIdResponse.get();

        Boolean editorElementMatched = projectService.query(new MatchEditorElementQuery(moduleId, editorElement));
        if (!editorElementMatched) {
            return List.of();
        }

        PossiblyUnavailable<Optional<I18nEntryPreview>> entryResponse
            = projectService.query(new I18nEntryPreviewQuery(moduleId, I18nKeyCandidate.of(key)));

        if (!entryResponse.available()) {
            project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId);
            return List.of();
        }

        if (entryResponse.result() == null || entryResponse.result().isEmpty()) {
            return List.of();
        }

        return List.of(new I18nKeyDocumentationTarget(file, moduleId, entryResponse.result().get()));
    }
}
