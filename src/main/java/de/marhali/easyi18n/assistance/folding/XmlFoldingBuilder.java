package de.marhali.easyi18n.assistance.folding;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class XmlFoldingBuilder extends AbstractFoldingBuilder {
    @Override
    @NotNull List<Pair<String, PsiElement>> extractRegions(@NotNull PsiElement root) {
        return PsiTreeUtil.findChildrenOfType(root, XmlAttributeValue.class)
                .stream()
                .map((attributeValue -> Pair.pair(attributeValue.getValue(), (PsiElement) attributeValue)))
                .collect(Collectors.toList());
    }

    @Override
    @Nullable String extractText(@NotNull ASTNode node) {
        XmlAttributeValue attributeValue = node.getPsi(XmlAttributeValue.class);
        return attributeValue.getValue();
    }
}
