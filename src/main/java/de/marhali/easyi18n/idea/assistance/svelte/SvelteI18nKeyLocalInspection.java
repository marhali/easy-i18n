package de.marhali.easyi18n.idea.assistance.svelte;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nKeyLocalInspection;

/**
 * @author marhali
 */
public class SvelteI18nKeyLocalInspection extends JavaScriptI18nKeyLocalInspection {
    public SvelteI18nKeyLocalInspection() {
        super(EditorLanguage.SVELTE);
    }
}
