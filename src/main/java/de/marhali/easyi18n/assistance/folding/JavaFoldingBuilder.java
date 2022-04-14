package de.marhali.easyi18n.assistance.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.util.PsiTreeUtil;

import de.marhali.easyi18n.DataStore;
import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Java specific translation key folding.
 * @author marhali
 */
public class JavaFoldingBuilder extends AbstractFoldingBuilder {

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        if(!isAssistance(root.getProject())) {
            return FoldingDescriptor.EMPTY;
        }

        Collection<PsiLiteralExpression> literalExpressions =
                PsiTreeUtil.findChildrenOfType(root, PsiLiteralExpression.class);

        DataStore store = InstanceManager.get(root.getProject()).store();
        KeyPathConverter converter = new KeyPathConverter(root.getProject());

        for(final PsiLiteralExpression literalExpression : literalExpressions) {
            String value = literalExpression.getValue() instanceof String
                    ? (String) literalExpression.getValue() : null;

            if(value == null || store.getData().getTranslation(converter.fromString(value)) == null) {
                continue;
            }

            TextRange range = literalExpression.getTextRange();
            FoldingDescriptor descriptor = new FoldingDescriptor(literalExpression.getNode(),
                    new TextRange(range.getStartOffset() + 1, range.getEndOffset() - 1), group);

            descriptors.add(descriptor);
        }

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        PsiLiteralExpression literalExpression = node.getPsi(PsiLiteralExpression.class);
        String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
        return getPlaceholderText(literalExpression.getProject(), value);
    }
}
