package io.github.xiaozhuai;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class FileReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // Java & groovy
        registerReferenceProvider(registrar, "com.intellij.psi.PsiLiteral", "com.intellij.java", (clazz, classLoader, psiElement) -> {
            try {
                Method getValue = clazz.getMethod("getValue");
                return (String) getValue.invoke(psiElement);
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // kotlin
        registerReferenceProvider(registrar, "org.jetbrains.kotlin.psi.KtStringTemplateExpression", "org.jetbrains.kotlin", (clazz, classLoader, psiElement) -> {
            try {
                Class<?> escaperClazz = Class.forName("org.jetbrains.kotlin.psi.KotlinStringLiteralTextEscaper", true, classLoader);
                Method createLiteralTextEscaper = clazz.getMethod("createLiteralTextEscaper");
                Object escaper = createLiteralTextEscaper.invoke(psiElement);
                Method getRelevantTextRange = escaperClazz.getMethod("getRelevantTextRange");
                Method decode = escaperClazz.getMethod("decode", TextRange.class, StringBuilder.class);
                TextRange range = (TextRange) getRelevantTextRange.invoke(escaper);
                StringBuilder builder = new StringBuilder();
                decode.invoke(escaper, range, builder);
                return builder.toString();
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // C/C++
        // registerReferenceProvider(registrar, "com.jetbrains.cidr.lang.psi.OCLiteralExpression", "com.jetbrains.cidr.lang", (clazz, classLoader, psiElement) -> {
        //     try {
        //         Method getValue = clazz.getMethod("getValue");
        //         return (String) getValue.invoke(psiElement);
        //     } catch (Exception e) {
        //         System.out.println("!!! Error GetValue: " + e.getMessage());
        //         return "";
        //     }
        // });
        registerReferenceProvider(registrar, "com.jetbrains.cidr.lang.psi.OCLiteralExpression", "com.intellij.modules.cidr.lang", (clazz, classLoader, psiElement) -> {
            try {
                Method getUnescapedLiteralText = clazz.getMethod("getUnescapedLiteralText");
                return (String) getUnescapedLiteralText.invoke(psiElement);
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // JavaScript
        registerReferenceProvider(registrar, "com.intellij.lang.javascript.psi.JSLiteralExpression", "JavaScript", (clazz, classLoader, psiElement) -> {
            try {
                Method getValue = clazz.getMethod("getValue");
                return (String) getValue.invoke(psiElement);
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // PHP
        registerReferenceProvider(registrar, "com.jetbrains.php.lang.psi.elements.StringLiteralExpression", "com.jetbrains.php", (clazz, classLoader, psiElement) -> {
            try {
                Method getContents = clazz.getMethod("getContents");
                return (String) getContents.invoke(psiElement);
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // Python
        // registerReferenceProvider(registrar, "com.jetbrains.python.psi.StringLiteralExpression", "Pythonid", (clazz, classLoader, psiElement) -> {
        //     try {
        //         Method getStringValue = clazz.getMethod("getStringValue");
        //         return (String) getStringValue.invoke(psiElement);
        //     } catch (Exception e) {
        //         System.out.println("!!! Error GetValue: " + e.getMessage());
        //         return "";
        //     }
        // });
        registerReferenceProvider(registrar, "com.jetbrains.python.psi.StringLiteralExpression", "com.intellij.modules.python", (clazz, classLoader, psiElement) -> {
            try {
                Method getStringValue = clazz.getMethod("getStringValue");
                return (String) getStringValue.invoke(psiElement);
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // Dart
        registerReferenceProvider(registrar, "com.jetbrains.lang.dart.psi.DartStringLiteralExpression", "Dart", (clazz, classLoader, psiElement) -> {
            try {
                String text = psiElement.getText();
                if ((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) {
                    return text.substring(1, text.length() - 1);
                }
                return "";
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // golang
        registerReferenceProvider(registrar, "com.goide.psi.GoStringLiteral", "org.jetbrains.plugins.go", (clazz, classLoader, psiElement) -> {
            try {
                Method getDecodedText = clazz.getMethod("getDecodedText");
                return (String) getDecodedText.invoke(psiElement);
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // clojure via cursive
        registerReferenceProvider(registrar, "cursive.psi.impl.ClStringLiteral", "com.cursiveclojure.cursive", (clazz, classLoader, psiElement) -> {
            try {
                Method getValue = clazz.getMethod("getValue");
                return (String) getValue.invoke(psiElement);
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // ruby
        registerReferenceProvider(registrar, "org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.RStringLiteralBase", "org.jetbrains.plugins.ruby", (clazz, classLoader, psiElement) -> {
            try {
                Method getContent = clazz.getMethod("getContent");
                return (String) getContent.invoke(psiElement);
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // Rust
        registerReferenceProvider(registrar, "org.rust.lang.core.psi.RsLitExpr", "com.jetbrains.rust", (clazz, classLoader, psiElement) -> {
            try {
                String text = psiElement.getText();
                if (text.startsWith("\"") && text.endsWith("\"")) {
                    return text.substring(1, text.length() - 1);
                }
                return "";
            } catch (Exception e) {
                System.out.println("!!! Error GetValue: " + e.getMessage());
                return "";
            }
        });

        // swift
        // c#
        // perl
    }

    @SuppressWarnings("unchecked")
    private void registerReferenceProvider(PsiReferenceRegistrar registrar,
                                           String className,
                                           String pluginId,
                                           OneWayPsiFileReference.ValueParser valueParser) {
        try {
            IdeaPluginDescriptor plugin = pluginId.isEmpty() ? null : PluginManagerCore.getPlugin(PluginId.getId(pluginId));
            ClassLoader classLoader = plugin == null ? getClass().getClassLoader() : plugin.getPluginClassLoader();
            Class<PsiElement> clazz = (Class<PsiElement>) Class.forName(className, true, classLoader);
            registrar.registerReferenceProvider(StandardPatterns.instanceOf(clazz), new PsiReferenceProvider() {
                @Override
                public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                    return new PsiReference[]{new OneWayPsiFileReference(element, clazz, classLoader, valueParser)};
                }
            });
        } catch (ClassNotFoundException e) {
            // ignore
            System.out.println("!!! Class not found: " + className);
        }
    }
}
