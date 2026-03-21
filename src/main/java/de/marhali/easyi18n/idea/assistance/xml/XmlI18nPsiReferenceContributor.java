package de.marhali.easyi18n.idea.assistance.xml;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
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

import java.util.Optional;

/**
 * @author marhali
 */
public class XmlI18nPsiReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
            PlatformPatterns.psiElement(XmlAttributeValue.class),
            new XmlI18nPsiReferenceProvider()
        );
    }

    private static final class XmlI18nPsiReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
            if (!(psiElement instanceof XmlAttributeValue attributeValue)) {
                return PsiReference.EMPTY_ARRAY;
            }

            String key = attributeValue.getValue();
            if (key == null || key.isBlank()) {
                return PsiReference.EMPTY_ARRAY;
            }

            Project project = psiElement.getProject();

            I18nProjectService projectService = project.getService(I18nProjectService.class);

            EditorFilePath editorFilePath = EditorFilePathExtractor.extract(attributeValue.getContainingFile());

            Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

            if (moduleIdResponse.isEmpty()) {
                return PsiReference.EMPTY_ARRAY;
            }

            ModuleId moduleId = moduleIdResponse.get();

            XmlEditorElementExtractor extractor = new XmlEditorElementExtractor();
            EditorElement editorElement = extractor.extract(attributeValue, attributeValue.getContainingFile());

            if (editorElement == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            Boolean editorElementMatched = projectService.query(new MatchEditorElementQuery(moduleId, editorElement));

            if (!editorElementMatched) {
                return PsiReference.EMPTY_ARRAY;
            }

            PossiblyUnavailable<Optional<I18nEntryPreview>> entryResponse
                = projectService.query(new I18nEntryPreviewQuery(moduleId, I18nKeyCandidate.of(key)));

            if (!entryResponse.available()) {
                project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId);
                return PsiReference.EMPTY_ARRAY;
            }

            if (entryResponse.result() != null && entryResponse.result().isPresent()) {
                return new PsiReference[] {
                    new I18nKeyPsiReference<>(attributeValue, moduleId, entryResponse.result().orElseThrow())
                };
            }

            return PsiReference.EMPTY_ARRAY;
        }
    }
}
