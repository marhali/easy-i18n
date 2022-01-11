package de.marhali.easyi18n.io.yaml;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.io.IOStrategy;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.util.NotificationHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import thito.nodeflow.config.MapSection;
import thito.nodeflow.config.Section;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Consumer;

/**
 * Strategy for simple yaml locale files. Each locale has its own file.
 * For example localesPath/en.y(a)ml, localesPath/de.y(a)ml
 * @author marhali
 */
public class YamlIOStrategy implements IOStrategy {

    private final String FILE_EXTENSION;

    public YamlIOStrategy(@NotNull String fileExtension) {
        this.FILE_EXTENSION = fileExtension;
    }

    @Override
    public boolean canUse(@NotNull Project project, @NotNull String localesPath, @NotNull SettingsState state) {
        VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(localesPath));

        if(directory == null || directory.getChildren() == null) {
            return false;
        }

        for(VirtualFile children : directory.getChildren()) {
            if(!children.isDirectory() && isFileRelevant(state, children)) {
                if(children.getExtension().equalsIgnoreCase(FILE_EXTENSION)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void read(@NotNull Project project, @NotNull String localesPath,
                     @NotNull SettingsState state, @NotNull Consumer<@Nullable TranslationData> result) {
        ApplicationManager.getApplication().saveAll(); // Save opened files (required if new locales were added)

        ApplicationManager.getApplication().runReadAction(() -> {
            VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(localesPath));

            if(directory == null || directory.getChildren() == null) {
                throw new IllegalArgumentException("Specified folder is invalid (" + localesPath + ")");
            }

            TranslationData data = new TranslationData(state.isSortKeys(), state.isNestedKeys());

            for(VirtualFile file : directory.getChildren()) {
                if(file.isDirectory() || !isFileRelevant(state, file)) {
                    continue;
                }

                String locale = file.getNameWithoutExtension();
                data.addLocale(locale);

                try {
                    try(Reader reader = new InputStreamReader(file.getInputStream(), file.getCharset())) {
                        Section section = Section.parseToMap(reader);
                        YamlMapper.read(locale, section, data.getRootNode());
                    }

                } catch (Exception e) {
                    NotificationHelper.createIOError(file.getName(), this.getClass(), e);
                    result.accept(null);
                    return;
                }
            }

            result.accept(data);
        });
    }

    @Override
    public void write(@NotNull Project project, @NotNull String localesPath,
                      @NotNull SettingsState state, @NotNull TranslationData data, @NotNull Consumer<Boolean> result) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                for(String locale : data.getLocales()) {
                    Section section = new MapSection();
                    YamlMapper.write(locale, section, data.getRootNode());

                    File file = new File(localesPath + "/" + locale + "." + FILE_EXTENSION);
                    boolean exists = file.createNewFile();

                    VirtualFile vf = exists
                            ? LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
                            : LocalFileSystem.getInstance().findFileByIoFile(file);

                    vf.setBinaryContent(Section.toString(section).getBytes(vf.getCharset()));
                }

                result.accept(true);

            } catch(IOException e) {
                e.printStackTrace();
                result.accept(false);
            }
        });
    }
}