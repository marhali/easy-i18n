package de.marhali.easyi18n.idea.assistance.svelte;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nCompletionContributor;

/**
 * @author marhali
 */
public class SvelteI18nCompletionContributor extends JavaScriptI18nCompletionContributor {
    public SvelteI18nCompletionContributor() {
        super(EditorLanguage.SVELTE);
    }
}
