package de.marhali.easyi18n.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;


public class DocumentUtil {
    protected Document document;
    FileType fileType;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        VirtualFile virtualFile = fileDocumentManager.getFile(document);
        if (virtualFile != null) {
            fileType = virtualFile.getFileType();
        }
    }

    public DocumentUtil(Document document) {
       setDocument(document);
    }

    public boolean isJsOrTs() {
        return (fileType.getDefaultExtension().contains("js") || fileType.getDescription().contains("ts"));
    }

    public boolean isVue() {
            return fileType.getDefaultExtension().contains("vue");
    }

}
