package de.marhali.easyi18n.idea.assistance.xml;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * Responsible for extracting {@link EditorElement} out from {@link XmlAttributeValue} in XML files.
 *
 * @author marhali
 */
public final class XmlEditorElementExtractor implements EditorElementExtractor<XmlAttributeValue, PsiFile> {

    public @Nullable EditorElement extract(@NotNull XmlAttributeValue attributeValue, @Nullable PsiFile psiFile) {
        String value = attributeValue.getValue();

        if (value == null || value.isBlank()) {
            return null;
        }

        PsiElement parent = attributeValue.getParent();
        if (!(parent instanceof XmlAttribute attribute)) {
            return null;
        }

        TriggerKind triggerKind = TriggerKind.PROPERTY_VALUE;
        EditorElement.Builder builder = EditorElement.builder(
            EditorLanguage.XML,
            LiteralKind.STRING,
            triggerKind,
            value
        );

        builder.staticallyKnown(true);
        builder.filePath(extractFilePath(psiFile));
        builder.inTestSources(isInTestSources(attributeValue));
        builder.importSources(Collections.emptySet());

        fillAttributeFacts(attribute, builder);

        return builder.build();
    }

    private void fillAttributeFacts(@NotNull XmlAttribute attribute, @NotNull EditorElement.Builder builder) {
        String attributeName = attribute.getName();
        builder.propertyName(attributeName);

        XmlTag parentTag = attribute.getParent();
        if (parentTag != null) {
            String tagName = parentTag.getName();
            builder.callableName(tagName);
            builder.propertyPath(tagName + "." + attributeName);
        } else {
            builder.propertyPath(attributeName);
        }

        Set<String> markers = Collections.singleton(attributeName);
        builder.declarationMarkers(markers);
    }

    private @Nullable String extractFilePath(@Nullable PsiFile file) {
        if (file == null) {
            return null;
        }
        VirtualFile virtualFile = file.getVirtualFile();
        return virtualFile != null ? virtualFile.getPath() : null;
    }

    private boolean isInTestSources(@NotNull XmlAttributeValue attributeValue) {
        PsiFile file = attributeValue.getContainingFile();
        if (file == null || file.getVirtualFile() == null) {
            return false;
        }
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(attributeValue.getProject());
        return fileIndex.isInTestSourceContent(file.getVirtualFile());
    }
}
