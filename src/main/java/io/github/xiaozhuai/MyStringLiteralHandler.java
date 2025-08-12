package io.github.xiaozhuai;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.lang.reflect.Method;
import java.util.Objects;

public abstract class MyStringLiteralHandler {
    private final String className;
    private final Class<PsiElement> clazz;

    public Class<?> findClassInAnyPlugin(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
            // System.out.printf("### Found class %s in current classloader\n", className);
            return clazz;
        } catch (ClassNotFoundException ignored) {

        }
        for (IdeaPluginDescriptor plugin : PluginManagerCore.getLoadedPlugins()) {
            try {
                clazz = Class.forName(className, true, plugin.getPluginClassLoader());
                // System.out.printf("### Found class %s in plugin %s\n", className, plugin.getName());
                return clazz;
            } catch (ClassNotFoundException ignored) {
            }
        }
        try {
            clazz = Class.forName(className, true, ClassLoader.getSystemClassLoader());
            // System.out.printf("### Found class %s in system classloader\n", className);
            return clazz;
        } catch (ClassNotFoundException ignored) {
        }
        System.out.printf("!!! Class %s not found\n", className);
        return null;
    }

    @SuppressWarnings("unchecked")
    public MyStringLiteralHandler(String className) {
        this.className = className;
        this.clazz = (Class<PsiElement>) findClassInAnyPlugin(className);
    }

    public final String getClassName() {
        return className;
    }

    public final Class<PsiElement> getClazz() {
        return clazz;
    }

    public final String getFinalValue(PsiElement psiElement) {
        String text = getValue(psiElement);
        if (text == null) return "";
        text = removeScheme(text);
        return text;
    }

    protected abstract String getValue(PsiElement psiElement);

    private static String removeScheme(String text) {
        if (text.startsWith("qrc:///")) {
            text = text.substring(7);
        } else if (text.contains("://")) {
            text = text.substring(text.indexOf("://") + 3);
        } else if (text.startsWith(":/")) {
            text = text.substring(2);
        }
        return text;
    }

    private static MyStringLiteralHandler[] handlers = null;

    public static MyStringLiteralHandler[] getAllHandlers() {
        if (handlers == null) {
            handlers = new MyStringLiteralHandler[]{
                    // Java & groovy
                    new MyStringLiteralHandler("com.intellij.psi.PsiLiteral") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getValue = this.getClazz().getMethod("getValue");
                                return (String) getValue.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // kotlin
                    new MyStringLiteralHandler("org.jetbrains.kotlin.psi.KtStringTemplateExpression") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Class<?> escaperClazz = this.findClassInAnyPlugin("org.jetbrains.kotlin.psi.KotlinStringLiteralTextEscaper");
                                Method createLiteralTextEscaper = this.getClazz().getMethod("createLiteralTextEscaper");
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
                        }
                    },

                    // C/C++
                    new MyStringLiteralHandler("com.jetbrains.cidr.lang.psi.OCStringLiteralExpression") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getUnescapedLiteralText = this.getClazz().getMethod("getUnescapedLiteralText");
                                return (String) getUnescapedLiteralText.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // C/C++ Clion Nova
                    new MyStringLiteralHandler("com.jetbrains.rider.cpp.fileType.psi.CppStringLiteralExpression") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Class<?> escaperClazz = this.findClassInAnyPlugin("com.jetbrains.rider.cpp.fileType.psi.impl.CppLiteralExpressionTextEscaper");
                                Method createLiteralTextEscaper = this.getClazz().getMethod("createLiteralTextEscaper");
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
                        }
                    },

                    // JavaScript
                    new MyStringLiteralHandler("com.intellij.lang.javascript.psi.JSLiteralExpression") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getValue = this.getClazz().getMethod("getValue");
                                return (String) getValue.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // PHP
                    new MyStringLiteralHandler("com.jetbrains.php.lang.psi.elements.StringLiteralExpression") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getContents = this.getClazz().getMethod("getContents");
                                return (String) getContents.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Python
                    new MyStringLiteralHandler("com.jetbrains.python.psi.StringLiteralExpression") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getStringValue = this.getClazz().getMethod("getStringValue");
                                return (String) getStringValue.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Dart
                    new MyStringLiteralHandler("com.jetbrains.lang.dart.psi.DartStringLiteralExpression") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                // TODO Unescape
                                String text = psiElement.getText();
                                if ((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) {
                                    text = text.substring(1, text.length() - 1);
                                    text = text.replace("\\\\", "\\");
                                    return text;
                                } else {
                                    return "";
                                }
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Go
                    new MyStringLiteralHandler("com.goide.psi.GoStringLiteral") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getDecodedText = this.getClazz().getMethod("getDecodedText");
                                return (String) getDecodedText.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Clojure via Cursive
                    new MyStringLiteralHandler("cursive.psi.impl.ClStringLiteral") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getValue = this.getClazz().getMethod("getValue");
                                return (String) getValue.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Ruby
                    new MyStringLiteralHandler("org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Method getContent = this.getClazz().getMethod("getContentValue");
                                return (String) getContent.invoke(psiElement);
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // Rust
                    new MyStringLiteralHandler("org.rust.lang.core.psi.RsLitExpr") {
                        @Override
                        protected String getValue(PsiElement psiElement) {
                            try {
                                Class<?> rsLitExprKtClazz = this.findClassInAnyPlugin("org.rust.lang.core.psi.ext.RsLitExprKt");
                                Method getStringValue = rsLitExprKtClazz.getMethod("getStringValue", this.getClazz());
                                String text = (String) getStringValue.invoke(null, psiElement);
                                return Objects.requireNonNullElse(text, "");
                            } catch (Exception e) {
                                System.out.println("!!! Error GetValue: " + e.getMessage());
                                return "";
                            }
                        }
                    },

                    // TODO
                    // C#
            };
        }
        return handlers;
    }
}
