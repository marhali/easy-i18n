package de.marhali.easyi18n.editor.generic;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.editor.KeyReference;
import de.marhali.easyi18n.service.SettingsService;

import org.jetbrains.annotations.NotNull;

/**
 * Generic translation key reference contributor.
 * @author marhali
 */
public class GenericKeyReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralValue.class), getProvider());
    }

    private PsiReferenceProvider getProvider() {
        return new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(
                    @NotNull PsiElement element, @NotNull ProcessingContext context) {

                PsiLiteralValue literalValue = (PsiLiteralValue) element;
                String value = literalValue.getValue() instanceof String ? (String) literalValue.getValue() : null;

                if(value == null) {
                    return PsiReference.EMPTY_ARRAY;
                }

                // Do not reference keys if service is disabled
                if(!SettingsService.getInstance(element.getProject()).getState().isCodeAssistance()) {
                    return PsiReference.EMPTY_ARRAY;
                }

                if(InstanceManager.get(element.getProject()).store().getData().getTranslation(value) == null) {
                    if(!KeyReference.isReferencable(value)) { // Creation policy
                        return PsiReference.EMPTY_ARRAY;
                    }
                }

                return new PsiReference[] { new KeyReference(element, value) };
            }
        };
    }
}
