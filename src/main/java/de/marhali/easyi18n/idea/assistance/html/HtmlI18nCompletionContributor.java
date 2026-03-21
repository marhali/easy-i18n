package de.marhali.easyi18n.idea.assistance.html;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
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

import java.util.List;
import java.util.Optional;

/**
 * @author marhali
 */
public class HtmlI18nCompletionContributor extends CompletionContributor {

    public HtmlI18nCompletionContributor() {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withParent(XmlAttributeValue.class),
            new CompletionProvider<>() {

                @Override
                protected void addCompletions(
                    @NotNull CompletionParameters completionParameters,
                    @NotNull ProcessingContext processingContext,
                    @NotNull CompletionResultSet completionResultSet
                ) {
                    PsiElement position = completionParameters.getPosition();
                    XmlAttributeValue attributeValue = findParentOfType(position, XmlAttributeValue.class);

                    if (attributeValue == null) {
                        return;
                    }

                    Project project = attributeValue.getProject();

                    I18nProjectService projectService = project.getService(I18nProjectService.class);

                    EditorFilePath editorFilePath = EditorFilePathExtractor.extract(completionParameters.getOriginalFile());

                    Optional<ModuleId> moduleIdResponse = projectService.query(new ModuleIdByEditorFilePathQuery(editorFilePath));

                    if (moduleIdResponse.isEmpty()) {
                        return;
                    }

                    ModuleId moduleId = moduleIdResponse.get();

                    HtmlEditorElementExtractor extractor = new HtmlEditorElementExtractor();
                    EditorElement editorElement = extractor.extract(attributeValue, completionParameters.getOriginalFile());

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

                    String currentValue = attributeValue.getValue();

                    if (currentValue == null) {
                        currentValue = "";
                    }

                    TextRange valueRange = attributeValue.getValueTextRange();
                    int caretOffset = completionParameters.getOffset();
                    if (caretOffset < valueRange.getStartOffset()) {
                        return;
                    }

                    int relativeCaretOffset = Math.min(
                        Math.max(0, caretOffset - valueRange.getStartOffset()),
                        currentValue.length()
                    );
                    String prefix = currentValue.substring(0, relativeCaretOffset);

                    CompletionResultSet prefixed = completionResultSet.withPrefixMatcher(prefix);

                    for (I18nEntryPreview suggestion : suggestions) {
                        LookupElementBuilder builder = LookupElementBuilder.create(suggestion.key().canonical())
                            .withInsertHandler(HtmlI18nCompletionContributor::replaceCompletionRange)
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

    @SuppressWarnings("unchecked")
    private static <T extends PsiElement> T findParentOfType(@NotNull PsiElement element, @NotNull Class<T> type) {
        PsiElement current = element;
        while (current != null) {
            if (type.isInstance(current)) {
                return (T) current;
            }
            current = current.getParent();
        }
        return null;
    }
}
