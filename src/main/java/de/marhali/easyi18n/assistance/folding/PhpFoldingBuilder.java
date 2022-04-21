package de.marhali.easyi18n.assistance.folding;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Php specific translation key folding.
 * @author marhali
 */
public class PhpFoldingBuilder extends AbstractFoldingBuilder {
    @Override
    @NotNull List<Pair<String, PsiElement>> extractRegions(@NotNull PsiElement root) {
        return PsiTreeUtil.findChildrenOfType(root, StringLiteralExpression.class).stream().map(literalExpression ->
                        Pair.pair(literalExpression.getContents(), (PsiElement) literalExpression))
                .collect(Collectors.toList());
    }

    @Override
    @Nullable String extractText(@NotNull ASTNode node) {
        StringLiteralExpression literalExpression = node.getPsi(StringLiteralExpression.class);
        return literalExpression.getContents();
    }
}
