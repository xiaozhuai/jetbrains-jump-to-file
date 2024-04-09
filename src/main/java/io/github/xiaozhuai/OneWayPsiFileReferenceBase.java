package io.github.xiaozhuai;

import com.google.common.collect.ComparisonChain;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

public abstract class OneWayPsiFileReferenceBase<T extends PsiElement> extends PsiPolyVariantReferenceBase<T> {

    public OneWayPsiFileReferenceBase(T psiElement) {
        super(psiElement, true);
    }

    @NotNull
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        String fileName = computeStringValue();
        System.out.println("### filename: " + fileName);
        if (fileName.isEmpty()) {
            return ResolveResult.EMPTY_ARRAY;
        }

        final String finalFileName = fileName.replace("\\", "/");

        Project project = getElement().getProject();
        ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();

        final Set<VirtualFile> sortedResults = new TreeSet<>((o1, o2) -> ComparisonChain.start().
                compare(o1, o2, (o11, o21) -> {
                    String o1CanonicalPath = o11.getCanonicalPath();
                    String o2CanonicalPath = o21.getCanonicalPath();
                    if (o1CanonicalPath != null && o2CanonicalPath != null) {
                        return o1CanonicalPath.compareTo(o2CanonicalPath);
                    } else {
                        return 0;
                    }
                }).
                compare(o1.getName(), o2.getName()).
                result());

        fileIndex.iterateContent(fileOrDir -> {
            if (!fileOrDir.isDirectory()) {
                final String finalTargetCleanFileName = fileOrDir.getPath().replace("\\", "/");
                if (finalTargetCleanFileName.endsWith(finalFileName)) {
                    sortedResults.add(fileOrDir);
                }
            }
            return true;
        });

        PsiManager psiManager = PsiManager.getInstance(project);
        ResolveResult[] result = new ResolveResult[sortedResults.size()];
        int i = 0;
        for (VirtualFile file : sortedResults) {
            PsiFile psiFile = psiManager.findFile(file);
            if (psiFile != null) {
                result[i++] = new PsiElementResolveResult(psiFile);
            }
        }
        return result;
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        return EMPTY_ARRAY;
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        return false;
    }

    @NotNull
    protected abstract String computeStringValue();

}