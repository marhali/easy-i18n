package de.marhali.easyi18n.io;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import de.marhali.easyi18n.exception.EmptyLocalesDirException;
import de.marhali.easyi18n.exception.SyntaxException;
import de.marhali.easyi18n.io.folder.FolderStrategy;
import de.marhali.easyi18n.io.parser.ParserStrategy;
import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.model.*;

import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.util.NotificationHelper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Central component for IO operations based on the configured strategies.
 * @author marhali
 */
public class IOHandler {

    private final @NotNull Project project;
    private final @NotNull ProjectSettings settings;

    private final @NotNull FolderStrategy folderStrategy;

    private final @NotNull ParserStrategyType parserStrategyType;
    private final @NotNull ParserStrategy parserStrategy;

    public IOHandler(@NotNull Project project, @NotNull ProjectSettings settings) throws Exception {
        this.project = project;
        this.settings = settings;

        this.folderStrategy = settings.getFolderStrategy().getStrategy()
                .getDeclaredConstructor(ProjectSettings.class).newInstance(settings);

        this.parserStrategyType = settings.getParserStrategy();
        this.parserStrategy = parserStrategyType.getStrategy()
                .getDeclaredConstructor(ProjectSettings.class).newInstance(settings);
    }

    /**
     * Reads translation files from the local project into our data structure. <br>
     * <b>Note:</b> This method needs to be called from a Read-Action-Context (see ApplicationManager)
     * @return Translation data based on the configured strategies
     * @throws IOException Could not read translation data
     */
    public @NotNull TranslationData read() throws IOException {
        String localesPath = PathMacroManager.getInstance(project)
                .expandPath(this.settings.getLocalesDirectory());

        if(localesPath == null || localesPath.isEmpty()) {
            throw new EmptyLocalesDirException("Locales path must not be empty");
        }

        VirtualFile localesDirectory = LocalFileSystem.getInstance().findFileByIoFile(new File(localesPath));

        if(localesDirectory == null || !localesDirectory.isDirectory()) {
            throw new IllegalArgumentException("Specified locales path is invalid (" + localesPath + ")");
        }

        TranslationData data = new TranslationData(this.settings.isSorting());
        List<TranslationFile> translationFiles = this.folderStrategy.analyzeFolderStructure(localesDirectory);

        for(TranslationFile file : translationFiles) {
            try {
                this.parserStrategy.read(file, data);
            } catch (SyntaxException ex) {
                NotificationHelper.createBadSyntaxNotification(project, ex);
            } catch(Exception ex) {
                throw new IOException(file + "\n\n" + ex.getMessage(), ex);
            }
        }

        return data;
    }

    /**
     * Writes the provided translation data to the local project files <br>
     * <b>Note:</b> This method must be called from a Write-Action-Context (see ApplicationManager)
     * @param data Cached translation data to save
     * @throws IOException Write action failed
     */
    public void write(@NotNull TranslationData data) throws IOException {
        String localesPath = this.settings.getLocalesDirectory();
        boolean isAddBlankLine = this.settings.isAddBlankLine();

        if(localesPath == null || localesPath.isEmpty()) {
            throw new EmptyLocalesDirException("Locales path must not be empty");
        }

        List<TranslationFile> translationFiles =
                this.folderStrategy.constructFolderStructure(localesPath, this.parserStrategyType, data);

        for(TranslationFile file : translationFiles) {
            try {
                String content = this.parserStrategy.write(data, file);

                if(content == null) {
                    // We should consider deleting the target translation file if it has no content
                    continue;
                }

                if (isAddBlankLine && !content.endsWith("\n")) {
                    content += "\n";
                }

                Document document = FileDocumentManager.getInstance().getDocument(file.getVirtualFile());
                assert document != null;

                // content must use \n line separators (internal intellij guideline)
                document.setText(content);

                PsiFile psi = PsiDocumentManager.getInstance(project).getCachedPsiFile(document);

                if(psi == null) {
                    psi = PsiDocumentManager.getInstance(project).getPsiFile(document);
                }

                assert psi != null;

                new ReformatCodeProcessor(psi, false).run();

                FileDocumentManager.getInstance().saveDocument(document);

            } catch (Exception ex) {
                throw new IOException(file + "\n\n" + ex.getMessage(), ex);
            }
        }
    }
}
