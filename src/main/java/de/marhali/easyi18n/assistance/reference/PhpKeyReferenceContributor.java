package de.marhali.easyi18n.assistance.reference;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

import org.jetbrains.annotations.NotNull;

/**
 * Php specific key reference binding
 */
public class PhpKeyReferenceContributor extends AbstractKeyReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(StringLiteralExpression.class),
                getProvider());
    }

    private PsiReferenceProvider getProvider() {
        return new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(
                    @NotNull PsiElement element, @NotNull ProcessingContext context) {

                Project project = element.getProject();
                StringLiteralExpression literalExpression = (StringLiteralExpression) element;
                return getReferences(project, element, literalExpression.getContents());
            }
        };
    }
}