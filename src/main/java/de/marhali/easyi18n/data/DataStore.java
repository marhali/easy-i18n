package de.marhali.easyi18n.data;

import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.SettingsService;
import de.marhali.easyi18n.io.translator.TranslatorIO;
import de.marhali.easyi18n.model.DataSynchronizer;
import de.marhali.easyi18n.model.KeyedTranslation;
import de.marhali.easyi18n.model.TranslationDelete;
import de.marhali.easyi18n.model.TranslationUpdate;
import de.marhali.easyi18n.util.IOUtil;
import de.marhali.easyi18n.util.TranslationsUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton service to manage localized messages.
 * @author marhali
 */
public class DataStore {

    private static DataStore INSTANCE;

    private final Project project;
    private final List<DataSynchronizer> synchronizer;

    private Translations translations;
    private String searchQuery;

    public static DataStore getInstance(Project project) {
        return INSTANCE == null ? INSTANCE = new DataStore(project) : INSTANCE;
    }

    private DataStore(Project project) {
        this.project = project;
        this.synchronizer = new ArrayList<>();
    }

    public void addSynchronizer(DataSynchronizer synchronizer) {
        this.synchronizer.add(synchronizer);
    }

    public void reloadFromDisk() throws IOException {
        String localesPath = SettingsService.getInstance(project).getState().getLocalesPath();

        if(localesPath == null || localesPath.isEmpty()) {
            translations = new Translations(new ArrayList<>(),
                    new LocalizedNode(LocalizedNode.ROOT_KEY, new ArrayList<>()));

        } else {
            TranslatorIO io = IOUtil.determineFormat(localesPath);
            translations = io.read(localesPath);
        }

        // Propagate changes
        synchronizer.forEach(synchronizer -> synchronizer.synchronize(translations, searchQuery));
    }

    public void saveToDisk() {
        String localesPath = SettingsService.getInstance(project).getState().getLocalesPath();

        if(localesPath == null || localesPath.isEmpty()) { // Cannot save without valid path
            return;
        }

        TranslatorIO io = IOUtil.determineFormat(localesPath);
        io.save(translations);
    }

    public void searchBeyKey(String fullPath) {
        // Use synchronizer to propagate search instance to all views
        synchronizer.forEach(synchronizer -> synchronizer.synchronize(translations, this.searchQuery = fullPath));
    }

    public void processUpdate(TranslationUpdate update) {
        if(update.isDeletion() || update.isKeyChange()) { // Delete origin i18n key
            String originKey = update.getOrigin().getKey();
            List<String> sections = TranslationsUtil.getSections(originKey);
            String nodeKey = sections.remove(sections.size() - 1); // Remove last node, which needs to be removed by parent

            LocalizedNode node = translations.getNodes();
            for(String section : sections) {
                if(node == null) { // Might be possible on multi-delete
                    break;
                }

                node = node.getChildren(section);
            }

            if(node != null) { // Only remove if parent exists. Might be already deleted on multi-delete
                node.removeChildren(nodeKey);

                // Parent is empty now, we need to remove it as well (except root)
                if(node.getChildren().isEmpty() && !node.getKey().equals(LocalizedNode.ROOT_KEY)) {
                    processUpdate(new TranslationDelete(new KeyedTranslation(
                            TranslationsUtil.sectionsToFullPath(sections), null)));
                }
            }
        }

        if(!update.isDeletion()) { // Recreate with changed val / create
            LocalizedNode node = translations.getOrCreateNode(update.getChange().getKey());
            node.setValue(update.getChange().getTranslations());
        }

        // Propagate changes and save them
        synchronizer.forEach(synchronizer -> synchronizer.synchronize(translations, searchQuery));
        saveToDisk();
    }

    public Translations getTranslations() {
        return translations;
    }
}