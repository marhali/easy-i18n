package de.marhali.easyi18n.e2e;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.settings.ProjectSettingsState;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * End-to-end test case.
 * @author marhali
 */
public abstract class EndToEndTestCase extends BasePlatformTestCase {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final ProjectSettings settings;
    private Path tempPath;

    public EndToEndTestCase(ProjectSettings settings) {
        this.settings = settings;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ProjectSettingsService.get(getProject()).setState(new ProjectSettingsState(settings));
        tempPath = Files.createTempDirectory("tests-easyi18n-");
    }

    @Override
    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(tempPath.toFile());
        super.tearDown();
    }

    public void testParseAndSerialize() throws IOException {
        // Read translation files based on the provided settings
        InstanceManager.get(getProject()).reload();

        // Save the cached translation data to a temporary output directory
        ProjectSettingsState out = new ProjectSettingsState(settings);
        out.setLocalesDirectory(tempPath.toString());
        ProjectSettingsService.get(getProject()).setState(out);

        InstanceManager.get(getProject()).store().saveToPersistenceLayer(success -> {});

        // Compare file structure and contents
        IOFileFilter fileFilter = TrueFileFilter.INSTANCE;

        File originalDirectory = new File(Objects.requireNonNull(settings.getLocalesDirectory()));
        Collection<File> originalFiles = FileUtils.listFiles(originalDirectory, fileFilter, fileFilter);

        File outputDirectory = tempPath.toFile();
        Collection<File> outputFiles = FileUtils.listFiles(outputDirectory, fileFilter, fileFilter);

        assertEquals(originalFiles.size(), outputFiles.size());

        Iterator<File> originalFilesIterator = originalFiles.iterator();
        Iterator<File> outputFilesIterator = outputFiles.iterator();

        while(originalFilesIterator.hasNext()) {
            File originalFile = originalFilesIterator.next();
            File outputFile = outputFilesIterator.next();

            assertEquals(FileUtils.readFileToString(originalFile, CHARSET),
                    FileUtils.readFileToString(outputFile, CHARSET));
        }
    }
}
