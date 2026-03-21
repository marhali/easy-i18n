package de.marhali.easyi18n.idea.assistance.svelte;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nPsiReferenceContributor;

/**
 * @author marhali
 */
public class SvelteI18nPsiReferenceContributor extends JavaScriptI18nPsiReferenceContributor {
    public SvelteI18nPsiReferenceContributor() {
        super(EditorLanguage.SVELTE);
    }
}
