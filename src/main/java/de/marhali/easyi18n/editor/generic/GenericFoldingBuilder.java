package de.marhali.easyi18n.editor.generic;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.util.PsiTreeUtil;

import de.marhali.easyi18n.DataStore;
import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.model.KeyPathConverter;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.service.SettingsService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Translation key folding with actual value based on i18n instance.
 * @author marhali
 */
public class GenericFoldingBuilder extends FoldingBuilderEx {

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        Collection<PsiLiteralValue> literalValues = PsiTreeUtil.findChildrenOfType(root, PsiLiteralValue.class);
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        if(!SettingsService.getInstance(root.getProject()).getState().isCodeAssistance()) {
            return FoldingDescriptor.EMPTY;
        }

        DataStore store = InstanceManager.get(root.getProject()).store();
        KeyPathConverter converter = new KeyPathConverter(root.getProject());

        for(final PsiLiteralValue literalValue : literalValues) {
            String value = literalValue.getValue() instanceof String ? (String) literalValue.getValue() : null;

            // Undefined string literal or not a translation
            if(value == null || store.getData().getTranslation(converter.split(value)) == null) {
                continue;
            }

            descriptors.add(new FoldingDescriptor(literalValue.getNode(),
                    new TextRange(literalValue.getTextRange().getStartOffset() + 1,
                            literalValue.getTextRange().getEndOffset() - 1)));
        }

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        PsiLiteralValue literalValue = node.getPsi(PsiLiteralValue.class);
        String value = literalValue.getValue() instanceof String ? (String) literalValue.getValue() : null;

        if(value == null) {
            return null;
        }

        DataStore store = InstanceManager.get(literalValue.getProject()).store();
        KeyPathConverter converter = new KeyPathConverter(literalValue.getProject());

        Translation translation = store.getData().getTranslation(converter.split(value));

        if(translation == null) {
            return null;
        }

        String previewLocale = SettingsService.getInstance(literalValue.getProject()).getState().getPreviewLocale();

        return translation.get(previewLocale);
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }
}