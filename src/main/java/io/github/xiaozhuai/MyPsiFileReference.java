package io.github.xiaozhuai;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class MyPsiFileReference extends MyPsiFileReferenceBase<PsiElement> {
    private final MyStringLiteralHandler handler;

    public MyPsiFileReference(PsiElement psiElement, MyStringLiteralHandler handler) {
        super(psiElement);
        this.handler = handler;
    }

    @NotNull
    @Override
    protected String computeStringValue() {
        return this.handler.getFinalValue(this.getElement());
    }
}