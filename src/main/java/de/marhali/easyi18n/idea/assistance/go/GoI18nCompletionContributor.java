package de.marhali.easyi18n.idea.assistance.go;

import com.goide.psi.GoStringLiteral;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.AllModuleI18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.MatchEditorElementQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import de.marhali.easyi18n.idea.assistance.AbstractI18nCompletionContributor;
import de.marhali.easyi18n.idea.assistance.EditorFilePathExtractor;
import de.marhali.easyi18n.idea.icons.PluginIcon;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.ScheduledModuleLoaderService;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * @author marhali
 */
public class GoI18nCompletionContributor extends AbstractI18nCompletionContributor {

    public GoI18nCompletionContributor() {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withParent(GoStringLiteral.class),
            new CompletionProvider<>() {

                @Override
                protected void addCompletions(
                    @NotNull CompletionParameters completionParameters,
                    @NotNull ProcessingContext processingContext,
                    @NotNull CompletionResultSet completionResultSet
                ) {
                    PsiElement position = completionParameters.getPosition();
                    GoStringLiteral literal = findParentOfType(position, GoStringLiteral.class);

                    if (literal == null) {
                        return;
                    }

                    Project project = literal.getProject();

                    I18nProjectService projectService = project.getService(I18nProjectService.class);

                    EditorFilePath editorFilePath = EditorFilePathExtractor.extract(completionParameters.getOriginalFile());

                    Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

                    if (moduleIdResponse.isEmpty()) {
                        return;
                    }

                    ModuleId moduleId = moduleIdResponse.get();

                    GoEditorElementExtractor extractor = new GoEditorElementExtractor();
                    EditorElement editorElement = extractor.extract(literal, completionParameters.getOriginalFile());

                    if (editorElement == null) {
                        return;
                    }

                    Boolean editorElementMatched = projectService.query(new MatchEditorElementQuery(moduleId, editorElement));

                    if (!editorElementMatched) {
                        return;
                    }

                    PossiblyUnavailable<List<I18nEntryPreview>> entriesResponse
                        = projectService.query(new AllModuleI18nEntryPreviewQuery(moduleId));

                    if (!entriesResponse.available()) {
                        project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId);
                        return;
                    }

                    if (entriesResponse.result() == null || entriesResponse.result().isEmpty()) {
                        return;
                    }

                    List<I18nEntryPreview> suggestions = entriesResponse.result();

                    String currentValue = GoEditorElementExtractor.getStringContent(literal);

                    if (currentValue == null) {
                        currentValue = "";
                    }

                    // Value range = literal range minus the surrounding quote characters
                    TextRange literalRange = literal.getTextRange();
                    TextRange absoluteValueRange = TextRange.create(
                        literalRange.getStartOffset() + 1,
                        literalRange.getEndOffset() - 1
                    );

                    int caretOffset = completionParameters.getOffset();
                    if (caretOffset < absoluteValueRange.getStartOffset()) {
                        return;
                    }

                    int relativeCaretOffset = Math.min(
                        Math.max(0, caretOffset - absoluteValueRange.getStartOffset()),
                        currentValue.length()
                    );
                    String prefix = currentValue.substring(0, relativeCaretOffset);

                    CompletionResultSet prefixed = completionResultSet.withPrefixMatcher(prefix);

                    for (I18nEntryPreview suggestion : suggestions) {
                        LookupElementBuilder builder = LookupElementBuilder.create(suggestion.key().canonical())
                            .withInsertHandler(AbstractI18nCompletionContributor::replaceCompletionRange)
                            .withPresentableText(suggestion.key().canonical())
                            .withIcon(PluginIcon.TRANSLATE_ICON);

                        if (suggestion.previewValue() != null) {
                            builder = builder.withTailText(" = " + suggestion.previewValue().toInputString(), true);
                        }

                        prefixed.addElement(builder);
                    }
                }
            }
        );
    }
}
