package de.marhali.easyi18n.idea.assistance;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import org.jetbrains.annotations.NotNull;

/**
 * Extractor to retrieve {@link EditorFilePath} from psi elements.
 *
 * @author marhali
 */
public final class EditorFilePathExtractor {

    private EditorFilePathExtractor() {}

    public static @NotNull EditorFilePath extract(@NotNull PsiElement psiElement) {
        return extract(psiElement.getContainingFile());
    }

    public static @NotNull EditorFilePath extract(@NotNull PsiFile psiFile) {
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null || !virtualFile.isInLocalFileSystem()) {
            // PsiFile may be an injected language fragment whose VirtualFile is synthetic
            // (e.g. Vue template {{ }} expressions produce a LightVirtualFile at "/Vue.vue.int").
            // Climb up to the actual host file via the PSI context.
            PsiElement context = psiFile.getContext();
            if (context != null) {
                return extract(context.getContainingFile());
            }
        }
        return new EditorFilePath(virtualFile != null ? virtualFile.getPath() : null);
    }
}
