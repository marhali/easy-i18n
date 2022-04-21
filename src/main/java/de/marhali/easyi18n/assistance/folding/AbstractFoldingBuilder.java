package de.marhali.easyi18n.assistance.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import de.marhali.easyi18n.DataStore;
import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.assistance.OptionalAssistance;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Language specific translation key folding with representative locale value.
 * @author marhali
 */
abstract class AbstractFoldingBuilder extends FoldingBuilderEx implements OptionalAssistance {
    /**
     * Extract all relevant folding regions for the desired root element.
     * The implementation does not need to verify if the character literal is a valid translation.
     * @param root Root element
     * @return found regions
     */
    abstract @NotNull List<Pair<String, PsiElement>> extractRegions(@NotNull PsiElement root);

    /**
     * Extract the text from the given node.
     * @param node Node
     * @return extracted text or null if not applicable
     */
    abstract @Nullable String extractText(@NotNull ASTNode node);

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {

        if(quick || !isAssistance(root.getProject())) {
            return FoldingDescriptor.EMPTY;
        }

        List<FoldingDescriptor> descriptors = new ArrayList<>();

        TranslationData data = InstanceManager.get(root.getProject()).store().getData();
        KeyPathConverter converter = new KeyPathConverter(root.getProject());

        for(Pair<String, PsiElement> region : extractRegions(root)) {
            if(data.getTranslation(converter.fromString(region.first)) == null) {
                continue;
            }

            TextRange range = new TextRange(region.second.getTextRange().getStartOffset() + 1,
                    region.second.getTextRange().getEndOffset() - 1);

            // Some language implementations like [Vue Template] does not support FoldingGroup's
            FoldingDescriptor descriptor = new FoldingDescriptor(region.second.getNode(), range);

            descriptors.add(descriptor);
        }

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        String text = extractText(node);

        if(text == null) {
            return null;
        }

        Project project = node.getPsi().getProject();
        DataStore store = InstanceManager.get(project).store();
        KeyPathConverter converter = new KeyPathConverter(project);
        TranslationValue localeValues = store.getData().getTranslation(converter.fromString(text));

        if(localeValues == null) {
            return null;
        }

        String previewLocale = ProjectSettingsService.get(project).getState().getPreviewLocale();
        return localeValues.get(previewLocale);
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }
}
