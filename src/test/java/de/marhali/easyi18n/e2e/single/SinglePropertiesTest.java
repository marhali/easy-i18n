package de.marhali.easyi18n.e2e.single;

import de.marhali.easyi18n.e2e.EndToEndTestCase;
import de.marhali.easyi18n.e2e.TestSettingsState;
import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.io.parser.ParserStrategyType;

/**
 * End-to-end tests for single directory .properties files.
 * @author marhali
 */
public class SinglePropertiesTest extends EndToEndTestCase {
    public SinglePropertiesTest() {
        super(new TestSettingsState(
                "src/test/resources/single/properties",
                FolderStrategyType.SINGLE,
                ParserStrategyType.PROPERTIES)
        );
    }
}