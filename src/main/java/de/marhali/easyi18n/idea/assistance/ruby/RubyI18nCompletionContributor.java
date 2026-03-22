package de.marhali.easyi18n.idea.assistance.ruby;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.AllModuleI18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.query.MatchEditorElementQuery;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import de.marhali.easyi18n.idea.assistance.EditorFilePathExtractor;
import de.marhali.easyi18n.idea.icons.PluginIcon;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.ScheduledModuleLoaderService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;

import java.util.List;
import java.util.Optional;

/**
 * @author marhali
 */
public class RubyI18nCompletionContributor extends CompletionContributor {

    public RubyI18nCompletionContributor() {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withParent(RStringLiteral.class),
            new CompletionProvider<>() {

                @Override
                protected void addCompletions(
                    @NotNull CompletionParameters params,
                    @NotNull ProcessingContext context,
                    @NotNull CompletionResultSet resultSet
                ) {
                    PsiElement position = params.getPosition();
                    RStringLiteral literal = PsiTreeUtil.getParentOfType(position, RStringLiteral.class, false);

                    if (literal == null || RubyEditorElementExtractor.isInterpolatedString(literal)) {
                        return;
                    }

                    Project project = literal.getProject();
                    I18nProjectService projectService = project.getService(I18nProjectService.class);

                    EditorFilePath editorFilePath = EditorFilePathExtractor.extract(params.getOriginalFile());
                    Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

                    if (moduleIdResponse.isEmpty()) {
                        return;
                    }

                    ModuleId moduleId = moduleIdResponse.get();

                    RubyEditorElementExtractor extractor = new RubyEditorElementExtractor();
                    EditorElement editorElement = extractor.extract(literal, params.getOriginalFile());

                    if (editorElement == null) {
                        return;
                    }

                    Boolean matched = projectService.query(new MatchEditorElementQuery(moduleId, editorElement));
                    if (!matched) {
                        return;
                    }

                    PossiblyUnavailable<List<I18nEntryPreview>> entriesResponse =
                        projectService.query(new AllModuleI18nEntryPreviewQuery(moduleId));

                    if (!entriesResponse.available()) {
                        project.getService(ScheduledModuleLoaderService.class).loadModule(moduleId);
                        return;
                    }

                    if (entriesResponse.result() == null || entriesResponse.result().isEmpty()) {
                        return;
                    }

                    String currentValue = literal.getContentValue();
                    if (currentValue == null) {
                        currentValue = "";
                    }

                    int caretOffset = params.getOffset();
                    int literalStart = literal.getTextRange().getStartOffset() + 1; // skip opening quote
                    if (caretOffset < literalStart) {
                        return;
                    }

                    int relativeCaretOffset = Math.min(
                        Math.max(0, caretOffset - literalStart),
                        currentValue.length()
                    );
                    String prefix = currentValue.substring(0, relativeCaretOffset);

                    CompletionResultSet prefixed = resultSet.withPrefixMatcher(prefix);

                    for (I18nEntryPreview suggestion : entriesResponse.result()) {
                        LookupElementBuilder builder = LookupElementBuilder
                            .create(suggestion.key().canonical())
                            .withInsertHandler(RubyI18nCompletionContributor::replaceCompletionRange)
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

    private static void replaceCompletionRange(@NotNull InsertionContext context, @NotNull LookupElement item) {
        Document document = context.getDocument();
        int startOffset = context.getStartOffset();
        int endOffset = context.getTailOffset();

        if (startOffset < 0 || endOffset < startOffset || endOffset > document.getTextLength()) {
            return;
        }

        String newText = item.getLookupString();
        document.replaceString(startOffset, endOffset, newText);

        int newTailOffset = startOffset + newText.length();
        context.setTailOffset(newTailOffset);
        context.getEditor().getCaretModel().moveToOffset(newTailOffset);
    }
}
