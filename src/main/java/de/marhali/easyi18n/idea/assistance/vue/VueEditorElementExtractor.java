package de.marhali.easyi18n.idea.assistance.vue;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptEditorElementExtractor;

/**
 * Responsible for extracting EditorElement from Vue files.
 * Extends JavaScript extractor since Vue script sections use JavaScript/TypeScript.
 *
 * @author marhali
 */
public final class VueEditorElementExtractor extends JavaScriptEditorElementExtractor {

    public VueEditorElementExtractor() {
        super(EditorLanguage.VUE);
    }
}
