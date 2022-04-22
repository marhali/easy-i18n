package de.marhali.easyi18n.assistance.folding;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtStringTemplateEntry;
import org.jetbrains.kotlin.psi.KtStringTemplateExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Kotlin specific translation-key folding.
 * @author marhali
 */
public class KtFoldingBuilder extends AbstractFoldingBuilder {
    @Override
    @NotNull List<Pair<String, PsiElement>> extractRegions(@NotNull PsiElement root) {
        List<Pair<String, PsiElement>> regions = new ArrayList<>();

        for (KtStringTemplateExpression templateExpression : PsiTreeUtil.findChildrenOfType(root, KtStringTemplateExpression.class)) {
            for (KtStringTemplateEntry entry : templateExpression.getEntries()) {
                regions.add(Pair.pair(entry.getText(), templateExpression));
                break;
            }
        }

        return regions;
    }

    @Override
    @Nullable String extractText(@NotNull ASTNode node) {
        KtStringTemplateExpression templateExpression = node.getPsi(KtStringTemplateExpression.class);

        for (KtStringTemplateEntry entry : templateExpression.getEntries()) {
            return entry.getText();
        }

        return null;
    }
}
