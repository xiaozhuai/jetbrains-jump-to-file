package io.github.xiaozhuai;

import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class MyFileReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        MyStringLiteralHandler[] handlers = MyStringLiteralHandler.getAllHandlers();
        for (MyStringLiteralHandler handler : handlers) {
            if (handler.getClazz() == null) {
                continue;
            }
            registerReferenceProvider(registrar, handler);
        }
    }

    private void registerReferenceProvider(PsiReferenceRegistrar registrar, MyStringLiteralHandler handler) {
        registrar.registerReferenceProvider(StandardPatterns.instanceOf(handler.getClazz()), new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                return new PsiReference[]{
                        new MyPsiFileReference(element, handler)
                };
            }
        });
    }
}
