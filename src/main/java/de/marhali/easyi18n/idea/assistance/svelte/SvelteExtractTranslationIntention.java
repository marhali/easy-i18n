package de.marhali.easyi18n.idea.assistance.svelte;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptExtractTranslationIntention;

/**
 * @author marhali
 */
public class SvelteExtractTranslationIntention extends JavaScriptExtractTranslationIntention {
    public SvelteExtractTranslationIntention() {
        super(EditorLanguage.SVELTE);
    }
}
