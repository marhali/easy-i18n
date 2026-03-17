package de.marhali.easyi18n.idea.assistance.javascript;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElementVisitor;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.GuessNullableI18nEntryQuery;
import de.marhali.easyi18n.core.application.query.I18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.MatchEditorElementQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.I18nKeyCandidate;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.model.NullableI18nEntry;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.EditorFilePathExtractor;
import de.marhali.easyi18n.idea.assistance.I18nKeyQuickFixIntentionAction;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.ScheduledModuleLoaderService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author marhali
 */
public class JavaScriptI18nKeyLocalInspection extends LocalInspectionTool {

    private final EditorLanguage language;

    public JavaScriptI18nKeyLocalInspection() {
        this(EditorLanguage.JAVASCRIPT);
    }

    protected JavaScriptI18nKeyLocalInspection(EditorLanguage language) {
        this.language = language;
    }

    @Override
    public @Nullable String getDescriptionFileName() {
        return "UnresolvedI18nKey";
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JSElementVisitor() {
            @Override
            public void visitJSLiteralExpression(@NotNull JSLiteralExpression literal) {
                if (!literal.isStringLiteral()) {
                    return;
                }

                String key = literal.getStringValue();
                if (key == null || key.isBlank()) {
                    return;
                }

                Project project = literal.getProject();

                I18nProjectService projectService = project.getService(I18nProjectService.class);

                EditorFilePath editorFilePath = EditorFilePathExtractor.extract(literal.getContainingFile());

                Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

                if (moduleIdResponse.isEmpty()) {
                    // No associated translation module for the editor element
                    return;
                }

                ModuleId moduleId = moduleIdResponse.get();

                JavaScriptEditorElementExtractor extractor = new JavaScriptEditorElementExtractor(language);
                EditorElement editorElement = extractor.extract(literal, literal.getContainingFile(), false);

                if (editorElement == null) {
                    return;
                }

                Boolean editorElementMatched = projectService.query(new MatchEditorElementQuery(moduleId, editorElement));

                if (!editorElementMatched) {
                    // Not targeted by editor rules
                    return;
                }

                PossiblyUnavailable<Optional<I18nEntryPreview>> entryResponse
                    = projectService.query(new I18nEntryPreviewQuery(moduleId, I18nKeyCandidate.of(key)));

                if (!entryResponse.available()) {
                    // Response is not available - module is not loaded yet
                    project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId); // Schedule module load
                    return;
                }

                if (entryResponse.result() == null || entryResponse.result().isPresent()) {
                    return;
                }

                NullableI18nEntry entry = projectService.query(new GuessNullableI18nEntryQuery(moduleId, key));

                holder.registerProblem(
                    literal,
                    PluginBundle.message("editor.intention.unresolved.description", key),
                    new I18nKeyQuickFixIntentionAction(literal, moduleId, entry)
                );
            }
        };
    }
}
