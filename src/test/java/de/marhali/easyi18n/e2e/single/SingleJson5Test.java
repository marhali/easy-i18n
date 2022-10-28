package de.marhali.easyi18n.e2e.single;

import de.marhali.easyi18n.e2e.EndToEndTestCase;
import de.marhali.easyi18n.e2e.TestSettingsState;
import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.io.parser.ParserStrategyType;

/**
 * @author marhali
 * End-to-end tests for single directory json5 files.
 */
public class SingleJson5Test extends EndToEndTestCase {
    public SingleJson5Test() {
        super(new TestSettingsState(
                "src/test/resources/single/json5",
                FolderStrategyType.SINGLE,
                ParserStrategyType.JSON5)
        );
    }
}
