package de.marhali.easyi18n.assistance.reference;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * JavaScript specific translation-key reference binding.
 * @author marhali
 */
public class JsKeyReferenceContributor extends AbstractKeyReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(JSLiteralExpression.class),
                getProvider());
    }

    private PsiReferenceProvider getProvider() {
        return new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(
                    @NotNull PsiElement element, @NotNull ProcessingContext context) {

                Project project = element.getProject();
                JSLiteralExpression literalExpression = (JSLiteralExpression) element;
                String value = literalExpression.getStringValue();

                return getReferences(project, element, value);
            }
        };
    }
}