package de.marhali.easyi18n.e2e.single;

import de.marhali.easyi18n.e2e.EndToEndTestCase;
import de.marhali.easyi18n.e2e.TestSettingsState;
import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.io.parser.ParserStrategyType;

/**
 * End-to-ends tests for single directory yaml files.
 * @author marhali
 */
public class SingleYamlTest extends EndToEndTestCase {
    public SingleYamlTest() {
        super(new TestSettingsState(
                "src/test/resources/single/yaml",
                FolderStrategyType.SINGLE,
                ParserStrategyType.YML)
        );
    }
}
