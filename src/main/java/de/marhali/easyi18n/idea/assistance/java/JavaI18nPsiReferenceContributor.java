package de.marhali.easyi18n.idea.assistance.java;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.EditorElementI18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.*;
import de.marhali.easyi18n.idea.assistance.EditorFilePathExtractor;
import de.marhali.easyi18n.idea.assistance.I18nKeyPsiReference;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.ScheduledModuleLoaderService;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author marhali
 */
public class JavaI18nPsiReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
            PlatformPatterns.psiElement(PsiLiteralExpression.class),
            new JavaI18nPsiReferenceProvider()
        );
    }

    private static final class JavaI18nPsiReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
            if (!(psiElement instanceof PsiLiteralExpression literal)) {
                return PsiReference.EMPTY_ARRAY;
            }

            Project project = psiElement.getProject();

            I18nProjectService projectService = project.getService(I18nProjectService.class);

            EditorFilePath editorFilePath = EditorFilePathExtractor.extract(literal.getContainingFile());

            Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

            if (moduleIdResponse.isEmpty()) {
                // No associated translation module for the editor element
                return PsiReference.EMPTY_ARRAY;
            }

            ModuleId moduleId = moduleIdResponse.get();

            JavaEditorElementExtractor extractor = new JavaEditorElementExtractor();
            EditorElement editorElement = extractor.extract(literal, literal.getContainingFile(), false);

            if (editorElement == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            PossiblyUnavailable<Optional<I18nEntryPreview>> entryResponse =
                projectService.query(new EditorElementI18nEntryPreviewQuery(moduleId, editorElement));

            if (!entryResponse.available()) {
                // Response is not available - module is not loaded yet
                project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId); // Schedule module load
                return PsiReference.EMPTY_ARRAY;
            }

            if (entryResponse.result() != null && entryResponse.result().isPresent()) {
                return new PsiReference[] {
                    new I18nKeyPsiReference<>(literal, moduleId, entryResponse.result().orElseThrow())
                };
            }

            return PsiReference.EMPTY_ARRAY;
        }
    }
}
