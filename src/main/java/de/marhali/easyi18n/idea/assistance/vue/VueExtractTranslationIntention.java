package de.marhali.easyi18n.idea.assistance.vue;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptExtractTranslationIntention;

/**
 * @author marhali
 */
public class VueExtractTranslationIntention extends JavaScriptExtractTranslationIntention {
    public VueExtractTranslationIntention() {
        super(EditorLanguage.VUE);
    }
}
