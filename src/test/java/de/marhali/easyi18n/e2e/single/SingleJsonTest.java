package de.marhali.easyi18n.e2e.single;

import de.marhali.easyi18n.e2e.EndToEndTestCase;
import de.marhali.easyi18n.e2e.TestSettingsState;
import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.io.parser.ParserStrategyType;

/**
 * End-to-end tests for single directory json files.
 * @author marhali
 */
public class SingleJsonTest extends EndToEndTestCase {
    public SingleJsonTest() {
        super(new TestSettingsState(
                "src/test/resources/single/json",
                FolderStrategyType.SINGLE,
                ParserStrategyType.JSON)
        );
    }
}
