package de.marhali.easyi18n.idea.assistance.svelte;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nDocumentationTargetProvider;

/**
 * @author marhali
 */
public class SvelteI18nDocumentationTargetProvider extends JavaScriptI18nDocumentationTargetProvider {

    public SvelteI18nDocumentationTargetProvider() {
        super(EditorLanguage.SVELTE);
    }
}
