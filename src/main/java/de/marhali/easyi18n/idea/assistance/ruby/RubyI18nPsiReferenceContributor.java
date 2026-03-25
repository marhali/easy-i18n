package de.marhali.easyi18n.idea.assistance.ruby;

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
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;

import java.util.Optional;

/**
 * @author marhali
 */
public class RubyI18nPsiReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(RStringLiteral.class),
            new RubyI18nPsiReferenceProvider()
        );
    }

    private static final class RubyI18nPsiReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext context) {
            if (!(psiElement instanceof RStringLiteral literal)) {
                return PsiReference.EMPTY_ARRAY;
            }

            String key = RubyEditorElementExtractor.getStringContent(literal);
            if (key == null || key.isBlank()) {
                return PsiReference.EMPTY_ARRAY;
            }

            Project project = psiElement.getProject();
            I18nProjectService projectService = project.getService(I18nProjectService.class);

            EditorFilePath editorFilePath = EditorFilePathExtractor.extract(literal.getContainingFile());
            Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

            if (moduleIdResponse.isEmpty()) {
                return PsiReference.EMPTY_ARRAY;
            }

            ModuleId moduleId = moduleIdResponse.get();

            RubyEditorElementExtractor extractor = new RubyEditorElementExtractor();
            EditorElement editorElement = extractor.extract(literal, literal.getContainingFile());

            if (editorElement == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            Boolean matched = projectService.query(new MatchEditorElementQuery(moduleId, editorElement));
            if (!matched) {
                return PsiReference.EMPTY_ARRAY;
            }

            PossiblyUnavailable<Optional<I18nEntryPreview>> entryResponse =
                projectService.query(new I18nEntryPreviewQuery(moduleId, I18nKeyCandidate.of(key)));

            if (!entryResponse.available()) {
                project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId);
                return PsiReference.EMPTY_ARRAY;
            }

            if (entryResponse.result() != null && entryResponse.result().isPresent()) {
                return new PsiReference[]{
                    new I18nKeyPsiReference<>(literal, moduleId, entryResponse.result().orElseThrow())
                };
            }

            return PsiReference.EMPTY_ARRAY;
        }
    }
}
