package de.marhali.easyi18n.idea.assistance;

import com.intellij.model.Pointer;
import com.intellij.platform.backend.documentation.DocumentationResult;
import com.intellij.platform.backend.documentation.DocumentationTarget;
import com.intellij.platform.backend.presentation.TargetPresentation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.icons.PluginIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author marhali
 */
public class I18nKeyDocumentationTarget implements DocumentationTarget {

    private final @NotNull SmartPsiElementPointer<PsiFile> filePointer;
    private final @NotNull ModuleId moduleId;
    private final @NotNull I18nEntryPreview entryPreview;

    public I18nKeyDocumentationTarget(
        @NotNull PsiFile file,
        @NotNull ModuleId moduleId, @NotNull I18nEntryPreview entryPreview
    ) {
        this.filePointer = SmartPointerManager.createPointer(file);
        this.moduleId = moduleId;
        this.entryPreview = entryPreview;
    }

    @Override
    public @NotNull Pointer<? extends DocumentationTarget> createPointer() {
        SmartPsiElementPointer<PsiFile> stableFilePointer = filePointer;
        I18nEntryPreview stableEntryPreview = entryPreview;

        return () -> {
            PsiFile file = stableFilePointer.getElement();
            return file != null
                ? new I18nKeyDocumentationTarget(file, moduleId, stableEntryPreview)
                : null;
        };
    }

    @Override
    public @NotNull TargetPresentation computePresentation() {
        return TargetPresentation.builder(entryPreview.key().canonical())
            .containerText(computeDocumentationHint())
            .icon(PluginIcon.TRANSLATE_ICON)
            .locationText(moduleId.name(), PluginIcon.TRANSLATE_ICON)
            .presentation();
    }

    @Override
    public @Nullable String computeDocumentationHint() {
        return getI18nKeyString() + "=" + getPreviewValueString();
    }

    @Override
    public @Nullable DocumentationResult computeDocumentation() {
        return DocumentationResult.documentation(
            "<b>%s</b>=%s".formatted(getI18nKeyString(), getPreviewValueString())
        );
    }

    private @NotNull String getI18nKeyString() {
        return entryPreview.key().canonical();
    }

    private @NotNull String getPreviewValueString() {
        return entryPreview.previewValue() != null
            ? entryPreview.previewValue().toInputString()
            : "";
    }
}
