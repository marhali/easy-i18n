package de.marhali.easyi18n.editor.kotlin;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;

import com.intellij.util.ProcessingContext;

import de.marhali.easyi18n.editor.KeyReference;
import de.marhali.easyi18n.service.LegacyDataStore;
import de.marhali.easyi18n.service.SettingsService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;
import org.jetbrains.kotlin.psi.KtStringTemplateExpression;

/**
 * Kotlin translation key reference contributor.
 * @author marhali
 */
public class KotlinKeyReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement().inside(KtStringTemplateExpression.class), getProvider());
    }

    private PsiReferenceProvider getProvider() {
        return new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                String value = null;

                for (PsiElement child : element.getChildren()) {
                    if(child instanceof KtLiteralStringTemplateEntry) {
                        value = child.getText();
                    }
                }

                if(value == null) {
                    return PsiReference.EMPTY_ARRAY;
                }

                // Do not reference keys if service is disabled
                if(!SettingsService.getInstance(element.getProject()).getState().isCodeAssistance()) {
                    return PsiReference.EMPTY_ARRAY;
                }

                if(LegacyDataStore.getInstance(element.getProject()).getTranslations().getNode(value) == null) {
                    return PsiReference.EMPTY_ARRAY;
                }

                return new PsiReference[] { new KeyReference(element, value) };
            }
        };
    }
}
