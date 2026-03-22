package de.marhali.easyi18n.idea.assistance.vue;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSRecursiveWalkingElementVisitor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.I18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.I18nKeyCandidate;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.EditorFilePathExtractor;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nFoldingBuilder;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.ModuleModificationTracker;
import de.marhali.easyi18n.idea.service.ScheduledModuleLoaderService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Folding builder for Vue SFC files.
 *
 * <p>Vue template expressions ({{ $t('key') }}) are injected as a separate language fragment
 * whose {@link com.intellij.openapi.vfs.VirtualFile} is a synthetic {@code LightVirtualFile}
 * (path ends in {@code .int}) and is not in the local file system. The base
 * {@link JavaScriptI18nFoldingBuilder} skips such fragments to avoid coordinate-space mismatches
 * between the injected document and the host document.
 *
 * <p>This subclass handles the template expressions explicitly: it walks the host Vue PSI tree,
 * enumerates injected JS fragments via {@link InjectedLanguageManager}, converts each literal's
 * range from injected-document space to host-document space, and locates the smallest host PSI
 * node that covers the converted range — satisfying {@link FoldingDescriptor}'s requirement that
 * the node's text range contains the folding range.
 *
 * @author marhali
 */
public class VueTemplateI18nFoldingBuilder extends JavaScriptI18nFoldingBuilder {

    public VueTemplateI18nFoldingBuilder() {
        super(EditorLanguage.VUE);
    }

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        Project project = root.getProject();

        I18nProjectService projectService = project.getService(I18nProjectService.class);

        EditorFilePath editorFilePath = EditorFilePathExtractor.extract(root.getContainingFile());

        Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

        if (moduleIdResponse.isEmpty()) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }

        ModuleId moduleId = moduleIdResponse.get();
        ModuleModificationTracker tracker = project.getService(ModuleModificationTracker.class);
        InjectedLanguageManager injectedLangManager = InjectedLanguageManager.getInstance(project);
        PsiFile hostFile = root.getContainingFile();
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        root.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);

                injectedLangManager.enumerate(element, (injectedPsi, places) ->
                    injectedPsi.accept(new JSRecursiveWalkingElementVisitor() {
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

                            // Convert the literal's range from injected-document space to host-document space.
                            TextRange hostRange = injectedLangManager.injectedToHost(literal, literal.getTextRange());
                            if (hostRange.isEmpty()) {
                                return;
                            }

                            // FoldingDescriptor requires node.getTextRange().contains(hostRange).
                            // Walk up the host PSI tree to find the smallest element that covers hostRange.
                            PsiElement hostElement = hostFile.findElementAt(hostRange.getStartOffset());
                            while (hostElement != null && !hostElement.getTextRange().contains(hostRange)) {
                                hostElement = hostElement.getParent();
                            }
                            if (hostElement == null) {
                                return;
                            }

                            descriptors.add(new FoldingDescriptor(
                                hostElement.getNode(),
                                hostRange,
                                null,
                                placeholder,
                                Boolean.TRUE,
                                Set.of(tracker.get(moduleId))
                            ));
                        }
                    })
                );
            }
        });

        return descriptors.toArray(FoldingDescriptor.EMPTY_ARRAY);
    }
}
