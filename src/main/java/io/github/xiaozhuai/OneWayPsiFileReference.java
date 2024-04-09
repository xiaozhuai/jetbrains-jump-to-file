package io.github.xiaozhuai;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class OneWayPsiFileReference extends OneWayPsiFileReferenceBase<PsiElement> {
    public interface ValueParser {
        String getValue(Class<PsiElement> clazz, ClassLoader classLoader, PsiElement psiElement);
    }

    private final Class<PsiElement> clazz;
    private final ClassLoader classLoader;
    private final ValueParser valueParser;

    public OneWayPsiFileReference(PsiElement psiElement, Class<PsiElement> clazz, ClassLoader classLoader, ValueParser valueParser) {
        super(psiElement);
        this.clazz = clazz;
        this.classLoader = classLoader;
        this.valueParser = valueParser;
    }

    @NotNull
    @Override
    protected String computeStringValue() {
        String text = valueParser.getValue(clazz, classLoader, getElement());
        text = removeScheme(text);
        return text;
    }

    private String removeScheme(String text) {
        if (text.startsWith("qrc:///")) {
            text = text.substring(7);
        } else if (text.contains("://")) {
            text = text.substring(text.indexOf("://") + 3);
        } else if (text.startsWith(":/")) {
            text = text.substring(2);
        }
        return text;
    }
}