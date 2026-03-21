package de.marhali.easyi18n.idea.assistance.vue;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nKeyLocalInspection;

/**
 * @author marhali
 */
public class VueI18nKeyLocalInspection extends JavaScriptI18nKeyLocalInspection {
    public VueI18nKeyLocalInspection() {
        super(EditorLanguage.VUE);
    }
}
