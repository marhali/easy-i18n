package de.marhali.easyi18n.idea.assistance.kotlin;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.I18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.MatchEditorElementQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.I18nKeyCandidate;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import de.marhali.easyi18n.idea.assistance.EditorFilePathExtractor;
import de.marhali.easyi18n.idea.assistance.I18nKeyPsiReference;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.ScheduledModuleLoaderService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;
import org.jetbrains.kotlin.psi.KtStringTemplateEntry;
import org.jetbrains.kotlin.psi.KtStringTemplateExpression;

import java.util.Optional;

/**
 * @author marhali
 */
public class KotlinI18nPsiReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
            PlatformPatterns.psiElement(KtStringTemplateExpression.class),
            new KotlinI18nPsiReferenceProvider()
        );
    }

    private static final class KotlinI18nPsiReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
            if (!(psiElement instanceof KtStringTemplateExpression literal)) {
                return PsiReference.EMPTY_ARRAY;
            }

            String key = extractSimpleStringValue(literal);
            if (key == null || key.isBlank()) {
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

            KotlinEditorElementExtractor extractor = new KotlinEditorElementExtractor();
            EditorElement editorElement = extractor.extract(literal, literal.getContainingFile());

            if (editorElement == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            Boolean editorElementMatched = projectService.query(new MatchEditorElementQuery(moduleId, editorElement));

            if (!editorElementMatched) {
                // Not targeted by editor rules
                return PsiReference.EMPTY_ARRAY;
            }

            PossiblyUnavailable<Optional<I18nEntryPreview>> entryResponse
                = projectService.query(new I18nEntryPreviewQuery(moduleId, I18nKeyCandidate.of(key)));

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

        private String extractSimpleStringValue(@NotNull KtStringTemplateExpression literal) {
            KtStringTemplateEntry[] entries = literal.getEntries();
            if (entries.length == 0) {
                return "";
            }
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
    }
}
