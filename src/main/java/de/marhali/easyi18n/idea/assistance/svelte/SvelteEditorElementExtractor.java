package de.marhali.easyi18n.idea.assistance.svelte;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptEditorElementExtractor;

/**
 * Responsible for extracting EditorElement from Svelte files.
 * Extends JavaScript extractor since Svelte script sections use JavaScript/TypeScript.
 *
 * @author marhali
 */
public final class SvelteEditorElementExtractor extends JavaScriptEditorElementExtractor {

    public SvelteEditorElementExtractor() {
        super(EditorLanguage.SVELTE);
    }
}
