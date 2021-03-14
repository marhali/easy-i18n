package de.marhali.easyi18n.model.tree;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;

import de.marhali.easyi18n.service.SettingsService;
import de.marhali.easyi18n.model.LocalizedNode;
import de.marhali.easyi18n.model.Translations;
import de.marhali.easyi18n.util.TranslationsUtil;
import de.marhali.easyi18n.util.UiUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * I18n key tree preparation.
 * @author marhali
 */
public class TreeModelTranslator extends DefaultTreeModel {

    private final @NotNull Project project;
    private final @NotNull Translations translations;
    private final @Nullable String searchQuery;


    public TreeModelTranslator(
            @NotNull Project project, @NotNull Translations translations, @Nullable String searchQuery) {
        super(null);

        this.project = project;
        this.translations = translations;
        this.searchQuery = searchQuery;

        setRoot(generateNodes());
    }

    private DefaultMutableTreeNode generateNodes() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(LocalizedNode.ROOT_KEY);

        if(translations.getNodes().isLeaf()) { // Empty tree
            return root;
        }

        List<String> searchSections = searchQuery == null ?
                Collections.emptyList() : TranslationsUtil.getSections(searchQuery);

        for(LocalizedNode children : translations.getNodes().getChildren()) {
            generateSubNodes(root, children, new ArrayList<>(searchSections));
        }

        return root;
    }

    private void generateSubNodes(DefaultMutableTreeNode parent,
                                  LocalizedNode localizedNode, List<String> searchSections) {

        String searchKey = searchSections.isEmpty() ? null : searchSections.remove(0);

        if(searchKey != null && !localizedNode.getKey().startsWith(searchKey)) { // Filter node
            return;
        }

        if(localizedNode.isLeaf()) {
            String previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();

            String title = localizedNode.getKey();
            String sub = "(" + previewLocale + ": " + localizedNode.getValue().get(previewLocale) + ")";
            String tooltip = UiUtil.generateHtmlTooltip(localizedNode.getValue());

            PresentationData data = new PresentationData(title, sub, null, null);
            data.setTooltip(tooltip);

            if(localizedNode.getValue().size() != translations.getLocales().size()) {
                data.setForcedTextForeground(JBColor.RED);
            }

            parent.add(new DefaultMutableTreeNode(data));

        } else {
            DefaultMutableTreeNode sub = new DefaultMutableTreeNode(localizedNode.getKey());
            parent.add(sub);

            for(LocalizedNode children : localizedNode.getChildren()) {
                generateSubNodes(sub, children, new ArrayList<>(searchSections));
            }
        }
    }
}
