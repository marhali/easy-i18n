package de.marhali.easyi18n.assistance.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import de.marhali.easyi18n.DataStore;
import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtStringTemplateEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Kotlin specific translation-key folding.
 * @author marhali
 */
public class KtFoldingBuilder extends AbstractFoldingBuilder {
    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        if(!isAssistance(root.getProject())) {
            return FoldingDescriptor.EMPTY;
        }

        Collection<KtStringTemplateEntry> templateEntries =
                PsiTreeUtil.findChildrenOfType(root, KtStringTemplateEntry.class);

        DataStore store = InstanceManager.get(root.getProject()).store();
        KeyPathConverter converter = new KeyPathConverter(root.getProject());

        for (KtStringTemplateEntry templateEntry : templateEntries) {
            String value = templateEntry.getText();

            if(value == null || store.getData().getTranslation(converter.fromString(value)) == null) {
                continue;
            }

            TextRange range = templateEntry.getTextRange();
            FoldingDescriptor descriptor = new FoldingDescriptor(templateEntry.getNode(),
                    new TextRange(range.getStartOffset(), range.getEndOffset()), group);

            descriptors.add(descriptor);
        }

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        KtStringTemplateEntry templateEntry = node.getPsi(KtStringTemplateEntry.class);
        return getPlaceholderText(templateEntry.getProject(), templateEntry.getText());
    }
}
