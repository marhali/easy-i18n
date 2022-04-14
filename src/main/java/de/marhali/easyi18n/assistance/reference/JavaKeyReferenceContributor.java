package de.marhali.easyi18n.assistance.reference;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;

import com.intellij.util.ProcessingContext;

import org.jetbrains.annotations.NotNull;

/**
 * Java specific key reference binding.
 * @author marhali
 */
public class JavaKeyReferenceContributor extends AbstractKeyReferenceContributor {

    // TODO: why not PsiLiteralExpression?

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(PsiLiteralValue.class),
                getProvider());
    }

    private PsiReferenceProvider getProvider() {
        return new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(
                    @NotNull PsiElement element, @NotNull ProcessingContext context) {

                Project project = element.getProject();
                PsiLiteralValue literalValue = (PsiLiteralValue) element;
                String value = literalValue.getValue() instanceof String ? (String) literalValue.getValue() : null;

                return getReferences(project, element, value);
            }
        };
    }
}
