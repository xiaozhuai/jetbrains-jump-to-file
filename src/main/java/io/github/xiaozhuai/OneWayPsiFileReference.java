package io.github.xiaozhuai;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class OneWayPsiFileReference extends OneWayPsiFileReferenceBase<PsiElement> {

    public static class Options {
        public boolean removeTripleSingleQuotes = false;
        public boolean removeQuotes = true;
        public boolean removeSingleQuotes = false;
        public boolean removeBacktick = false;
        public boolean removeCppHereDoc = false;
    }

    private final Options options;

    public OneWayPsiFileReference(PsiElement psiElement, Options options) {
        super(psiElement);
        this.options = options;
    }

    @NotNull
    @Override
    protected String computeStringValue() {
        String text = getElement().getText();
        if (options.removeTripleSingleQuotes) {
            text = removeTripleSingleQuotes(text);
        }
        if (options.removeQuotes) {
            text = removeQuotes(text);
        }
        if (options.removeSingleQuotes) {
            text = removeSingleQuotes(text);
        }
        if (options.removeBacktick) {
            text = removeBacktick(text);
        }
        if (options.removeCppHereDoc) {
            text = removeCppHereDoc(text);
        }
        text = removeScheme(text);
        return text;
    }

    private String removeTripleSingleQuotes(String text) {
        if (text.length() >= 6 && (text.startsWith("'''") && text.endsWith("'''"))) {
            text = text.substring(3, text.length() - 3);
        }
        return text;
    }

    private String removeQuotes(String text) {
        if (text.length() >= 2 && (text.startsWith("\"") && text.endsWith("\""))) {
            text = text.substring(1, text.length() - 1);
        }
        return text;
    }

    private String removeSingleQuotes(String text) {
        if (text.length() >= 2 && (text.startsWith("'") && text.endsWith("'"))) {
            text = text.substring(1, text.length() - 1);
        }
        return text;
    }

    private String removeBacktick(String text) {
        if (text.length() >= 2 && (text.startsWith("`") && text.endsWith("`"))) {
            text = text.substring(1, text.length() - 1);
        }
        return text;
    }

    private String removeCppHereDoc(String text) {
        String newText = text;
        if (newText.length() >= 2 && (newText.startsWith("R\"") && newText.endsWith("\""))) {
            newText = newText.substring(2, newText.length() - 1);
        }
        int p0 = newText.indexOf('(');
        int p1 = newText.lastIndexOf(')');
        if (p0 != -1 && p1 != -1 && p0 < p1) {
            String begTag = newText.substring(0, p0);
            String endTag = newText.substring(p1 + 1);
            if (begTag.equals(endTag)) {
                newText = newText.substring(p0 + 1, p1);
                return newText;
            }
        }
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