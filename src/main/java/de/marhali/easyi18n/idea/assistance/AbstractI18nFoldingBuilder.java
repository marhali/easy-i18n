package de.marhali.easyi18n.idea.assistance;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.I18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.I18nKeyCandidate;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
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
 * Abstract base class for all language-specific i18n folding builders.
 *
 * <p>Provides the complete {@link #buildFoldRegions} implementation via the template method
 * {@link #collectLiterals}: subclasses walk the language-specific PSI tree and invoke the
 * provided {@link LiteralConsumer} for each non-blank string literal candidate. The base class
 * handles all module lookup, key resolution, placeholder sanitization, and descriptor creation.
 *
 * @author marhali
 */
public abstract class AbstractI18nFoldingBuilder extends FoldingBuilderEx implements DumbAware {

    @FunctionalInterface
    protected interface LiteralConsumer {
        /**
         * Callback invoked by {@link #collectLiterals} for each string literal candidate.
         *
         * @param key   the non-blank string value of the literal
         * @param node  the AST node of the literal (used for the FoldingDescriptor)
         * @param range the text range of the literal in document coordinates
         */
        void accept(@NotNull String key, @NotNull ASTNode node, @NotNull TextRange range);
    }

    /**
     * Walk the PSI tree rooted at {@code root} and invoke {@code consumer} for each
     * non-blank string literal. Only filter by whether the element is a non-blank string literal —
     * do NOT filter by i18n key existence or editor rules.
     */
    protected abstract void collectLiterals(@NotNull PsiElement root, @NotNull LiteralConsumer consumer);

    @Override
    public final FoldingDescriptor @NotNull [] buildFoldRegions(
        @NotNull PsiElement root, @NotNull Document document, boolean quick
    ) {
        Project project = root.getProject();
        I18nProjectService projectService = project.getService(I18nProjectService.class);

        EditorFilePath editorFilePath = EditorFilePathExtractor.extract(root.getContainingFile());
        Optional<ModuleId> moduleIdOpt = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));
        if (moduleIdOpt.isEmpty()) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }

        ModuleId moduleId = moduleIdOpt.get();
        ModuleModificationTracker tracker = project.getService(ModuleModificationTracker.class);
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        collectLiterals(root, (key, node, range) -> {
            if (range.isEmpty()) return;

            PossiblyUnavailable<Optional<I18nEntryPreview>> entryResponse =
                projectService.query(new I18nEntryPreviewQuery(moduleId, I18nKeyCandidate.of(key)));

            if (!entryResponse.available() || entryResponse.result() == null) {
                project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId);
                return;
            }

            if (entryResponse.result().isEmpty()) return;

            I18nEntryPreview entryPreview = entryResponse.result().get();
            if (entryPreview.previewValue() == null) return;

            String placeholder = sanitizePlaceholder(entryPreview.previewValue().toInputString());
            descriptors.add(new FoldingDescriptor(
                node, range, null, placeholder, Boolean.TRUE, Set.of(tracker.get(moduleId))
            ));
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

    protected @NotNull String sanitizePlaceholder(@NotNull String value) {
        if (value.isBlank()) {
            return StringUtil.THREE_DOTS;
        }
        return value
            .replace("\n", "\\n")
            .replace("\r", "");
    }
}
