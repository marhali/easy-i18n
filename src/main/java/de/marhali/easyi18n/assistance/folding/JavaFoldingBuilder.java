package de.marhali.easyi18n.assistance.folding;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Java specific translation key folding.
 * @author marhali
 */
public class JavaFoldingBuilder extends AbstractFoldingBuilder {
    @Override
    @NotNull List<Pair<String, PsiElement>> extractRegions(@NotNull PsiElement root) {
        return PsiTreeUtil.findChildrenOfType(root, PsiLiteralExpression.class).stream().map(literalExpression ->
                        Pair.pair(String.valueOf(literalExpression.getValue()), (PsiElement) literalExpression))
                .collect(Collectors.toList());
    }

    @Override
    @Nullable String extractText(@NotNull ASTNode node) {
        PsiLiteralExpression literalExpression = node.getPsi(PsiLiteralExpression.class);
        return String.valueOf(literalExpression.getValue());
    }
}
