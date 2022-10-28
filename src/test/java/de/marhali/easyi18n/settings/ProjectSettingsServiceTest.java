package de.marhali.easyi18n.settings;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.xmlb.XmlSerializerUtil;

import de.marhali.easyi18n.settings.presets.DefaultPreset;

import org.jetbrains.annotations.NotNull;

/**
 * Tests for the project settings service itself.
 * @author marhali
 */
public class ProjectSettingsServiceTest extends BasePlatformTestCase {

    @Override
    protected @NotNull String getTestName(boolean lowercaseFirstLetter) {
        return lowercaseFirstLetter ? "projectSettingsServiceTest" : "ProjectSettingsServiceTest";
    }

    public void testSettingsDefaultPreset() {
        ProjectSettingsState state = ProjectSettingsService.get(getProject()).getState();
        assertEquals(new ProjectSettingsState(new DefaultPreset()), state);
    }

    public void testPersistenceState() {
        ProjectSettingsState previous = new ProjectSettingsState(new SettingsTestPreset());
        ProjectSettingsState after = XmlSerializerUtil.createCopy(previous);
        assertEquals(previous, after);
    }

    public void testPersistenceSingle() {
        ProjectSettingsState previous = new ProjectSettingsState();
        previous.setLocalesDirectory("mySinglePropTest");

        ProjectSettingsState after = XmlSerializerUtil.createCopy(previous);
        assertEquals("mySinglePropTest", after.getLocalesDirectory());
    }
}
