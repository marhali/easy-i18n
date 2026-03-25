package de.marhali.easyi18n.idea.assistance.svelte;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nFoldingBuilder;

/**
 * @author marhali
 */
public class SvelteI18nFoldingBuilder extends JavaScriptI18nFoldingBuilder {
    public SvelteI18nFoldingBuilder() {
        super(EditorLanguage.SVELTE);
    }
}
