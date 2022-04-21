package de.marhali.easyi18n.assistance.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JavaScript specific translation key folding.
 * @author marhali
 */
public class JsFoldingBuilder extends AbstractFoldingBuilder {
    @Override
    @NotNull List<Pair<String, PsiElement>> extractRegions(@NotNull PsiElement root) {
        return PsiTreeUtil.findChildrenOfType(root, JSLiteralExpression.class).stream().map(literalExpression ->
                        Pair.pair(literalExpression.getStringValue(), (PsiElement) literalExpression))
                .collect(Collectors.toList());
    }

    @Override
    @Nullable String extractText(@NotNull ASTNode node) {
        JSLiteralExpression literalExpression = node.getPsi(JSLiteralExpression.class);
        return literalExpression.getStringValue();
    }
}
