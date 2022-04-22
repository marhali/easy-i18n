package de.marhali.easyi18n.assistance.reference;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;
import org.jetbrains.kotlin.psi.KtStringTemplateExpression;

import java.util.Arrays;
import java.util.Optional;

/**
 * Kotlin specific translation-key reference binding.
 * @author marhali
 */
public class KtKeyReferenceContributor extends AbstractKeyReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement().inside(KtStringTemplateExpression.class),
                getProvider());
    }

    private PsiReferenceProvider getProvider() {
        return new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(
                    @NotNull PsiElement element, @NotNull ProcessingContext context) {

                Optional<PsiElement> targetElement = Arrays.stream(element.getChildren()).filter(child ->
                        child instanceof KtLiteralStringTemplateEntry).findAny();

                if(targetElement.isEmpty()) {
                    return PsiReference.EMPTY_ARRAY;
                }

                return getReferences(element.getProject(), element, targetElement.get().getText());
            }
        };
    }
}
